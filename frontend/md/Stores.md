# Stores

## Auth Store (`src/stores/authStore.ts`)

### State Management with Zustand
Centralized authentication state management using Zustand for simplicity and performance.

### State Structure
```typescript
interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  username: string;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  setAuth: (token: string, username?: string) => void;
}
```

### State Properties
- **`isAuthenticated`**: Boolean flag indicating user authentication status
- **`token`**: JWT access token for API authentication (null when not authenticated)
- **`username`**: Currently logged-in user's display name

### Actions

#### `login(email: string, password: string)`
- **Purpose**: Authenticates user with email/password credentials
- **Process**:
  1. Validates input format (email, password)
  2. Makes API call to `/api/auth/login`
  3. Handles authentication response
  4. Updates store state with token and user info
  5. Throws error on authentication failure
- **Error Handling**: Throws descriptive error messages for failed login attempts
- **Rate Limiting**: Subject to backend rate limiting (10 requests/minute per IP)

#### `logout()`
- **Purpose**: Clears authentication state and logs out user
- **Process**:
  1. Clears all authentication state
  2. Removes token from memory
  3. Resets user information
- **Security**: Token should be blacklisted on backend (future enhancement)

#### `setAuth(token: string, username?: string)`
- **Purpose**: Sets authentication state programmatically
- **Use Cases**:
  - Setting auth state after successful registration
  - Restoring auth state from persistent storage
  - Manual token refresh scenarios
- **Parameters**:
  - `token`: JWT access token
  - `username`: Optional user display name

### Integration with Services

#### Automatic Token Injection
The auth store integrates seamlessly with the API service layer:
```typescript
// In api.ts
const token = useAuthStore.getState().token;
if (token) {
  headers['Authorization'] = `Bearer ${token}`;
}
```

#### Authentication State Persistence
- **Client-Side**: State persists during browser session
- **Storage**: Memory-based (resets on page refresh)
- **Future Enhancement**: LocalStorage/SessionStorage integration for persistence

### Security Considerations

#### Token Management
- **Storage**: Tokens stored in memory only (not in localStorage for security)
- **Expiration**: Access tokens expire after 1 hour
- **Refresh**: Manual re-authentication required when token expires
- **Validation**: Token format and expiration validated on each API call

#### State Protection
- **XSS Protection**: No persistent storage prevents XSS token theft
- **CSRF Protection**: JWT tokens provide inherent CSRF protection
- **State Validation**: Auth state validated before sensitive operations

### Usage Examples

#### Login Flow
```typescript
import { useAuthStore } from '@/stores/authStore';

const { login, isAuthenticated } = useAuthStore();

try {
  await login('user@example.com', 'password123');
  // User is now authenticated
  console.log('Login successful');
} catch (error) {
  console.error('Login failed:', error.message);
}
```

#### Protected Route Check
```typescript
import { useAuthStore } from '@/stores/authStore';

const { isAuthenticated, token } = useAuthStore();

if (!isAuthenticated || !token) {
  // Redirect to login page
  router.push('/login');
  return;
}
```

#### Logout
```typescript
import { useAuthStore } from '@/stores/authStore';

const { logout } = useAuthStore();

logout();
// User is now logged out, state cleared
```

### Future Enhancements
- **Refresh Token Support**: Automatic token refresh before expiration
- **Persistent Storage**: Optional localStorage integration with encryption
- **Multi-tab Sync**: Authentication state synchronization across browser tabs
- **Session Timeout**: Automatic logout after inactivity period