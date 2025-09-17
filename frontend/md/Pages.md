# Pages & Routes

## Route Structure
Next.js App Router with authentication-aware routing and protected pages.

### Public Routes
- **`/`** → `src/app/page.tsx`
  - Landing/home page
  - Accessible without authentication
  - Styles: `src/app/page.module.scss`

- **`/login`** → `src/app/login/page.tsx`
  - User authentication page
  - Email/password login form
  - Redirects to `/lobbies` on successful login
  - Rate limited: 10 attempts per minute per IP

- **`/register`** → `src/app/register/page.tsx`
  - User registration wizard
  - Multi-step form with validation
  - Email verification integration
  - Terms and privacy agreement

- **`/oauth/callback`** → `src/app/oauth/callback/page.tsx`
  - OAuth callback handler (legacy)
  - Currently unused in local auth implementation
  - Reserved for future OAuth integration

### Protected Routes
All protected routes require valid JWT authentication.

- **`/lobbies`** → `src/app/lobbies/page.tsx`
  - Game room listing and management
  - Create new rooms, join existing rooms
  - Real-time room status updates

- **`/lobbies/[roomCode]`** → `src/app/lobbies/[roomCode]/page.tsx`
  - Individual game room details
  - Room member management
  - Team composition controls (room owner only)
  - Styles: `src/app/lobbies/[roomCode]/GameRoomDetailPage.module.scss`

- **`/onboarding`** → `src/app/onboarding/page.tsx`
  - Post-registration user setup
  - Profile completion
  - LoL account linking

## Layout Structure

### Root Layout (`src/app/layout.tsx`)
- **Framework**: Next.js App Router layout
- **Font**: Inter font family (Google Fonts)
- **Theme**: Material-UI ThemeRegistry integration
- **Layout Component**: AppLayout wrapper
- **Metadata**: SEO configuration for Nexus platform
- **Global Styles**: `src/app/globals.scss`

### Layout Hierarchy
```
RootLayout
├── ThemeRegistry (MUI theme provider)
└── AppLayout (main application shell)
    ├── Header (navigation and user controls)
    ├── Sidebar (application navigation)
    └── Main Content (page-specific content)
```

## Page-Specific Features

### Authentication Pages

#### Login Page (`/login`)
- **Form Validation**: Client-side email/password validation
- **Error Handling**: User-friendly error messages
- **Security**: CSRF protection via JWT
- **Rate Limiting**: Backend enforcement
- **Redirect Logic**: Automatic redirection after successful login

#### Registration Page (`/register`)
- **Multi-Step Process**: Progressive form completion
- **Email Verification**: Real-time email code verification
- **Validation**: Real-time nickname and LoL tag availability
- **Password Security**: Client-side password strength validation
- **Terms Agreement**: Required privacy and terms acceptance

### Game Room Pages

#### Lobbies Listing (`/lobbies`)
- **Public Data**: Room list accessible without full authentication
- **Room Creation**: Authenticated users can create new rooms
- **Real-time Updates**: Live room status and participant counts
- **Filtering**: Search and filter room options

#### Room Details (`/lobbies/[roomCode]`)
- **Dynamic Routing**: Room code-based navigation
- **Member Management**: View room participants
- **Team Composition**: Automated or auction-based team creation
- **Owner Controls**: Room-specific management for room creators
- **Real-time State**: Live updates of room status changes

## Style Organization

### Global Styles (`src/app/globals.scss`)
- **CSS Reset**: Consistent cross-browser styling
- **Theme Variables**: SCSS variables for consistent theming
- **Typography**: Global font and text styling
- **Layout Helpers**: Common layout utilities

### Page-Specific Styles
- **Modular Approach**: Each page has corresponding `.module.scss`
- **CSS Modules**: Scoped styling to prevent conflicts
- **Responsive Design**: Mobile-first responsive layouts
- **Material-UI Integration**: Consistent with MUI theme system

### Style File Structure
```
src/app/
├── globals.scss                    # Global styles
├── page.module.scss               # Home page styles
└── lobbies/
    ├── page.module.scss           # Lobbies listing styles
    └── [roomCode]/
        └── GameRoomDetailPage.module.scss  # Room details styles
```

## Navigation & Routing

### Client-Side Navigation
- **Next.js Router**: Built-in routing with App Router
- **Programmatic Navigation**: `useRouter` for dynamic redirects
- **Route Protection**: Authentication checks before route access
- **State Preservation**: Maintain state during navigation

### Authentication Flow Integration
- **Route Guards**: Automatic redirect to login for protected routes
- **Login Redirects**: Return to intended route after authentication
- **Logout Handling**: Clear state and redirect to public routes
- **Session Timeout**: Handle expired tokens gracefully

### SEO & Meta Tags
- **Dynamic Metadata**: Page-specific titles and descriptions
- **Open Graph**: Social media sharing optimization
- **Structured Data**: Rich snippets for game platform
- **Viewport**: Mobile-responsive meta configuration