package com.nexus.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class InputSanitizer {

    // XSS 공격 패턴
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)<script[^>]*>.*?</script>|javascript:|vbscript:|onload=|onerror=|onclick=|onmouseover=",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
    );

    // SQL Injection 패턴
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(\\s*(;|'|\"|--)|(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s)",
        Pattern.CASE_INSENSITIVE
    );

    // 허용되는 문자만 포함하는 패턴들
    private static final Pattern SAFE_EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern SAFE_NICKNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9가-힣_]{2,20}$"
    );

    private static final Pattern SAFE_LOL_TAG_PATTERN = Pattern.compile(
        "^[\\w가-힣]{3,16}#[A-Z]{2,4}\\d*$"
    );

    public String sanitizeString(String input) {
        if (input == null) return null;

        // HTML 태그 제거
        String sanitized = input.replaceAll("<[^>]*>", "");

        // 특수 문자 이스케이프
        sanitized = sanitized
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");

        return sanitized.trim();
    }

    public boolean containsXSS(String input) {
        if (input == null) return false;
        boolean hasXSS = XSS_PATTERN.matcher(input).find();
        if (hasXSS) {
            log.warn("XSS attack attempt detected: {}", input.substring(0, Math.min(input.length(), 100)));
        }
        return hasXSS;
    }

    public boolean containsSQLInjection(String input) {
        if (input == null) return false;
        boolean hasSQLI = SQL_INJECTION_PATTERN.matcher(input).find();
        if (hasSQLI) {
            log.warn("SQL injection attempt detected: {}", input.substring(0, Math.min(input.length(), 100)));
        }
        return hasSQLI;
    }

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return SAFE_EMAIL_PATTERN.matcher(email).matches() &&
               !containsXSS(email) &&
               !containsSQLInjection(email);
    }

    public boolean isValidNickname(String nickname) {
        if (nickname == null) return false;
        return SAFE_NICKNAME_PATTERN.matcher(nickname).matches() &&
               !containsXSS(nickname) &&
               !containsSQLInjection(nickname);
    }

    public boolean isValidLolTag(String lolTag) {
        if (lolTag == null) return false;
        return SAFE_LOL_TAG_PATTERN.matcher(lolTag).matches() &&
               !containsXSS(lolTag) &&
               !containsSQLInjection(lolTag);
    }

    public boolean isSafeInput(String input) {
        if (input == null) return true;
        return !containsXSS(input) && !containsSQLInjection(input);
    }

    public String sanitizeForDisplay(String input) {
        if (input == null) return null;

        // 기본 sanitization
        String sanitized = sanitizeString(input);

        // 길이 제한 (DoS 방지)
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000) + "...";
        }

        return sanitized;
    }
}