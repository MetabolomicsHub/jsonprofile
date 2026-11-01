from pathlib import Path
from typing import Dict, Union

from jsonprofile.validator import JsonValidator


def test_create_markdown_file_for_isa_json_and_mztabm(tmp_path: Path) -> None:
    isa_base_id = "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json"
    isa_ext_id = (
        "https://github.com/ISA-tools/isa-api/"
        "metabolights-bh-extended-profile-0.1.0.json"
    )
    isa_refs: Dict[str, Union[str, Path]] = {
        isa_base_id: (
            "../shared/resources/isa-json/profiles/metabolights-profile-0.1.0.json"
        ),
        isa_ext_id: (
            "../shared/resources/isa-json/profiles/"
            "metabolights-bh-extended-profile-0.1.0.json"
        ),
    }

    isa_validator = JsonValidator(
        json_schema=None,
        profile=isa_refs[isa_ext_id],
        referenced_profiles=dict(isa_refs),
    )
    isa_md_path = tmp_path / "isa_extended_profile.md"
    isa_md = isa_validator.create_markdown_file(isa_md_path)

    assert isa_md_path.exists()
    assert isa_md_path.read_text(encoding="utf-8") == isa_md
    assert "# MetaboLights BH Extended ISA-JSON Profile" in isa_md
    assert f"| **ID** | `{isa_ext_id}` |" in isa_md
    assert f"| **Extends** | `{isa_base_id}` |" in isa_md
    assert "| `$.studies[0].identifier` | `BH-00304` | `required`" in isa_md
    assert "### `$.studies[0].protocols[*]`" in isa_md
    assert "#### `M-000402`" in isa_md
    assert (
        "allowed_cv_terms=[{cv_label=`NCIT`, "
        "cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, "
        "name=`Organism Part`}]"
    ) in isa_md
    assert "allowed_cv_terms=[{name=`disease`}]" in isa_md
    assert (
        "{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0002927`, "
        "name=`NMR sample preparation`}"
    ) in isa_md

    mztab_default_id = (
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/"
        "mztabm-default-profile-2.1.0-M.json"
    )
    mztab_validator = JsonValidator(
        json_schema="../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json",
        profile=(
            "../shared/resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json"
        ),
        referenced_profiles={
            mztab_default_id: (
                "../shared/resources/mztab-m/profiles/"
                "mztabm-default-profile-2.1.0-M.json"
            )
        },
    )
    mztab_md_path = tmp_path / "mztabm_default_profile.md"
    mztab_md = mztab_validator.create_markdown_file(mztab_md_path)

    assert mztab_md_path.exists()
    assert "# mzTab-M 2.1.0-M Default Profile" in mztab_md
    assert "| `$` | `D-0010` | `required` | `opa-field-requirement`" in mztab_md
    assert "policy_id=`policy_d_0010`" in mztab_md
    assert "| `$.smallMoleculeFeature[*].charge` | `D-SMF-CHARGE-0001`" in mztab_md
