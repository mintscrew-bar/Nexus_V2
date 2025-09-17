package com.nexus.repository;

import com.nexus.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    Page<AuditLog> findByUserEmailOrderByTimestampDesc(String userEmail, Pageable pageable);

    List<AuditLog> findByActionTypeAndTimestampBetween(
        AuditLog.AuditAction actionType,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    @Query("SELECT a FROM AuditLog a WHERE a.result = :result AND a.timestamp > :since")
    List<AuditLog> findFailedActionsSince(@Param("result") AuditLog.AuditResult result,
                                         @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.result = 'FAILURE' AND a.timestamp > :since")
    long countFailedLoginsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}