# Nexus Frontend Overview

## Tech Stack
- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: SCSS modules + Material-UI
- **UI Library**: MUI (@mui/material) with custom theme
- **State Management**: Zustand (authStore)
- **Authentication**: Local JWT-based authentication (no OAuth dependency)
- **API Communication**: Axios with centralized API service
- **API Base URL**: `http://localhost:8080`

## Project Structure
```
src/
├── app/              # Next.js App Router pages
│   ├── login/        # Login page
│   ├── register/     # Registration wizard
│   ├── oauth/        # OAuth callback handler (legacy)
│   ├── onboarding/   # Post-registration setup
│   └── lobbies/      # Game room management
├── components/       # Reusable UI components
│   ├── layout/       # Layout components (Header, Sidebar, AppLayout)
│   ├── auth/         # Authentication-related components
│   └── ui/           # General UI components
├── services/         # API layer and external services
│   ├── api.ts        # Main API service with auto token injection
│   └── auth.ts       # Authentication-specific endpoints
├── stores/           # Global state management
│   └── authStore.ts  # Authentication state (Zustand)
└── theme/            # Material-UI theme configuration
```

## Authentication Architecture
- **Local Authentication**: Email/password based system
- **JWT Tokens**: Access tokens with 1-hour expiration
- **State Management**: Zustand store for auth state persistence
- **Protected Routes**: Automatic authentication checks
- **Token Management**: Automatic Bearer token injection in API calls

## Security Features
- **Rate Limiting**: API request throttling by endpoint type
- **Input Validation**: XSS and SQL injection prevention
- **Security Headers**: CSP, HSTS, X-Frame-Options implementation
- **Audit Logging**: All user actions and security events tracked
- **Session Management**: Secure JWT token handling with blacklisting

## Key Application Flows

### Authentication Flow
1. User enters email/password on login page
2. Frontend validates input format
3. API call to `/api/auth/login` with credentials
4. Backend validates and returns JWT token + user info
5. Token stored in Zustand store
6. Automatic token injection in subsequent API calls

### Registration Flow
1. Email verification code request
2. User fills registration form (email, nickname, password, LoL tag)
3. Form validation (client + server side)
4. API call to `/api/auth/register`
5. Account creation with audit logging
6. Automatic login after successful registration

### Game Room Management
1. Room listing from `/api/games` (public endpoint)
2. Authenticated room creation and joining
3. Real-time room state management
4. Team composition with AUTO/AUCTION methods

## API Response Format
All API responses follow a consistent structure:
```typescript
{
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
  timestamp: string;
}
```