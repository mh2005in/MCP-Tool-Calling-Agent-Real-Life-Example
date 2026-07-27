package com.immiauto.repository;

import com.immiauto.entity.McpToolAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McpToolAuditLogRepository extends JpaRepository<McpToolAuditLog, Long> {
}
