package com.company.incident_triage.repository;

import com.company.incident_triage.model.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Page<Incident> findByAiSeverity(Incident.Severity severity, Pageable pageable);
    
    Page<Incident> findByAiCategory(Incident.Category category, Pageable pageable);
    
    Page<Incident> findByAiSeverityAndAiCategory(
            Incident.Severity severity, 
            Incident.Category category, 
            Pageable pageable
    );

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.aiSeverity = :severity")
    Long countBySeverity(@Param("severity") Incident.Severity severity);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.aiCategory = :category")
    Long countByCategory(@Param("category") Incident.Category category);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.status = :status")
    Long countByStatus(@Param("status") Incident.Status status);
}