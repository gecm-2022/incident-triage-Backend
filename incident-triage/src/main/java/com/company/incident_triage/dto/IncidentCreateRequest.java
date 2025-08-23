package com.company.incident_triage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IncidentCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotBlank(message = "Affected service is required")
    @Size(max = 100, message = "Affected service must not exceed 100 characters")
    private String affectedService;

    // Constructors
    public IncidentCreateRequest() {}

    public IncidentCreateRequest(String title, String description, String affectedService) {
        this.title = title;
        this.description = description;
        this.affectedService = affectedService;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAffectedService() { return affectedService; }
    public void setAffectedService(String affectedService) { this.affectedService = affectedService; }
}