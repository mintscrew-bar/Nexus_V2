package com.nexus.exception;

// HTTP 404 Not Found 상태 코드와 연결될 수 있는 커스텀 예외 클래스입니다.
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}