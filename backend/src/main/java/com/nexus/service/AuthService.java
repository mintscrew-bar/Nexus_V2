package com.nexus.service;

import com.nexus.controller.AuthController;
import com.nexus.entity.AuditLog;
import com.nexus.entity.User;
import com.nexus.exception.BusinessException;
import com.nexus.repository.UserRepository;
import com.nexus.security.SecurityValidator;
import com.nexus.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SecurityValidator securityValidator;
    private final JwtTokenManager jwtTokenManager;
    private final AuditService auditService;
    private final EmailService emailService;

    // 임시 저장소 (운영환경에서는 Redis 등 사용)
    private final Map<String, String> emailCodeStorage = new ConcurrentHashMap<>();

    public void requestEmailVerificationCode(String email) {
        // 6자리 랜덤 코드 생성
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
        emailCodeStorage.put(email, code);

        log.info("Email verification code generated for: {}", email);
        // 실제 이메일 발송
        emailService.sendVerificationCode(email, code);
    }

    public boolean verifyEmailCode(String email, String code) {
        String storedCode = emailCodeStorage.get(email);
        if (storedCode == null) {
            throw new BusinessException.InvalidVerificationCodeException();
        }

        boolean isValid = storedCode.equals(code);
        if (isValid) {
            emailCodeStorage.remove(email); // 인증 성공 시 코드 삭제
        }

        return isValid;
    }

    public boolean validateLolTag(String lolTag) {
        // TODO: Riot API를 통한 실제 LoL 태그 검증 구현
        // 현재는 기본적인 형식 검증만 수행
        if (lolTag == null || lolTag.trim().isEmpty()) {
            return false;
        }

        // 기본 형식: 소환사명#태그 (예: PlayerName#KR1)
        return lolTag.matches("^[\\w가-힣]{3,16}#[A-Z]{2,4}\\d*$");
    }

    @Transactional
    public User registerUser(AuthController.RegisterRequest request) {
        // 보안 검증
        securityValidator.validateAndSanitizeAuthRequest(
            request.email(), request.password(), request.nickname(), request.lolTag()
        );

        // 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            auditService.logUserAction(
                "UNKNOWN", request.email(), "REGISTER_FAILED", "USER",
                AuditLog.AuditAction.REGISTER, AuditLog.AuditResult.FAILURE,
                "Email already exists"
            );
            throw new BusinessException.EmailAlreadyExistsException();
        }

        // 닉네임 중복 확인
        if (userRepository.findByNickname(request.nickname()).isPresent()) {
            auditService.logUserAction(
                "UNKNOWN", request.email(), "REGISTER_FAILED", "USER",
                AuditLog.AuditAction.REGISTER, AuditLog.AuditResult.FAILURE,
                "Nickname already exists"
            );
            throw new BusinessException.NicknameAlreadyExistsException();
        }

        // 약관 동의 확인
        if (!request.agreements().terms() || !request.agreements().privacy()) {
            auditService.logUserAction(
                "UNKNOWN", request.email(), "REGISTER_FAILED", "USER",
                AuditLog.AuditAction.REGISTER, AuditLog.AuditResult.FAILURE,
                "Terms not agreed"
            );
            throw new BusinessException("필수 약관에 동의해야 합니다.");
        }

        User user = userService.registerLocalUser(
            request.email(),
            request.nickname(),
            request.password(),
            request.lolTag()
        );

        auditService.logUserAction(
            String.valueOf(user.getId()), user.getEmail(), "REGISTER_SUCCESS", "USER",
            AuditLog.AuditAction.REGISTER, AuditLog.AuditResult.SUCCESS,
            "User registered successfully"
        );

        return user;
    }

    public AuthController.LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException.InvalidCredentialsException());

        if (!userService.verifyPassword(user, password)) {
            throw new BusinessException.InvalidCredentialsException();
        }

        String token = jwtTokenManager.generateToken(user);

        return new AuthController.LoginResponse(
            token,
            user.getNickname(),
            user.getEmail()
        );
    }


    public void completeOnboarding(String nickname, String lolTag) {
        // TODO: 온보딩 완료 로직 구현
        log.info("Onboarding completed for nickname: {}, lolTag: {}", nickname, lolTag);
    }
}