package com.company.incident_triage.service;

import com.company.incident_triage.model.Incident;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AiTriageService {

    private static final Map<String, Incident.Severity> SEVERITY_KEYWORDS = Map.ofEntries(
        Map.entry("critical", Incident.Severity.CRITICAL),
        Map.entry("down", Incident.Severity.CRITICAL),
        Map.entry("outage", Incident.Severity.CRITICAL),
        Map.entry("emergency", Incident.Severity.CRITICAL),
        Map.entry("production down", Incident.Severity.CRITICAL),
        Map.entry("system failure", Incident.Severity.CRITICAL),
        Map.entry("major", Incident.Severity.HIGH),
        Map.entry("urgent", Incident.Severity.HIGH),
        Map.entry("blocking", Incident.Severity.HIGH),
        Map.entry("cannot access", Incident.Severity.HIGH),
        Map.entry("widespread", Incident.Severity.HIGH),
        Map.entry("slow", Incident.Severity.MEDIUM),
        Map.entry("degraded", Incident.Severity.MEDIUM),
        Map.entry("intermittent", Incident.Severity.MEDIUM),
        Map.entry("some users", Incident.Severity.MEDIUM),
        Map.entry("performance", Incident.Severity.MEDIUM)
    );

    private static final Map<String, Incident.Category> CATEGORY_KEYWORDS = Map.ofEntries(
        Map.entry("database", Incident.Category.DATABASE),
        Map.entry("db", Incident.Category.DATABASE),
        Map.entry("sql", Incident.Category.DATABASE),
        Map.entry("connection", Incident.Category.DATABASE),
        Map.entry("query", Incident.Category.DATABASE),
        Map.entry("network", Incident.Category.NETWORK),
        Map.entry("connectivity", Incident.Category.NETWORK),
        Map.entry("timeout", Incident.Category.NETWORK),
        Map.entry("dns", Incident.Category.NETWORK),
        Map.entry("firewall", Incident.Category.NETWORK),
        Map.entry("security", Incident.Category.SECURITY),
        Map.entry("breach", Incident.Category.SECURITY),
        Map.entry("attack", Incident.Category.SECURITY),
        Map.entry("unauthorized", Incident.Category.SECURITY),
        Map.entry("malware", Incident.Category.SECURITY),
        Map.entry("ui", Incident.Category.FRONTEND),
        Map.entry("frontend", Incident.Category.FRONTEND),
        Map.entry("display", Incident.Category.FRONTEND),
        Map.entry("layout", Incident.Category.FRONTEND),
        Map.entry("browser", Incident.Category.FRONTEND),
        Map.entry("hardware", Incident.Category.HARDWARE),
        Map.entry("server", Incident.Category.HARDWARE),
        Map.entry("disk", Incident.Category.HARDWARE),
        Map.entry("memory", Incident.Category.HARDWARE),
        Map.entry("cpu", Incident.Category.HARDWARE)
    );

    private static final Map<Incident.Category, Map<Incident.Severity, String>> ACTION_TEMPLATES = Map.of(
        Incident.Category.DATABASE, Map.of(
            Incident.Severity.CRITICAL, "Immediately check database server status, restart if necessary, and verify connection pools",
            Incident.Severity.HIGH, "Check database connection pool configuration and query performance",
            Incident.Severity.MEDIUM, "Monitor database performance metrics and optimize slow queries",
            Incident.Severity.LOW, "Review database maintenance schedules and update documentation"
        ),
        Incident.Category.NETWORK, Map.of(
            Incident.Severity.CRITICAL, "Immediately check network infrastructure, routers, and switches",
            Incident.Severity.HIGH, "Investigate network connectivity and DNS resolution issues",
            Incident.Severity.MEDIUM, "Monitor network latency and bandwidth utilization",
            Incident.Severity.LOW, "Schedule network performance review and capacity planning"
        ),
        Incident.Category.SECURITY, Map.of(
            Incident.Severity.CRITICAL, "URGENT: Isolate affected systems, review security logs, and activate incident response team",
            Incident.Severity.HIGH, "Review security logs, check for unauthorized access, and update security policies",
            Incident.Severity.MEDIUM, "Investigate potential security threats and update monitoring rules",
            Incident.Severity.LOW, "Review security configurations and update access controls"
        ),
        Incident.Category.FRONTEND, Map.of(
            Incident.Severity.CRITICAL, "Check frontend application servers and content delivery networks",
            Incident.Severity.HIGH, "Investigate frontend performance issues and resource loading problems",
            Incident.Severity.MEDIUM, "Analyze frontend performance metrics and optimize user experience",
            Incident.Severity.LOW, "Review frontend code for potential improvements and optimizations"
        ),
        Incident.Category.HARDWARE, Map.of(
            Incident.Severity.CRITICAL, "Immediately check hardware status, replace failed components, and verify system health",
            Incident.Severity.HIGH, "Investigate hardware performance issues and check system logs",
            Incident.Severity.MEDIUM, "Monitor hardware metrics and schedule maintenance if needed",
            Incident.Severity.LOW, "Review hardware monitoring data and plan for capacity upgrades"
        ),
        Incident.Category.SOFTWARE, Map.of(
            Incident.Severity.CRITICAL, "Check application servers, restart services, and review error logs",
            Incident.Severity.HIGH, "Investigate software errors and performance bottlenecks",
            Incident.Severity.MEDIUM, "Analyze application logs and optimize software configuration",
            Incident.Severity.LOW, "Review software updates and plan for maintenance windows"
        )
    );

    public AiTriageResult analyzeIncident(String title, String description, String affectedService) {
        String content = (title + " " + description + " " + affectedService).toLowerCase();
        
        Incident.Severity severity = determineSeverity(content);
        Incident.Category category = determineCategory(content);
        String suggestedAction = generateSuggestedAction(severity, category, content);
        BigDecimal confidence = calculateConfidence(content, severity, category);
        
        return new AiTriageResult(severity, category, suggestedAction, confidence);
    }

    private Incident.Severity determineSeverity(String content) {
        int criticalScore = 0;
        int highScore = 0;
        int mediumScore = 0;

        for (Map.Entry<String, Incident.Severity> entry : SEVERITY_KEYWORDS.entrySet()) {
            if (content.contains(entry.getKey())) {
                switch (entry.getValue()) {
                    case CRITICAL -> criticalScore += 3;
                    case HIGH -> highScore += 2;
                    case MEDIUM -> mediumScore += 1;
                }
            }
        }

        // Additional pattern matching
        if (Pattern.compile("\\b(all|entire|complete)\\s+(system|service|application)\\s+(down|failed|unavailable)\\b")
                .matcher(content).find()) {
            criticalScore += 5;
        }

        if (Pattern.compile("\\b\\d+%\\s+(of\\s+)?(users|customers)\\s+(affected|impacted)\\b")
                .matcher(content).find()) {
            highScore += 3;
        }

        // Determine severity based on scores
        if (criticalScore > 0) return Incident.Severity.CRITICAL;
        if (highScore > 2) return Incident.Severity.HIGH;
        if (mediumScore > 1 || highScore > 0) return Incident.Severity.MEDIUM;
        return Incident.Severity.LOW;
    }

    private Incident.Category determineCategory(String content) {
        Map<Incident.Category, Integer> categoryScores = new HashMap<>();

        for (Map.Entry<String, Incident.Category> entry : CATEGORY_KEYWORDS.entrySet()) {
            if (content.contains(entry.getKey())) {
                categoryScores.merge(entry.getValue(), 1, Integer::sum);
            }
        }

        // Find category with highest score
        return categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Incident.Category.SOFTWARE);
    }

    private String generateSuggestedAction(Incident.Severity severity, Incident.Category category, String content) {
        return ACTION_TEMPLATES
                .getOrDefault(category, ACTION_TEMPLATES.get(Incident.Category.SOFTWARE))
                .get(severity);
    }

    private BigDecimal calculateConfidence(String content, Incident.Severity severity, Incident.Category category) {
        double baseConfidence = 0.7;
        
        // Increase confidence based on keyword matches
        long keywordMatches = SEVERITY_KEYWORDS.keySet().stream()
                .mapToLong(keyword -> content.contains(keyword) ? 1 : 0)
                .sum();
        
        long categoryMatches = CATEGORY_KEYWORDS.keySet().stream()
                .mapToLong(keyword -> content.contains(keyword) ? 1 : 0)
                .sum();

        // Adjust confidence based on matches and content length
        double confidenceAdjustment = (keywordMatches * 0.05) + (categoryMatches * 0.03);
        double lengthFactor = Math.min(content.length() / 100.0, 1.0) * 0.1;
        
        double finalConfidence = Math.min(baseConfidence + confidenceAdjustment + lengthFactor, 0.99);
        
        return BigDecimal.valueOf(finalConfidence).setScale(2, RoundingMode.HALF_UP);
    }

    public static class AiTriageResult {
        private final Incident.Severity severity;
        private final Incident.Category category;
        private final String suggestedAction;
        private final BigDecimal confidence;

        public AiTriageResult(Incident.Severity severity, Incident.Category category, 
                             String suggestedAction, BigDecimal confidence) {
            this.severity = severity;
            this.category = category;
            this.suggestedAction = suggestedAction;
            this.confidence = confidence;
        }

        // Getters
        public Incident.Severity getSeverity() { return severity; }
        public Incident.Category getCategory() { return category; }
        public String getSuggestedAction() { return suggestedAction; }
        public BigDecimal getConfidence() { return confidence; }
    }
}