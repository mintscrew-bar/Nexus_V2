package com.nexus.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Value("${app.domain:localhost}")
    private String appDomain;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // Content Security Policy
        response.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' https:; " +
            "connect-src 'self' https:; " +
            "frame-ancestors 'none';"
        );

        // X-Frame-Options (Clickjacking 방지)
        response.setHeader("X-Frame-Options", "DENY");

        // X-Content-Type-Options (MIME 스니핑 방지)
        response.setHeader("X-Content-Type-Options", "nosniff");

        // X-XSS-Protection
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Referrer Policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions Policy
        response.setHeader("Permissions-Policy",
            "camera=(), microphone=(), geolocation=(), payment=(), usb=()");

        // HTTPS 강제 (운영 환경)
        if (sslEnabled) {
            // HSTS (HTTP Strict Transport Security)
            response.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains; preload");

            // HTTPS 리다이렉트
            if (!request.isSecure() && !isLocalDevelopment(request)) {
                String httpsUrl = "https://" + request.getServerName() + request.getRequestURI();
                if (request.getQueryString() != null) {
                    httpsUrl += "?" + request.getQueryString();
                }
                response.sendRedirect(httpsUrl);
                return;
            }
        }

        // Cache Control for sensitive endpoints
        if (isSensitiveEndpoint(request.getRequestURI())) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }

        // Server 헤더 숨기기
        response.setHeader("Server", "Nexus-API");

        filterChain.doFilter(request, response);
    }

    private boolean isLocalDevelopment(HttpServletRequest request) {
        String host = request.getServerName();
        return "localhost".equals(host) || "127.0.0.1".equals(host);
    }

    private boolean isSensitiveEndpoint(String uri) {
        return uri.startsWith("/api/auth/") ||
               uri.startsWith("/api/users/") ||
               uri.contains("admin") ||
               uri.contains("password");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator/health") ||
               uri.startsWith("/error");
    }
}