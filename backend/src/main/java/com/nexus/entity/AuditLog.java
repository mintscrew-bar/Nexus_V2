package com.nexus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String resource;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction actionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditResult result;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public enum AuditAction {
        LOGIN, LOGOUT, REGISTER, PASSWORD_CHANGE,
        CREATE, READ, UPDATE, DELETE,
        JOIN_ROOM, LEAVE_ROOM, CREATE_ROOM,
        TEAM_COMPOSITION_START, ADMIN_ACTION
    }

    public enum AuditResult {
        SUCCESS, FAILURE, ERROR
    }
}