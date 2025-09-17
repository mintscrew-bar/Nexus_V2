package com.nexus.service;

import com.nexus.entity.Role;
import com.nexus.entity.User;
import com.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public User registerLocalUser(String email, String nickname, String rawPassword, String lolTag) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole(Role.USER);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setSummonerName(lolTag);
        return userRepository.save(user);
    }

    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    @Transactional
    public User createOAuth2User(String email, String nickname, String profileImageUrl, String provider, String providerId) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole(Role.USER);
        user.setProfileImageUrl(profileImageUrl);
        // OAuth2 사용자는 패스워드가 없으므로 랜덤 해시 설정
        user.setPasswordHash(passwordEncoder.encode("oauth2-" + providerId + "-" + System.currentTimeMillis()));

        return userRepository.save(user);
    }
}
