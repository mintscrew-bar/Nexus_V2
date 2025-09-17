# API Documentation

## Base Configuration

### API Service Architecture
- **Base URL**: `http://localhost:8080` (configurable via `NEXT_PUBLIC_API_BASE_URL`)
- **Authentication**: JWT Bearer token automatic injection
- **Response Format**: Standardized API response structure
- **Error Handling**: Comprehensive error parsing and user feedback
- **Rate Limiting**: Backend-enforced request throttling

### Standard Response Format
All API endpoints return responses in this format:
```typescript
interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
  timestamp: string;
}
```

## Authentication Endpoints

### Email Verification
#### `POST /api/auth/email/code`
Request email verification code for registration.

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "인증 코드가 발송되었습니다.",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Rate Limiting:** 10 requests per minute per IP
**Validation:** Email format validation

#### `POST /api/auth/email/verify`
Verify email with received code.

**Request Body:**
```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "data": true,
  "message": "이메일 인증이 완료되었습니다.",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Validation Endpoints
#### `POST /api/auth/check/nickname`
Check nickname availability.

**Request Body:**
```json
{
  "nickname": "PlayerName"
}
```

**Response:**
```json
{
  "success": true,
  "data": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Validation Rules:**
- 2-20 characters
- Korean, English, numbers, underscore only
- XSS/SQL injection protection

#### `POST /api/auth/check/loltag`
Validate LoL tag format.

**Request Body:**
```json
{
  "lolTag": "PlayerName#KR1"
}
```

**Response:**
```json
{
  "success": true,
  "data": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Validation Rules:**
- Format: `SummonerName#TagCode`
- Summoner name: 3-16 characters
- Tag code: 2-4 uppercase letters + optional digits

### User Registration
#### `POST /api/auth/register`
Create new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "nickname": "PlayerName",
  "password": "SecurePassword123!",
  "lolTag": "PlayerName#KR1",
  "agreements": {
    "terms": true,
    "privacy": true,
    "marketing": false
  }
}
```

**Response:**
```json
{
  "success": true,
  "data": 123,
  "message": "회원가입이 완료되었습니다.",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Security Features:**
- Email uniqueness validation
- Nickname uniqueness validation
- Password complexity requirements
- Input sanitization
- Audit logging

### User Authentication
#### `POST /api/auth/login`
Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "username": "PlayerName",
    "email": "user@example.com"
  },
  "message": "로그인 성공",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Security Features:**
- Rate limiting: 10 attempts per minute per IP
- Failed attempt logging
- IP blocking after 5 failed attempts in 15 minutes
- Password validation
- Audit logging

## User Management Endpoints

### User Profile
#### `GET /api/users/me`
Get current user profile information.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "email": "user@example.com",
    "nickname": "PlayerName",
    "summonerName": "PlayerName#KR1"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Authentication:** Required (JWT token)

## Game Room Endpoints

### Room Listing
#### `GET /api/games`
Get list of available game rooms.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "내전방 제목",
      "roomCode": "ABC123",
      "currentParticipants": 5,
      "maxParticipants": 10,
      "status": "WAITING",
      "createdAt": "2024-01-01T12:00:00Z"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Authentication:** Not required (public endpoint)

### Room Details
#### `GET /api/games/{roomCode}`
Get specific room information.

**Parameters:**
- `roomCode`: Unique room identifier

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "내전방 제목",
    "roomCode": "ABC123",
    "currentParticipants": 5,
    "maxParticipants": 10,
    "status": "WAITING",
    "participants": [
      {
        "id": 123,
        "nickname": "PlayerName",
        "role": "OWNER"
      }
    ],
    "createdAt": "2024-01-01T12:00:00Z"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Room Creation
#### `POST /api/games`
Create new game room.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "title": "내전방 제목",
  "maxParticipants": 10
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "내전방 제목",
    "roomCode": "ABC123",
    "currentParticipants": 1,
    "maxParticipants": 10,
    "status": "WAITING"
  },
  "message": "게임 방이 생성되었습니다.",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Authentication:** Required
**Rate Limiting:** 60 requests per minute per IP

### Room Joining
#### `POST /api/games/{roomCode}/join`
Join existing game room.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Parameters:**
- `roomCode`: Room to join

**Response:**
```json
{
  "success": true,
  "message": "게임 방에 참여하셨습니다.",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Authentication:** Required
**Validation:** Room capacity and status checks

### Team Composition
#### `POST /api/games/{roomCode}/team-composition`
Start team composition process.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "method": "AUTO"
}
```

**Parameters:**
- `method`: "AUTO" or "AUCTION"

**Response:**
```json
{
  "success": true,
  "message": "팀 구성이 시작되었습니다.",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Authentication:** Required (room owner only)

## Error Handling

### Error Response Format
```json
{
  "success": false,
  "error": "Error description",
  "message": "User-friendly message",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Common HTTP Status Codes
- **200 OK**: Successful request
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict (duplicate email/nickname)
- **429 Too Many Requests**: Rate limit exceeded
- **500 Internal Server Error**: Server error

### Security Error Types
- **Rate Limit Exceeded**: Too many requests
- **Invalid Token**: JWT token invalid or expired
- **Malicious Input**: XSS/SQL injection attempt detected
- **Authentication Failed**: Invalid credentials
- **Authorization Failed**: Insufficient permissions

## Rate Limiting

### Endpoint-Specific Limits
- **General APIs**: 60 requests per minute per IP
- **Authentication APIs**: 10 requests per minute per IP
- **Sensitive Operations**: 5 requests per minute per IP

### Rate Limit Headers
Response includes rate limiting information:
```
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 59
X-RateLimit-Reset: 1640995200
```

## Security Features

### Request Security
- **Input Sanitization**: XSS and SQL injection prevention
- **CSRF Protection**: JWT tokens provide CSRF protection
- **CORS Configuration**: Restricted origin access
- **Security Headers**: CSP, HSTS, X-Frame-Options

### Authentication Security
- **JWT Tokens**: Secure token-based authentication
- **Token Expiration**: 1-hour access token lifecycle
- **Blacklist Support**: Token revocation capability
- **Audit Logging**: All authentication events logged

### Data Protection
- **Sensitive Data Encryption**: AES-256-GCM encryption
- **Password Hashing**: BCrypt with high cost factor
- **Input Validation**: Comprehensive validation rules
- **Error Information**: Minimal error information disclosure