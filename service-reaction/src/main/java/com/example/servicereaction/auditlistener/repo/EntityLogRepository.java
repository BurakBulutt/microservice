package com.example.servicereaction.auditlistener.repo;

import com.example.servicereaction.auditlistener.model.EntityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityLogRepository extends JpaRepository<EntityLog, String> {
}
