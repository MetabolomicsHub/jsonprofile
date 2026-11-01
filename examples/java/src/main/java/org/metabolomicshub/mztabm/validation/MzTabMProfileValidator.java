package org.metabolomicshub.mztabm.validation;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;

public class MzTabMProfileValidator {
    public static void main(String[] args) {
        Path mztabDir = Path.of("../../shared/resources/mztab-m");
        Path profilesDir = mztabDir.resolve("profiles");
        Path schemaPath = mztabDir.resolve("mztabm-2.1.0-M.schema.json");
        Path inputJsonPath = mztabDir.resolve("manual_null_MTBLS263.mztab.json");

        String defaultId = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json";
        String metabolightsId = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json";

        Map<String, Object> referenced = Map.of(
                defaultId, profilesDir.resolve("mztabm-default-profile-2.1.0-M.json"));

        ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
        rt.setOfflineMode(false);
        rt.setSkipJsonschemaValidation(true);

        Path profilePath = profilesDir.resolve("mztabm-metabolights-profile-2.1.0-M.json");
        JsonValidator validator = new JsonValidator(schemaPath, profilePath, referenced);
        JsonValidationResult res = validator.validateJsonFile(inputJsonPath, rt);

        System.out.printf("=== %s (%s) ===%n", validator.getJsonProfile().getName(), metabolightsId);
        System.out.printf("Errors (%d paths):%n", res.getErrors().size());
        for (var entry : new TreeMap<>(res.getErrors()).entrySet()) {
            for (JsonProfileMessage msg : entry.getValue()) {
                System.out.printf("  [REQUIRED]    %s\t%s\t%s%n",
                        msg.getCode(), entry.getKey(), msg.getMessage());
            }
        }
        System.out.printf("Recommendations (%d paths):%n", res.getRecommendations().size());
        for (var entry : new TreeMap<>(res.getRecommendations()).entrySet()) {
            for (JsonProfileMessage msg : entry.getValue()) {
                System.out.printf("  [RECOMMENDED] %s\t%s\t%s%n",
                        msg.getCode(), entry.getKey(), msg.getMessage());
            }
        }
        System.out.printf("Optionals (%d paths):%n", res.getOptionals().size());
        for (var entry : new TreeMap<>(res.getOptionals()).entrySet()) {
            for (JsonProfileMessage msg : entry.getValue()) {
                System.out.printf("  [OPTIONAL]    %s\t%s\t%s%n",
                        msg.getCode(), entry.getKey(), msg.getMessage());
            }
        }
    }
}
