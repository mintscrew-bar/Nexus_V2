# Error Handling

## Error Handling Architecture

### Centralized Error Management
Nexus implements a comprehensive error handling strategy across all application layers:

- **Frontend**: User-friendly error displays and recovery options
- **API Layer**: Consistent error response formatting
- **Backend**: Detailed error logging with security considerations
- **Database**: Transaction rollback and data integrity protection

## Frontend Error Handling

### Global Error Boundary
React Error Boundary component captures unhandled errors:

```tsx
class ErrorBoundary extends Component {
  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    // Log error details
    console.error('Unhandled error:', error, errorInfo);

    // Report to monitoring service
    errorReportingService.reportError(error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return <ErrorFallbackComponent />;
    }
    return this.props.children;
  }
}
```

### API Error Handling
Centralized API error processing in the service layer:

```typescript
// In api.ts
const handleApiError = (error: any) => {
  if (error.response) {
    // Server responded with error status
    const { status, data } = error.response;

    switch (status) {
      case 401:
        authStore.logout();
        router.push('/login');
        break;
      case 403:
        showNotification('접근 권한이 없습니다.');
        break;
      case 429:
        showNotification('요청이 너무 많습니다. 잠시 후 다시 시도해주세요.');
        break;
      default:
        showNotification(data.error || '오류가 발생했습니다.');
    }
  } else if (error.request) {
    // Network error
    showNotification('네트워크 연결을 확인해주세요.');
  } else {
    // Other error
    showNotification('예상치 못한 오류가 발생했습니다.');
  }
};
```

### Form Validation Errors
Real-time validation with user-friendly feedback:

```typescript
// Registration form validation
const validateRegistrationForm = (data: RegisterForm) => {
  const errors: FormErrors = {};

  if (!data.email || !isValidEmail(data.email)) {
    errors.email = '올바른 이메일 주소를 입력해주세요.';
  }

  if (!data.nickname || data.nickname.length < 2) {
    errors.nickname = '닉네임은 2자 이상이어야 합니다.';
  }

  if (!data.password || !isStrongPassword(data.password)) {
    errors.password = '비밀번호는 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다.';
  }

  return errors;
};
```

### Authentication Error Handling
```typescript
// In authStore.ts
const login = async (email: string, password: string) => {
  try {
    const response = await authApi.login(email, password);

    if (!response.success) {
      throw new Error(response.error || 'Login failed');
    }

    // Update auth state
    setAuthState(response.data);

  } catch (error) {
    // Handle specific auth errors
    if (error.status === 401) {
      throw new Error('이메일 또는 비밀번호가 올바르지 않습니다.');
    } else if (error.status === 429) {
      throw new Error('로그인 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.');
    } else {
      throw new Error('로그인 중 오류가 발생했습니다.');
    }
  }
};
```

## Backend Error Handling

