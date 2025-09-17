package com.nexus.service;

import com.nexus.entity.AuditLog;
import com.nexus.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void logUserAction(String userId, String userEmail, String action, String resource,
                             AuditLog.AuditAction actionType, AuditLog.AuditResult result, String details) {
        try {
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .userEmail(userEmail)
                .action(action)
                .resource(resource)
                .actionType(actionType)
                .result(result)
                .details(details)
                .ipAddress(getClientIpAddress(request))
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .build();

            auditLogRepository.save(auditLog);
            log.info("Audit log created: {} - {} - {} - {}", userId, action, resource, result);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    @Async
    public void logSecurityEvent(String action, String details, AuditLog.AuditResult result) {
        try {
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                .userId("SYSTEM")
                .userEmail("system@nexus.com")
                .action(action)
                .resource("SECURITY")
                .actionType(AuditLog.AuditAction.ADMIN_ACTION)
                .result(result)
                .details(details)
                .ipAddress(getClientIpAddress(request))
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .build();

            auditLogRepository.save(auditLog);
            log.warn("Security event logged: {} - {} - {}", action, details, result);
        } catch (Exception e) {
            log.error("Failed to save security audit log", e);
        }
    }

    public boolean isIpBlocked(String ipAddress) {
        // 지난 15분간 실패한 로그인 시도가 5회 이상인 경우 차단
        long failedAttempts = auditLogRepository.countFailedLoginsByIp(
            ipAddress,
            java.time.LocalDateTime.now().minusMinutes(15)
        );

        return failedAttempts >= 5;
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "unknown";

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}