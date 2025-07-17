package com.example.servicemedia.auditlistener.repo;

import com.example.servicemedia.auditlistener.model.EntityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityLogRepository extends JpaRepository<EntityLog, String> {
}
