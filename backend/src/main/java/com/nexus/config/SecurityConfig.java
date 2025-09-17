package com.nexus.config;

import com.nexus.security.RateLimitingFilter;
import com.nexus.security.SecurityHeadersFilter;
import com.nexus.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final RateLimitingFilter rateLimitingFilter;
    private final SecurityHeadersFilter securityHeadersFilter;
    private final OAuth2Service oauth2Service;

    @Value("${jwt.secret:dev-secret}")
    private String jwtSecret;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");

        http
            .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(withDefaults())
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    // OAuth2 성공 후 프론트엔드로 리다이렉트
                    try {
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        String provider = extractProvider(request.getRequestURI());

                        // OAuth2Service로 사용자 처리 및 토큰 생성
                        String token = oauth2Service.processOAuth2User(provider, oauth2User);

                        // 프론트엔드로 토큰과 함께 리다이렉트
                        String frontendUrl = "http://localhost:3000"; // TODO: 설정에서 가져오기
                        response.sendRedirect(frontendUrl + "/oauth/callback?token=" + token);
                    } catch (Exception e) {
                        log.error("OAuth2 success handler error", e);
                        response.sendRedirect("http://localhost:3000/login?error=processing_failed");
                    }
                })
                .failureUrl("/login?error=oauth2_failed")
            )
            .authorizeHttpRequests(authorize -> authorize
                // 인증이 필요없는 엔드포인트
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/login").permitAll()

                // 게임 방 목록 조회는 인증 없이 허용 (선택사항)
                .requestMatchers("GET", "/api/games").permitAll()

                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("Authentication failed for request: {}", request.getRequestURI());
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"인증이 필요합니다.\",\"success\":false}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("Access denied for request: {}", request.getRequestURI());
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"접근 권한이 없습니다.\",\"success\":false}");
                })
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용된 Origins 설정
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // 허용된 HTTP 메서드
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용된 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // Credentials 허용
        configuration.setAllowCredentials(true);

        // Preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("CORS configuration applied with allowed origins: {}", Arrays.toString(allowedOrigins));
        return source;
    }

    private String extractProvider(String requestURI) {
        // /login/oauth2/code/google -> google
        String[] parts = requestURI.split("/");
        return parts[parts.length - 1];
    }
}
