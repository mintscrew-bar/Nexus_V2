package com.nexus.service;

import com.nexus.entity.Role;
import com.nexus.entity.User;
import com.nexus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Keycloak으로부터 받은 사용자 정보로 우리 DB의 사용자를 찾거나 새로 생성합니다.
     * @param keycloakId Keycloak 사용자의 고유 ID
     * @param email 사용자의 이메일
     * @param nickname 사용자의 닉네임
     * @return DB에 저장되거나 조회된 User 객체
     */
    @Transactional
    public User findOrCreateUser(String keycloakId, String email, String nickname) {
        // 1. keycloakId로 DB에서 사용자를 찾아봅니다.
        Optional<User> userOptional = userRepository.findByKeycloakId(keycloakId);

        if (userOptional.isPresent()) {
            // 2. 사용자가 이미 존재하면, 해당 사용자 정보를 반환합니다.
            return userOptional.get();
        } else {
            // 3. 사용자가 존재하지 않으면, 새로운 사용자를 생성합니다.
            User newUser = new User();
            newUser.setKeycloakId(keycloakId);
            newUser.setEmail(email);
            newUser.setNickname(nickname);
            newUser.setRole(Role.USER); // 기본 역할은 'USER'로 설정합니다.
            
            // Riot 관련 정보(puuid, summonerName 등)는
            // 추후 사용자가 직접 입력하거나 API를 통해 받아와서 업데이트해야 합니다.
            // 지금은 필수값이 아닌 필드는 비워둡니다.

            // 4. 새로 만든 사용자를 DB에 저장하고 반환합니다.
            return userRepository.save(newUser);
        }
    }
}
