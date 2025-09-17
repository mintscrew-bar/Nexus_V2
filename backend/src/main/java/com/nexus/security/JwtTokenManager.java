package com.nexus.security;

import com.nexus.entity.AuditLog;
import com.nexus.service.AuditService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final AuditService auditService;

    @Value("${jwt.secret:dev-secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration:3600}")
    private long accessTokenExpiration; // 1시간

    @Value("${jwt.refresh-token-expiration:604800}")
    private long refreshTokenExpiration; // 7일

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:";

    public String generateAccessToken(String userId, String email, String nickname, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenExpiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("nexus-local")
            .issuedAt(now)
            .expiresAt(expiration)
            .subject(userId)
            .claim("email", email)
            .claim("preferred_username", nickname)
            .claim("role", role)
            .claim("type", "access")
            .build();

        return encodeToken(claims);
    }

    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(refreshTokenExpiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("nexus-local")
            .issuedAt(now)
            .expiresAt(expiration)
            .subject(userId)
            .claim("type", "refresh")
            .build();

        String refreshToken = encodeToken(claims);

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set(
            REFRESH_TOKEN_PREFIX + userId,
            refreshToken,
            refreshTokenExpiration,
            TimeUnit.SECONDS
        );

        return refreshToken;
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            String jti = extractJti(token);
            return redisTemplate.hasKey(BLACKLIST_PREFIX + jti);
        } catch (Exception e) {
            log.warn("Error checking token blacklist status", e);
            return true; // 오류 시 안전하게 차단
        }
    }

    public void blacklistToken(String token, String userId, String reason) {
        try {
            String jti = extractJti(token);
            long expiration = extractExpiration(token);

            // 토큰이 만료될 때까지 블랙리스트에 유지
            long ttl = expiration - System.currentTimeMillis() / 1000;
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + jti,
                    reason,
                    ttl,
                    TimeUnit.SECONDS
                );

                auditService.logUserAction(
                    userId, "", "TOKEN_BLACKLISTED", "JWT",
                    AuditLog.AuditAction.LOGOUT, AuditLog.AuditResult.SUCCESS,
                    "Reason: " + reason
                );

                log.info("Token blacklisted for user: {} - Reason: {}", userId, reason);
            }
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
        }
    }

    public void invalidateAllUserTokens(String userId, String reason) {
        try {
            // 사용자의 모든 리프레시 토큰 무효화
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);

            auditService.logUserAction(
                userId, "", "ALL_TOKENS_INVALIDATED", "JWT",
                AuditLog.AuditAction.LOGOUT, AuditLog.AuditResult.SUCCESS,
                "Reason: " + reason
            );

            log.info("All tokens invalidated for user: {} - Reason: {}", userId, reason);
        } catch (Exception e) {
            log.error("Error invalidating all user tokens", e);
        }
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            if (isTokenBlacklisted(refreshToken)) {
                throw new JwtException("Refresh token is blacklisted");
            }

            Jwt jwt = decodeToken(refreshToken);
            String userId = jwt.getSubject();
            String tokenType = jwt.getClaimAsString("type");

            if (!"refresh".equals(tokenType)) {
                throw new JwtException("Invalid token type");
            }

            // Redis에서 저장된 리프레시 토큰과 비교
            String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                throw new JwtException("Invalid refresh token");
            }

            // 새로운 액세스 토큰 생성 (사용자 정보는 별도로 조회 필요)
            return generateAccessToken(userId, "", "", "USER");

        } catch (Exception e) {
            log.warn("Failed to refresh access token", e);
            throw new JwtException("Token refresh failed");
        }
    }

    private String encodeToken(JwtClaimsSet claims) {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey));
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Jwt decodeToken(String token) {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey).build();
        return decoder.decode(token);
    }

    private String extractJti(String token) {
        Jwt jwt = decodeToken(token);
        return jwt.getClaimAsString("jti");
    }

    private long extractExpiration(String token) {
        Jwt jwt = decodeToken(token);
        return jwt.getExpiresAt().getEpochSecond();
    }

    // OAuth2Service에서 사용하는 User 객체로 토큰 생성
    public String generateToken(com.nexus.entity.User user) {
        return generateAccessToken(
            String.valueOf(user.getId()),
            user.getEmail(),
            user.getNickname(),
            user.getRole().name()
        );
    }

    public void cleanupExpiredBlacklistedTokens() {
        try {
            Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                // Redis TTL이 자동으로 만료된 키들을 제거하므로 별도 처리 불필요
                log.info("Blacklisted tokens cleanup completed. Found {} keys", keys.size());
            }
        } catch (Exception e) {
            log.error("Error during blacklisted tokens cleanup", e);
        }
    }
}