package com.company.incident_triage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Affected service is required")
    @Column(name = "affected_service", nullable = false)
    private String affectedService;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_severity")
    private Severity aiSeverity;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_category")
    private Category aiCategory;

    @Column(name = "ai_suggested_action", columnDefinition = "TEXT")
    private String aiSuggestedAction;

    @Column(name = "confidence_score", precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Incident() {}

    public Incident(String title, String description, String affectedService) {
        this.title = title;
        this.description = description;
        this.affectedService = affectedService;
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

    public Severity getAiSeverity() { return aiSeverity; }
    public void setAiSeverity(Severity aiSeverity) { this.aiSeverity = aiSeverity; }

    public Category getAiCategory() { return aiCategory; }
    public void setAiCategory(Category aiCategory) { this.aiCategory = aiCategory; }

    public String getAiSuggestedAction() { return aiSuggestedAction; }
    public void setAiSuggestedAction(String aiSuggestedAction) { this.aiSuggestedAction = aiSuggestedAction; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Category {
        HARDWARE, SOFTWARE, NETWORK, SECURITY, DATABASE, FRONTEND
    }

    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }
}