### Global Exception Handler
Centralized exception handling with consistent response format:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Map<String, String>>builder()
                .success(false)
                .error("입력값 검증 실패")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("Unexpected exception", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
    }
}
```

### Business Exception Types
Specific exception classes for different error scenarios:

```java
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

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
}
```

### Validation Error Handling
Input validation with detailed error messages:

```java
public record RegisterRequest(
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다.")
    String nickname,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password
) {}
```

## Error Response Standards

### API Response Format
All API endpoints return errors in this consistent format:

```json
{
  "success": false,
  "error": "Error category or type",
  "message": "User-friendly error description",
  "data": {
    "field1": "Field-specific error message",
    "field2": "Another field error"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### HTTP Status Code Standards

#### Client Errors (4xx)
- **400 Bad Request**: Invalid request format or data
- **401 Unauthorized**: Authentication required or failed
- **403 Forbidden**: Valid authentication but insufficient permissions
- **404 Not Found**: Requested resource does not exist
- **409 Conflict**: Resource conflict (duplicate email, etc.)
- **422 Unprocessable Entity**: Valid format but semantic errors
- **429 Too Many Requests**: Rate limit exceeded

#### Server Errors (5xx)
- **500 Internal Server Error**: Unhandled server error
- **502 Bad Gateway**: Upstream service unavailable
- **503 Service Unavailable**: Temporary service interruption
- **504 Gateway Timeout**: Upstream service timeout

### Security Error Handling
Security-related errors require special handling to prevent information disclosure:

```java
@ExceptionHandler(SecurityException.class)
public ResponseEntity<ApiResponse<Void>> handleSecurityException(SecurityException e) {
    // Log detailed error internally
    log.error("Security violation: {}", e.getMessage(), e);

    // Audit log the security event
    auditService.logSecurityEvent(
        "SECURITY_VIOLATION",
        e.getMessage(),
        AuditResult.FAILURE
    );

    // Return generic error to client
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("접근이 거부되었습니다."));
}
```

## Error Recovery Strategies

### Automatic Recovery
- **Token Refresh**: Automatic token renewal for expired tokens
- **Request Retry**: Automatic retry for transient network errors
- **Fallback Data**: Cached data display during service unavailability
- **Graceful Degradation**: Reduced functionality instead of complete failure

### User-Initiated Recovery
- **Retry Actions**: Manual retry buttons for failed operations
- **Alternative Paths**: Different ways to accomplish the same task
- **Help Documentation**: Context-sensitive help for error resolution
- **Support Contact**: Easy access to technical support

### Error Prevention
- **Input Validation**: Real-time validation to prevent errors
- **Confirmation Dialogs**: Confirmation for destructive actions
- **Loading States**: Clear feedback during async operations
- **Progressive Enhancement**: Feature availability based on system state

## Logging Strategy

### Error Log Levels
- **ERROR**: Application errors requiring immediate attention
- **WARN**: Potential issues that don't stop application function
- **INFO**: Important business events and state changes
- **DEBUG**: Detailed diagnostic information (development only)

### Log Content Standards
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "level": "ERROR",
  "logger": "com.nexus.controller.AuthController",
  "message": "Login failed for user",
  "userId": "user123",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "requestId": "req-uuid-123",
  "exception": {
    "type": "InvalidCredentialsException",
    "message": "Invalid password",
    "stackTrace": "..."
  }
}
```

### Security Logging
Security events require enhanced logging for audit and monitoring:

```java
// Security event logging
auditService.logSecurityEvent(
    "FAILED_LOGIN_ATTEMPT",
    String.format("Failed login for email: %s from IP: %s",
                  email, clientIpAddress),
    AuditResult.FAILURE
);

// Rate limit violation logging
auditService.logSecurityEvent(
    "RATE_LIMIT_EXCEEDED",
    String.format("Rate limit exceeded for IP: %s on endpoint: %s",
                  ipAddress, requestPath),
    AuditResult.FAILURE
);
```

## Monitoring & Alerting

### Error Rate Monitoring
- **Threshold Alerts**: Notifications when error rates exceed baselines
- **Trend Analysis**: Error pattern analysis over time
- **Impact Assessment**: User impact measurement for errors
- **Recovery Tracking**: Time to recovery monitoring

### Real-time Error Tracking
- **Error Aggregation**: Similar error grouping and counting
- **User Impact**: Affected user identification and notification
- **Performance Impact**: Error impact on system performance
- **Resolution Tracking**: Error fix deployment and verification

### Error Analytics
- **Error Classification**: Automatic error categorization
- **Root Cause Analysis**: Error source identification
- **User Journey Impact**: Error impact on user workflows
- **Improvement Opportunities**: Error reduction opportunities

## User Experience Considerations

### Error Message Design
- **Clear Language**: Non-technical, user-friendly messages
- **Actionable Guidance**: Specific steps for error resolution
- **Contextual Help**: Relevant assistance for the current task
- **Emotional Tone**: Empathetic and supportive messaging

### Error UI Components
- **Toast Notifications**: Non-intrusive error displays
- **Inline Validation**: Real-time form validation feedback
- **Error Pages**: Dedicated pages for significant errors
- **Loading States**: Clear feedback during error recovery

### Accessibility
- **Screen Reader Support**: ARIA labels for error messages
- **Color Independence**: Error indication beyond color alone
- **Keyboard Navigation**: Error handling via keyboard
- **Focus Management**: Proper focus handling after errors