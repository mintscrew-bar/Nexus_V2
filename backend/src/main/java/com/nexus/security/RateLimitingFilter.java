package com.nexus.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // 일반 API: 분당 60회
    private final Bandwidth generalLimit = Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)));

    // 인증 API: 분당 10회 (더 엄격)
    private final Bandwidth authLimit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));

    // 민감한 작업: 분당 5회
    private final Bandwidth sensitiveLimit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        String clientIp = getClientIpAddress(request);
        String uri = request.getRequestURI();
        String key = clientIp + ":" + uri;

        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, uri);
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"success\":false,\"error\":\"요청 횟수 제한을 초과했습니다. 잠시 후 다시 시도해주세요.\",\"timestamp\":\"" +
                java.time.LocalDateTime.now() + "\"}"
            );
        }
    }

    private Bucket createBucket(String key) {
        String uri = key.split(":", 2)[1];

        if (uri.startsWith("/api/auth/")) {
            return Bucket4j.builder().addLimit(authLimit).build();
        } else if (uri.contains("/password") || uri.contains("/delete") || uri.contains("/admin")) {
            return Bucket4j.builder().addLimit(sensitiveLimit).build();
        } else {
            return Bucket4j.builder().addLimit(generalLimit).build();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator/") ||
               uri.equals("/error") ||
               uri.startsWith("/static/");
    }
}