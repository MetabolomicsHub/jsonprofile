use crate::profile::base::CvTerm;
use serde_json::Value;
use std::collections::HashMap;
use std::sync::RwLock;
use std::time::Duration;

pub trait CvTermSearch: Send + Sync {
    fn get_iri(&self, cv_term: &CvTerm) -> Option<String>;
    fn get_curie(&self, cv_term: &CvTerm) -> Option<String>;
    fn find_cv_term(
        &self,
        source: &str,
        accession_or_label: &str,
        matched_accession: Option<&str>,
        allow_synonym_search: bool,
    ) -> Option<CvTerm>;
    fn check_cv_term(
        &self,
        cv_term: &CvTerm,
        parent_cv_term: Option<&CvTerm>,
        allow_synonym_search: bool,
    ) -> (bool, String);
}

#[derive(Default)]
pub struct OlsCvTermSearch {
    search_cache: RwLock<HashMap<String, (bool, String)>>,
    cv_term_cache: RwLock<HashMap<String, CvTerm>>,
}

impl OlsCvTermSearch {
    pub fn new() -> Self {
        Self::default()
    }
}

impl CvTermSearch for OlsCvTermSearch {
    fn get_curie(&self, cv_term: &CvTerm) -> Option<String> {
        if cv_term.cv_accession.is_empty() {
            return None;
        }
        if let Some(tail) = cv_term
            .cv_accession
            .strip_prefix("http://purl.obolibrary.org/obo/")
        {
            return Some(tail.replace('_', ":"));
        }
        Some(cv_term.cv_accession.clone())
    }

    fn get_iri(&self, cv_term: &CvTerm) -> Option<String> {
        if cv_term.cv_accession.is_empty() {
            return None;
        }
        if cv_term.cv_accession.starts_with("http://")
            || cv_term.cv_accession.starts_with("https://")
        {
            return Some(cv_term.cv_accession.clone());
        }
        if let Some((prefix, id)) = cv_term.cv_accession.split_once(':') {
            return Some(format!("http://purl.obolibrary.org/obo/{prefix}_{id}"));
        }
        None
    }

    fn find_cv_term(
        &self,
        source: &str,
        accession_or_label: &str,
        matched_accession: Option<&str>,
        allow_synonym_search: bool,
    ) -> Option<CvTerm> {
        let key = format!(
            "{source}|{accession_or_label}|{}|{allow_synonym_search}",
            matched_accession.unwrap_or("")
        );
        if let Ok(guard) = self.cv_term_cache.read() {
            if let Some(found) = guard.get(&key) {
                return Some(found.clone());
            }
        }

        let client = reqwest::blocking::Client::builder()
            .timeout(Duration::from_secs(10))
            .build()
            .ok()?;

        let q = matched_accession
            .unwrap_or(accession_or_label)
            .to_lowercase();
        let ont = source.to_lowercase();
        let resp = client
            .get("https://www.ebi.ac.uk/ols4/api/search")
            .query(&[
                ("q", q.as_str()),
                ("ontology", ont.as_str()),
                ("type", "class,property,individual"),
                ("exact", "true"),
                ("format", "json"),
                ("start", "0"),
                ("rows", "1"),
            ])
            .send()
            .ok()?;
        if !resp.status().is_success() {
            return None;
        }
        let body: Value = resp.json().ok()?;
        let docs = body.get("response")?.get("docs")?.as_array()?;
        let first = docs.first()?;
        let label = first.get("label").and_then(|v| v.as_str()).unwrap_or("");
        let term = CvTerm::new(source, accession_or_label, label, "");
        if let Ok(mut guard) = self.cv_term_cache.write() {
            guard.insert(key, term.clone());
        }
        Some(term)
    }

    fn check_cv_term(
        &self,
        cv_term: &CvTerm,
        parent_cv_term: Option<&CvTerm>,
        allow_synonym_search: bool,
    ) -> (bool, String) {
        if cv_term.cv_accession.is_empty() || cv_term.name.is_empty() || cv_term.cv_label.is_empty()
        {
            return (false, format!("Invalid cv term {cv_term}"));
        }
        if parent_cv_term.is_none() {
            if self
                .find_cv_term(
                    &cv_term.cv_label,
                    &cv_term.cv_accession,
                    None,
                    allow_synonym_search,
                )
                .is_some()
            {
                return (true, String::new());
            }
            return (false, format!("CV term '{cv_term}' is not found"));
        }
        let key = format!("{cv_term}|{parent_cv_term:?}|{allow_synonym_search}");
        if let Ok(guard) = self.search_cache.read() {
            if let Some(found) = guard.get(&key) {
                return found.clone();
            }
        }
        (false, format!("{} does not match", cv_term.cv_accession))
    }
}
