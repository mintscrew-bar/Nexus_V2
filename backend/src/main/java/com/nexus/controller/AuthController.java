package com.nexus.controller;

import com.nexus.dto.ApiResponse;
import com.nexus.entity.User;
import com.nexus.service.AuthService;
import com.nexus.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public record RegisterRequest(
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "닉네임은 필수입니다.")
            @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다.")
            String nickname,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
            String password,

            @NotBlank(message = "LoL 태그는 필수입니다.")
            String lolTag,

            @Valid
            AgreementsRequest agreements
    ) {}

    public record AgreementsRequest(
            boolean terms,
            boolean privacy,
            Boolean marketing
    ) {}

    public record LoginRequest(
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ) {}

    public record EmailRequest(
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email
    ) {}

    public record EmailVerifyRequest(
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "인증 코드는 필수입니다.")
            String code
    ) {}

    public record NicknameRequest(
            @NotBlank(message = "닉네임은 필수입니다.")
            @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다.")
            String nickname
    ) {}

    public record LolTagRequest(
            @NotBlank(message = "LoL 태그는 필수입니다.")
            String lolTag
    ) {}

    public record LoginResponse(
            String token,
            String username,
            String email
    ) {}

    @PostMapping("/email/code")
    public ResponseEntity<ApiResponse<Void>> requestEmailCode(@Valid @RequestBody EmailRequest request) {
        log.info("Email verification code requested for: {}", request.email());
        authService.requestEmailVerificationCode(request.email());
        return ResponseEntity.ok(ApiResponse.success("인증 코드가 발송되었습니다."));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request) {
        log.info("Email verification attempted for: {}", request.email());
        boolean verified = authService.verifyEmailCode(request.email(), request.code());
        return ResponseEntity.ok(ApiResponse.success(verified, "이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/check/nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@Valid @RequestBody NicknameRequest request) {
        log.info("Nickname availability check for: {}", request.nickname());
        boolean available = userService.isNicknameAvailable(request.nickname());
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    @PostMapping("/check/loltag")
    public ResponseEntity<ApiResponse<Boolean>> checkLolTag(@Valid @RequestBody LolTagRequest request) {
        log.info("LoL tag validation for: {}", request.lolTag());
        boolean valid = authService.validateLolTag(request.lolTag());
        return ResponseEntity.ok(ApiResponse.success(valid));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> register(@Valid @RequestBody RegisterRequest req) {
        log.info("User registration attempted for email: {}", req.email());
        User user = authService.registerUser(req);
        return ResponseEntity.ok(ApiResponse.success(user.getId(), "회원가입이 완료되었습니다."));
    }

    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> completeOnboarding(@Valid @RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        String lolTag = request.get("lolTag");
        log.info("Onboarding completion for nickname: {}", nickname);
        authService.completeOnboarding(nickname, lolTag);
        return ResponseEntity.ok(ApiResponse.success("온보딩이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        log.info("Login attempt for email: {}", req.email());
        LoginResponse response = authService.login(req.email(), req.password());
        return ResponseEntity.ok(ApiResponse.success(response, "로그인 성공"));
    }
}