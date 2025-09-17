package com.nexus.security;

import com.nexus.exception.BusinessException;
import com.nexus.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.nexus.entity.AuditLog.AuditAction;
import static com.nexus.entity.AuditLog.AuditResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityValidator {

    private final InputSanitizer inputSanitizer;
    private final AuditService auditService;

    public void validateAndSanitizeAuthRequest(String email, String password, String nickname, String lolTag) {
        // 이메일 검증
        if (!inputSanitizer.isValidEmail(email)) {
            auditService.logSecurityEvent(
                "INVALID_EMAIL_FORMAT",
                "Invalid email format attempted: " + inputSanitizer.sanitizeForDisplay(email),
                AuditResult.FAILURE
            );
            throw new BusinessException("올바르지 않은 이메일 형식입니다.");
        }

        // 비밀번호 보안 검증
        validatePasswordSecurity(password);

        // 닉네임 검증 (등록 시에만)
        if (nickname != null && !inputSanitizer.isValidNickname(nickname)) {
            auditService.logSecurityEvent(
                "INVALID_NICKNAME_FORMAT",
                "Invalid nickname format attempted: " + inputSanitizer.sanitizeForDisplay(nickname),
                AuditResult.FAILURE
            );
            throw new BusinessException("닉네임은 2-20자의 한글, 영문, 숫자, 언더스코어만 사용 가능합니다.");
        }

        // LoL 태그 검증 (등록 시에만)
        if (lolTag != null && !inputSanitizer.isValidLolTag(lolTag)) {
            auditService.logSecurityEvent(
                "INVALID_LOL_TAG_FORMAT",
                "Invalid LoL tag format attempted: " + inputSanitizer.sanitizeForDisplay(lolTag),
                AuditResult.FAILURE
            );
            throw new BusinessException("올바르지 않은 LoL 태그 형식입니다.");
        }
    }

    public void validateGeneralInput(String input, String fieldName) {
        if (input == null) return;

        if (!inputSanitizer.isSafeInput(input)) {
            auditService.logSecurityEvent(
                "MALICIOUS_INPUT_DETECTED",
                String.format("Malicious input in field '%s': %s", fieldName, inputSanitizer.sanitizeForDisplay(input)),
                AuditResult.FAILURE
            );
            throw new BusinessException("입력값에 허용되지 않는 문자가 포함되어 있습니다.");
        }
    }

    private void validatePasswordSecurity(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        if (password.length() > 128) {
            throw new BusinessException("비밀번호는 최대 128자까지 가능합니다.");
        }

        // 기본적인 보안 요구사항 검증
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        int complexity = 0;
        if (hasUpper) complexity++;
        if (hasLower) complexity++;
        if (hasDigit) complexity++;
        if (hasSpecial) complexity++;

        if (complexity < 3) {
            throw new BusinessException("비밀번호는 대문자, 소문자, 숫자, 특수문자 중 최소 3가지 이상을 포함해야 합니다.");
        }

        // 일반적인 약한 비밀번호 패턴 검사
        String[] weakPatterns = {
            "password", "123456", "qwerty", "admin", "login",
            "user", "guest", "test", "demo", "default"
        };

        String lowerPassword = password.toLowerCase();
        for (String pattern : weakPatterns) {
            if (lowerPassword.contains(pattern)) {
                auditService.logSecurityEvent(
                    "WEAK_PASSWORD_DETECTED",
                    "Weak password pattern detected: " + pattern,
                    AuditResult.FAILURE
                );
                throw new BusinessException("더 안전한 비밀번호를 사용해주세요.");
            }
        }

        // XSS/SQLi 공격 시도 탐지
        if (!inputSanitizer.isSafeInput(password)) {
            auditService.logSecurityEvent(
                "MALICIOUS_PASSWORD_INPUT",
                "Malicious characters detected in password",
                AuditResult.FAILURE
            );
            throw new BusinessException("비밀번호에 허용되지 않는 문자가 포함되어 있습니다.");
        }
    }
}