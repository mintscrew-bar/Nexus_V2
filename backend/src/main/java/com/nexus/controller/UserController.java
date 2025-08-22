package com.nexus.controller;

import com.nexus.entity.User;
import com.nexus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 현재 로그인한 사용자의 프로필 정보를 조회하고,
     * 사용자가 DB에 없으면 새로 생성하는 동기화 API입니다.
     *
     * @param jwt Keycloak에서 발급한 JWT 토큰 객체 (Spring Security가 자동으로 주입)
     * @return ResponseEntity<User>
     */
    @GetMapping("/me")
    public ResponseEntity<User> getMyUserInfo(@AuthenticationPrincipal Jwt jwt) {
        // JWT 토큰에서 사용자 정보를 추출합니다.
        String keycloakId = jwt.getSubject(); // 'sub' 클레임이 Keycloak의 사용자 ID입니다.
        String email = jwt.getClaimAsString("email");
        String nickname = jwt.getClaimAsString("preferred_username");

        // UserService를 통해 사용자를 찾거나 생성합니다.
        User user = userService.findOrCreateUser(keycloakId, email, nickname);

        return ResponseEntity.ok(user);
    }
}
