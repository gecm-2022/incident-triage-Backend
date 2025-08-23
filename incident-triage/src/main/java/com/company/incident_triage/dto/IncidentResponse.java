package com.company.incident_triage.dto;

import com.company.incident_triage.model.Incident;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IncidentResponse {
    private Long id;
    private String title;
    private String description;
    private String affectedService;
    private String aiSeverity;
    private String aiCategory;
    private String aiSuggestedAction;
    private BigDecimal confidenceScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public IncidentResponse() {}

    public static IncidentResponse from(Incident incident) {
        IncidentResponse response = new IncidentResponse();
        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setAffectedService(incident.getAffectedService());
        response.setAiSeverity(incident.getAiSeverity() != null ? incident.getAiSeverity().name() : null);
        response.setAiCategory(incident.getAiCategory() != null ? incident.getAiCategory().name() : null);
        response.setAiSuggestedAction(incident.getAiSuggestedAction());
        response.setConfidenceScore(incident.getConfidenceScore());
        response.setStatus(incident.getStatus().name());
        response.setCreatedAt(incident.getCreatedAt());
        response.setUpdatedAt(incident.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAffectedService() { return affectedService; }
    public void setAffectedService(String affectedService) { this.affectedService = affectedService; }

    public String getAiSeverity() { return aiSeverity; }
    public void setAiSeverity(String aiSeverity) { this.aiSeverity = aiSeverity; }

    public String getAiCategory() { return aiCategory; }
    public void setAiCategory(String aiCategory) { this.aiCategory = aiCategory; }

    public String getAiSuggestedAction() { return aiSuggestedAction; }
    public void setAiSuggestedAction(String aiSuggestedAction) { this.aiSuggestedAction = aiSuggestedAction; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}