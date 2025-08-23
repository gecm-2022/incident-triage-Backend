package com.company.incident_triage.service;

import com.company.incident_triage.dto.IncidentCreateRequest;
import com.company.incident_triage.model.Incident;
import com.company.incident_triage.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private AiTriageService aiTriageService;

    public Incident createIncident(IncidentCreateRequest request) {
        // Create new incident
        Incident incident = new Incident(request.getTitle(), request.getDescription(), request.getAffectedService());
        
        // Apply AI analysis
        AiTriageService.AiTriageResult aiResult = aiTriageService.analyzeIncident(
                incident.getTitle(), 
                incident.getDescription(), 
                incident.getAffectedService()
        );
        
        // Set AI-generated insights
        incident.setAiSeverity(aiResult.getSeverity());
        incident.setAiCategory(aiResult.getCategory());
        incident.setAiSuggestedAction(aiResult.getSuggestedAction());
        incident.setConfidenceScore(aiResult.getConfidence());
        
        // Save and return
        return incidentRepository.save(incident);
    }

    public Page<Incident> getIncidents(Pageable pageable, String severity, String category) {
        if (severity != null && category != null) {
            return incidentRepository.findByAiSeverityAndAiCategory(
                    Incident.Severity.valueOf(severity.toUpperCase()),
                    Incident.Category.valueOf(category.toUpperCase()),
                    pageable
            );
        } else if (severity != null) {
            return incidentRepository.findByAiSeverity(
                    Incident.Severity.valueOf(severity.toUpperCase()),
                    pageable
            );
        } else if (category != null) {
            return incidentRepository.findByAiCategory(
                    Incident.Category.valueOf(category.toUpperCase()),
                    pageable
            );
        }
        return incidentRepository.findAll(pageable);
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public Incident getIncidentByIdOrThrow(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));
    }

    public Map<String, Object> getIncidentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Severity statistics
        Map<String, Long> severityStats = new HashMap<>();
        for (Incident.Severity severity : Incident.Severity.values()) {
            severityStats.put(severity.name(), incidentRepository.countBySeverity(severity));
        }
        stats.put("severity", severityStats);
        
        // Category statistics
        Map<String, Long> categoryStats = new HashMap<>();
        for (Incident.Category category : Incident.Category.values()) {
            categoryStats.put(category.name(), incidentRepository.countByCategory(category));
        }
        stats.put("category", categoryStats);
        
        // Status statistics
        Map<String, Long> statusStats = new HashMap<>();
        for (Incident.Status status : Incident.Status.values()) {
            statusStats.put(status.name(), incidentRepository.countByStatus(status));
        }
        stats.put("status", statusStats);
        
        // Total count
        stats.put("total", incidentRepository.count());
        
        return stats;
    }

    public Incident updateIncidentStatus(Long id, Incident.Status status) {
        Incident incident = getIncidentByIdOrThrow(id);
        incident.setStatus(status);
        return incidentRepository.save(incident);
    }
}