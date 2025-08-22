package com.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정을 활성화합니다.
            .cors(withDefaults())
            // CSRF(Cross-Site Request Forgery) 보호를 비활성화합니다. Stateless API에서는 보통 비활성화합니다.
            .csrf(csrf -> csrf.disable())
            // 세션을 사용하지 않는 Stateless 방식으로 설정합니다. JWT 인증에 적합합니다.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // OAuth2 리소스 서버 설정을 활성화하고, JWT 토큰을 사용하도록 설정합니다.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
            // HTTP 요청에 대한 접근 권한을 설정합니다.
            .authorizeHttpRequests(authorize -> authorize
                // '/api/public/**' 경로의 모든 요청은 인증 없이 허용합니다. (예: 헬스 체크, 공지사항 등)
                .requestMatchers("/api/public/**").permitAll()
                // 그 외의 모든 요청은 인증된 사용자만 접근 가능하도록 설정합니다.
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // .env 파일의 CORS_ALLOWED_ORIGINS 값을 여기에 설정해야 합니다.
        // 지금은 개발 환경을 위해 localhost:3000을 허용합니다.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // 자격 증명(쿠키 등)을 허용합니다.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정을 적용합니다.
        return source;
    }
}
