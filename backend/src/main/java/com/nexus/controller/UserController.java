package com.nexus.controller;

import com.nexus.dto.ApiResponse;
import com.nexus.entity.User;
import com.nexus.exception.BusinessException;
import com.nexus.repository.UserRepository;
import com.nexus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자의 프로필 정보를 조회합니다.
     *
     * @param jwt JWT 토큰 객체 (Spring Security가 자동으로 주입)
     * @return ResponseEntity<ApiResponse<UserProfileResponse>>
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyUserInfo(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.info("User profile requested for userId: {}", userId);

        User user = userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new BusinessException.UserNotFoundException());

        UserProfileResponse response = new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getSummonerName()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    public record UserProfileResponse(
            Long id,
            String email,
            String nickname,
            String summonerName
    ) {}
}