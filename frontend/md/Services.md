# Services

## API Service (`src/services/api.ts`)

### Base Configuration
- **Base URL**: `http://localhost:8080` (configurable via `NEXT_PUBLIC_API_BASE_URL`)
- **Default Headers**: `Content-Type: application/json`
- **Authentication**: Automatic `Authorization: Bearer <token>` injection from `authStore`
- **Error Handling**: Automatic error parsing and logging
- **Response Format**: Standardized API response structure

### Core Function
```typescript
apiFetch(url: string, options: RequestInit = {}) => Promise<ApiResponse<T>>
```

### Game Room Endpoints
- `getGameRooms()` → `GET /api/games`
  - Public endpoint (no authentication required)
  - Returns list of available game rooms

- `getGameRoomDetails(roomCode: string)` → `GET /api/games/{roomCode}`
  - Public endpoint for room details
  - Returns specific room information

- `createGameRoom(title: string, maxParticipants: number)` → `POST /api/games`
  - Requires authentication
  - Creates new game room with specified parameters

- `joinGameRoom(roomCode: string)` → `POST /api/games/{roomCode}/join`
  - Requires authentication
  - Joins user to existing game room

- `startTeamComposition(roomCode: string, method: 'AUTO' | 'AUCTION')` → `POST /api/games/{roomCode}/team-composition`
  - Requires authentication (room owner only)
  - Initiates team composition process

## Authentication Service (`src/services/auth.ts`)

### Base Configuration
- **Base URL**: Same as main API service
- **Headers**: Automatic Bearer token injection
- **Error Handling**: Specific auth error handling
- **Validation**: Client-side input validation

### Authentication Endpoints

#### Email Verification
- `requestEmailCode(email: string)` → `POST /api/auth/email/code`
  - Sends verification code to email
  - Rate limited: 10 requests per minute per IP

- `verifyEmailCode(email: string, code: string)` → `POST /api/auth/email/verify`
  - Verifies email with received code
  - Returns verification status

#### Validation Endpoints
- `checkNickname(nickname: string)` → `POST /api/auth/check/nickname`
  - Checks nickname availability
  - Returns boolean availability status

- `checkLolTag(lolTag: string)` → `POST /api/auth/check/loltag`
  - Validates LoL tag format and availability
  - Returns validation status

#### Registration & Login
- `registerUser(payload: RegisterPayload)` → `POST /api/auth/register`
  - Creates new user account
  - Requires email verification and agreement acceptance

- `login(email: string, password: string)` → `POST /api/auth/login`
  - Authenticates user and returns JWT token
  - Rate limited: 10 requests per minute per IP

#### User Management
- `completeOnboarding(payload: OnboardingPayload)` → `POST /api/auth/onboarding`
  - Completes user setup process
  - Updates user profile information

- `syncMyUser()` → `GET /api/users/me`
  - Retrieves current user profile
  - Requires valid JWT token

### Data Types

```typescript
interface RegisterPayload {
  email: string;
  nickname: string;
  password: string;
  lolTag: string;
  agreements: {
    terms: boolean;
    privacy: boolean;
    marketing?: boolean;
  };
}

interface UserProfile {
  id: number;
  email: string;
  nickname: string;
  summonerName: string;
}

interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
  timestamp: string;
}
```

### Security Features
- **Input Sanitization**: Client-side XSS prevention
- **Rate Limiting**: API endpoint throttling
- **Token Management**: Automatic token refresh handling
- **Error Logging**: Security event tracking
- **Validation**: Email, nickname, and LoL tag format validation

### Error Handling
All services implement consistent error handling:
- Network errors are caught and logged
- API errors are parsed from response
- User-friendly error messages are provided
- Security violations are reported to audit system