package com.nexus.service;

import com.nexus.entity.AuditLog;
import com.nexus.entity.User;
import com.nexus.exception.BusinessException;
import com.nexus.repository.UserRepository;
import com.nexus.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;
    private final AuditService auditService;

    @Transactional
    public String processOAuth2User(String provider, OAuth2User oauth2User) {
        try {
            // OAuth2 제공자별 사용자 정보 추출
            OAuth2UserInfo userInfo = extractUserInfo(provider, oauth2User);

            // 이메일로 기존 사용자 확인
            User user = userRepository.findByEmail(userInfo.getEmail())
                    .orElse(null);

            if (user == null) {
                // 새 사용자 생성
                user = createNewOAuth2User(userInfo, provider);
                auditService.logUserAction(
                    String.valueOf(user.getId()), user.getEmail(), "OAUTH2_REGISTER", "USER",
                    AuditLog.AuditAction.REGISTER, AuditLog.AuditResult.SUCCESS,
                    "OAuth2 user registered with " + provider
                );
            } else {
                // 기존 사용자 로그인
                updateUserLastLogin(user);
                auditService.logUserAction(
                    String.valueOf(user.getId()), user.getEmail(), "OAUTH2_LOGIN", "USER",
                    AuditLog.AuditAction.LOGIN, AuditLog.AuditResult.SUCCESS,
                    "OAuth2 login with " + provider
                );
            }

            // JWT 토큰 생성
            return jwtTokenManager.generateToken(user);

        } catch (Exception e) {
            log.error("OAuth2 user processing failed", e);
            auditService.logUserAction(
                "UNKNOWN", "unknown@email.com", "OAUTH2_FAILED", "USER",
                AuditLog.AuditAction.LOGIN, AuditLog.AuditResult.FAILURE,
                "OAuth2 processing failed: " + e.getMessage()
            );
            throw new BusinessException("OAuth2 인증 처리 중 오류가 발생했습니다.");
        }
    }

    public User getCurrentUser(OAuth2User oauth2User) {
        String email = (String) oauth2User.getAttributes().get("email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
    }

    private OAuth2UserInfo extractUserInfo(String provider, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        switch (provider.toLowerCase()) {
            case "google":
                return OAuth2UserInfo.builder()
                        .email((String) attributes.get("email"))
                        .name((String) attributes.get("name"))
                        .profileImageUrl((String) attributes.get("picture"))
                        .providerId((String) attributes.get("sub"))
                        .build();

            case "discord":
                String discordId = (String) attributes.get("id");
                String username = (String) attributes.get("username");
                String discriminator = (String) attributes.get("discriminator");
                String avatar = (String) attributes.get("avatar");

                String profileImageUrl = null;
                if (avatar != null) {
                    profileImageUrl = "https://cdn.discordapp.com/avatars/" + discordId + "/" + avatar + ".png";
                }

                return OAuth2UserInfo.builder()
                        .email((String) attributes.get("email"))
                        .name(username + (discriminator != null ? "#" + discriminator : ""))
                        .profileImageUrl(profileImageUrl)
                        .providerId(discordId)
                        .build();

            default:
                throw new BusinessException("지원하지 않는 OAuth2 제공자입니다: " + provider);
        }
    }

    private User createNewOAuth2User(OAuth2UserInfo userInfo, String provider) {
        // 닉네임 생성 (중복 체크)
        String nickname = generateUniqueNickname(userInfo.getName());

        return userService.createOAuth2User(
                userInfo.getEmail(),
                nickname,
                userInfo.getProfileImageUrl(),
                provider,
                userInfo.getProviderId()
        );
    }

    private String generateUniqueNickname(String baseName) {
        // 특수문자 제거 및 길이 제한
        String cleanName = baseName.replaceAll("[^a-zA-Z0-9가-힣]", "");
        if (cleanName.length() > 12) {
            cleanName = cleanName.substring(0, 12);
        }

        // 중복 체크 및 숫자 추가
        String nickname = cleanName;
        int counter = 1;
        while (userRepository.findByNickname(nickname).isPresent()) {
            nickname = cleanName + counter;
            counter++;
            if (counter > 9999) { // 무한 루프 방지
                nickname = cleanName + System.currentTimeMillis() % 10000;
                break;
            }
        }

        return nickname;
    }

    private void updateUserLastLogin(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // OAuth2 사용자 정보를 담는 내부 클래스
    private static class OAuth2UserInfo {
        private String email;
        private String name;
        private String profileImageUrl;
        private String providerId;

        public static OAuth2UserInfoBuilder builder() {
            return new OAuth2UserInfoBuilder();
        }

        // Getters
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public String getProviderId() { return providerId; }

        // Builder 클래스
        public static class OAuth2UserInfoBuilder {
            private OAuth2UserInfo userInfo = new OAuth2UserInfo();

            public OAuth2UserInfoBuilder email(String email) {
                userInfo.email = email;
                return this;
            }

            public OAuth2UserInfoBuilder name(String name) {
                userInfo.name = name;
                return this;
            }

            public OAuth2UserInfoBuilder profileImageUrl(String profileImageUrl) {
                userInfo.profileImageUrl = profileImageUrl;
                return this;
            }

            public OAuth2UserInfoBuilder providerId(String providerId) {
                userInfo.providerId = providerId;
                return this;
            }

            public OAuth2UserInfo build() {
                return userInfo;
            }
        }
    }
}