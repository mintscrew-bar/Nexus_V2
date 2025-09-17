package com.nexus.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static class EmailAlreadyExistsException extends BusinessException {
        public EmailAlreadyExistsException() {
            super("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);
        }
    }

    public static class NicknameAlreadyExistsException extends BusinessException {
        public NicknameAlreadyExistsException() {
            super("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT);
        }
    }

    public static class InvalidCredentialsException extends BusinessException {
        public InvalidCredentialsException() {
            super("이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    public static class UserNotFoundException extends BusinessException {
        public UserNotFoundException() {
            super("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    public static class InvalidVerificationCodeException extends BusinessException {
        public InvalidVerificationCodeException() {
            super("잘못된 인증 코드입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}