# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Development
- `npm run dev` - Start development server with turbopack (default: http://localhost:3000)
- `npm run build` - Build production application
- `npm start` - Start production server
- `npm run lint` - Run ESLint for code quality checks

### Environment Setup
- Frontend runs on port 3000 by default
- Backend API expected at `http://localhost:8080` (configurable via `NEXT_PUBLIC_API_BASE_URL`)
- Uses TypeScript with strict mode enabled
- SCSS modules for styling with path alias `@/*` pointing to `./src/*`

## Architecture Overview

### Tech Stack
- **Framework**: Next.js 14 with App Router
- **UI**: Material-UI (@mui/material) with Emotion styling
- **State Management**: Zustand for client state
- **HTTP Client**: Axios with custom API service layer
- **Authentication**: JWT tokens with Keycloak integration
- **Styling**: SCSS modules with Material-UI theming

### Key Architectural Patterns

#### Authentication Flow
- Local authentication with email/password login via `/api/auth/login`
- Registration with email verification system via `/api/auth/register`
- Email verification endpoints: `/api/auth/email/code` and `/api/auth/email/verify`
- Nickname and LoL tag validation endpoints: `/api/auth/check/nickname` and `/api/auth/check/loltag`
- JWT token management through `authStore` (Zustand)
- Authentication state persisted and managed globally
- Protected routes check `isAuthenticated` state

#### API Integration
- Centralized API service in `src/services/api.ts`
- Automatic Bearer token injection from auth store
- Base URL configuration via environment variables
- Error handling with detailed logging
- Separate auth service for authentication-specific endpoints

#### State Management
- Zustand store pattern for auth state (`src/stores/authStore.ts`)
- Store includes login, logout, and token management
- Direct state access without providers/context

#### Routing Structure
- App Router with nested layouts
- Route structure:
  - `/` - Landing page
  - `/login` - Login page
  - `/register` - Registration wizard
  - `/oauth/callback` - OAuth callback handler
  - `/onboarding` - Post-registration setup
  - `/lobbies` - Game room listings
  - `/lobbies/[roomCode]` - Individual game rooms

#### Component Organization
- Layout components: `AppLayout`, `Header`, `Sidebar`
- UI components: Reusable components in `src/components/ui/`
- Auth components: Authentication-related flows
- Business components: Game room management, user flows

### Backend Integration
- Spring Boot backend with PostgreSQL database
- RESTful API endpoints for game rooms, users, authentication
- Local JWT-based authentication (no OAuth2 dependency)
- Docker Compose setup for full-stack development
- CORS configuration for frontend-backend communication

### Game Room Features
- Room creation with customizable parameters
- Room joining via room codes
- Team composition with AUTO and AUCTION methods
- Real-time room state management
- League of Legends integration via summoner tags

## Development Guidelines

### File Structure Conventions
- Components use `.tsx` extension
- Styles use `.module.scss` for component-specific styles
- API services separated by domain (auth, game rooms, etc.)
- Absolute imports using `@/` prefix

### Authentication Integration
- Always use `useAuthStore` for auth state
- Check `isAuthenticated` before making authenticated requests
- Use `src/services/auth.ts` for auth-related API calls
- Use `src/services/api.ts` for general API operations

### Environment Variables
- `NEXT_PUBLIC_API_BASE_URL` - Backend API base URL
- Backend requires database credentials and JWT secrets
- No OAuth provider credentials needed (local authentication only)

### Theme and Styling
- Material-UI theme registry provides consistent theming
- SCSS modules for component-specific styles
- Inter font family used throughout application
- Korean language support configured