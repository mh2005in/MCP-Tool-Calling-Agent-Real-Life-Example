package com.immiauto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentClassificationService {

    private static final Map<Pattern, String[]> CLASSIFICATION_RULES = new LinkedHashMap<>();

    static {
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)passport"), new String[]{"Identity", "Passport"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)birth.?cert"), new String[]{"Identity", "Birth Certificate"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(ielts|celpip|tef|tcf)"), new String[]{"Language", "Language Test Results"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(transcript|diploma|degree)"), new String[]{"Education", "Educational Credential"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(eca|wes|iqas|credential.?assessment)"), new String[]{"Education", "ECA Report"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)bank.?statement"), new String[]{"Financial", "Bank Statement"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(noa|notice.?of.?assessment)"), new String[]{"Financial", "Notice of Assessment"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(t4|pay.?stub|pay.?slip)"), new String[]{"Financial", "Pay Stub / T4"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)police.?(cert|clear)"), new String[]{"Police Certificates", "Police Certificate"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(medical|ime|panel.?physician)"), new String[]{"Medical", "Medical Exam"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)marriage.?cert"), new String[]{"Civil Status", "Marriage Certificate"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)divorce"), new String[]{"Civil Status", "Divorce Decree"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(pr.?card|permanent.?resid)"), new String[]{"PR Proof", "PR Card"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(copr|confirmation.?of.?perm)"), new String[]{"PR Proof", "COPR"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(lmia|labour.?market)"), new String[]{"Job/Employer", "LMIA"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(job.?offer|employment.?offer|offer.?letter)"), new String[]{"Job/Employer", "Job Offer Letter"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(reference.?letter|employment.?letter)"), new String[]{"Work Experience", "Reference Letter"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(resum[eé]|cv|curriculum)"), new String[]{"Qualifications", "Resume / CV"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(photo|passport.?photo)"), new String[]{"Photos", "Passport Photo"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(imm.?5476|representative)"), new String[]{"Representative", "Use of Representative Form"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)(insurance|medical.?insurance)"), new String[]{"Medical Insurance", "Insurance Policy"});
        CLASSIFICATION_RULES.put(Pattern.compile("(?i)invitation"), new String[]{"Host Documents", "Invitation Letter"});
    }

    public String[] classifyByFileName(String fileName) {
        if (fileName == null) return new String[]{"Uncategorized", "Unknown"};
        for (Map.Entry<Pattern, String[]> rule : CLASSIFICATION_RULES.entrySet()) {
            if (rule.getKey().matcher(fileName).find()) {
                return rule.getValue();
            }
        }
        return new String[]{"Uncategorized", "Unknown"};
    }

    public Map<String, String> suggestClassification(String fileName) {
        String[] result = classifyByFileName(fileName);
        Map<String, String> suggestion = new LinkedHashMap<>();
        suggestion.put("category", result[0]);
        suggestion.put("type", result[1]);
        suggestion.put("confidence", result[0].equals("Uncategorized") ? "LOW" : "HIGH");
        return suggestion;
    }
}
