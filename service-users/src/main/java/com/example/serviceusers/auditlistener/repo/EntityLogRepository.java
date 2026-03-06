package com.example.serviceusers.auditlistener.repo;

import com.example.serviceusers.auditlistener.model.EntityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityLogRepository extends JpaRepository<EntityLog, String> {
}
