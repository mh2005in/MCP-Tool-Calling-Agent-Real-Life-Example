package com.immiauto.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class AiBoundaryService {

    private static final List<Pattern> PROHIBITED_PATTERNS = List.of(
            Pattern.compile("(?i)\\b(you are eligible|you qualify|you will be approved)\\b"),
            Pattern.compile("(?i)\\b(approval (chance|probability|likelihood|rate))\\b"),
            Pattern.compile("(?i)\\b(refusal risk is (low|medium|high|minimal))\\b"),
            Pattern.compile("(?i)\\b(i recommend (applying|choosing|selecting) .*(program|stream|category))\\b"),
            Pattern.compile("(?i)\\b(your (NOC|TEER) (code |classification )?(is|should be))\\b"),
            Pattern.compile("(?i)\\b(you (should|can) start working)\\b"),
            Pattern.compile("(?i)\\b(the relationship (is|appears|seems) genuine)\\b"),
            Pattern.compile("(?i)\\b(employer (qualifies|meets|satisfies) (the |LMIA ))\\b"),
            Pattern.compile("(?i)\\b(income threshold (is met|satisfied))\\b"),
            Pattern.compile("(?i)\\b(guarantee.{0,20}(approval|outcome|result|success))\\b")
    );

    private static final String VIOLATION_REPLACEMENT =
            "[This content was filtered because it may constitute immigration advice. "
            + "Please consult with your RCIC for professional guidance.]";

    public AiBoundaryResult validateAndSanitize(String aiOutput) {
        if (aiOutput == null || aiOutput.isBlank()) {
            return new AiBoundaryResult(aiOutput, false, List.of());
        }

        List<String> violations = PROHIBITED_PATTERNS.stream()
                .filter(p -> p.matcher(aiOutput).find())
                .map(p -> p.pattern())
                .toList();

        if (violations.isEmpty()) {
            return new AiBoundaryResult(aiOutput, false, violations);
        }

        String sanitized = aiOutput;
        for (Pattern p : PROHIBITED_PATTERNS) {
            sanitized = p.matcher(sanitized).replaceAll(VIOLATION_REPLACEMENT);
        }

        return new AiBoundaryResult(sanitized, true, violations);
    }

    public record AiBoundaryResult(
            String output,
            boolean hadViolations,
            List<String> violatedPatterns
    ) {}
}
