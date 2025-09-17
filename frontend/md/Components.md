# Components

## Layout Components

### AppLayout (`components/layout/AppLayout.tsx`)
**Main application shell providing consistent layout structure.**

- **Purpose**: Wraps all pages with header, sidebar, and main content area
- **Features**:
  - Responsive layout with collapsible sidebar
  - Authentication-aware component rendering
  - Consistent spacing and Material-UI integration
- **Styling**: `components/layout/AppLayout.module.scss`
- **Dependencies**: Header, Sidebar components
- **State Management**: Integrates with auth store for user state

### Header (`components/layout/Header.tsx`)
**Top navigation bar with user controls and branding.**

- **Features**:
  - Application branding and logo
  - User authentication status display
  - Profile dropdown with logout functionality
  - Responsive design for mobile devices
- **Authentication Integration**:
  - Shows login/register buttons for unauthenticated users
  - Displays user info and logout for authenticated users
  - Real-time auth state updates
- **Styling**: `components/layout/Header.module.scss`
- **Material-UI Components**: AppBar, Toolbar, Button, Avatar

### Sidebar (`components/layout/Sidebar.tsx`)
**Application navigation menu with route management.**

- **Features**:
  - Primary navigation links
  - Authentication-aware menu items
  - Active route highlighting
  - Collapsible design for mobile
- **Navigation Items**:
  - Home / Dashboard
  - Game Lobbies
  - User Profile (authenticated only)
  - Admin Panel (admin users only)
- **Styling**: `components/layout/Sidebar.module.scss`
- **Material-UI Components**: Drawer, List, ListItem, Icon

## Authentication Components

### OAuthButtons (`components/auth/OAuthButtons.tsx`)
**OAuth provider authentication buttons.**

- **Purpose**: Provides social login options (Google, Discord)
- **Status**: Currently unused in local auth implementation
- **Future Use**: Reserved for OAuth integration enhancement
- **Features**:
  - Provider-specific branding
  - Error handling for OAuth failures
  - Redirect management after authentication

### RegisterWizard (`components/auth/RegisterWizard.tsx`)
**Multi-step registration form with validation.**

- **Features**:
  - Step-by-step registration process
  - Real-time form validation
  - Email verification integration
  - LoL tag validation and checking
  - Terms and privacy agreement
- **Steps**:
  1. Email verification
  2. Basic information (nickname, password)
  3. LoL account information
  4. Terms and agreements
- **Validation**:
  - Client-side input validation
  - Server-side availability checking
  - Password strength requirements
  - Email format validation

## UI Components

### EmptyState (`components/ui/EmptyState.tsx`)
**Consistent empty state display component.**

- **Purpose**: Shows placeholder content when data is unavailable
- **Use Cases**:
  - Empty game room lists
  - No search results
  - Loading states
- **Features**:
  - Customizable icon and message
  - Call-to-action button support
  - Consistent styling with app theme

### LoadingOverlay (`components/ui/LoadingOverlay.tsx`)
**Loading state management component.**

- **Purpose**: Provides loading feedback during async operations
- **Features**:
  - Backdrop overlay with loading spinner
  - Customizable loading messages
  - Non-blocking UI for better UX
- **Use Cases**:
  - API request loading
  - Form submission feedback
  - Page transition loading

## Business Logic Components

### GameRoomList (`components/GameRoomList.tsx`)
**Game room listing and management component.**

- **Features**:
  - Displays available game rooms
  - Real-time room status updates
  - Join room functionality
  - Room filtering and search
- **Integration**:
  - API service for room data
  - Authentication for join actions
  - Real-time WebSocket updates (future)
- **Data Display**:
  - Room title and description
  - Current player count
  - Room status (waiting, in-progress, full)
  - Join button with auth check

### CreateRoomModal (`components/CreateRoomModal.tsx`)
**Modal dialog for creating new game rooms.**

- **Features**:
  - Form for room configuration
  - Real-time validation
  - Integration with room creation API
- **Form Fields**:
  - Room title (required)
  - Maximum participants
  - Room description
  - Game mode selection
- **Validation**:
  - Title length and format
  - Participant count limits
  - Authentication requirement check

## Theme Integration

### ThemeRegistry (`components/ThemeRegistry/ThemeRegistry.tsx`)
**Material-UI theme provider setup.**

- **Purpose**: Configures Material-UI theme across the application
- **Features**:
  - Dark mode theme configuration
  - Custom color palette
  - Typography settings
  - Component styling overrides
- **Theme Configuration**:
  - Primary color: `#90caf9` (blue)
  - Secondary color: `#f48fb1` (pink)
  - Background: Dark theme (`#121212`)
  - Paper: `#1e1e1e`

## Component Architecture

### Design Patterns

#### Composition Pattern
Components are designed for reusability and composition:
```tsx
<AppLayout>
  <Header />
  <Sidebar />
  <MainContent>
    {children}
  </MainContent>
</AppLayout>
```

#### Container/Presentational Pattern
- **Container Components**: Handle state and business logic
- **Presentational Components**: Focus on UI rendering
- **Separation**: Clear division between data and presentation

#### Authentication Integration
Components automatically adapt based on authentication state:
```tsx
const { isAuthenticated, user } = useAuthStore();

return (
  <div>
    {isAuthenticated ? (
      <AuthenticatedView user={user} />
    ) : (
      <UnauthenticatedView />
    )}
  </div>
);
```

### Component Communication

#### Props Pattern
- **Parent-to-Child**: Props for data and event handlers
- **Type Safety**: TypeScript interfaces for prop validation
- **Default Props**: Sensible defaults for optional props

#### State Management
- **Local State**: Component-specific state with useState
- **Global State**: Authentication via Zustand store
- **Form State**: Form libraries for complex forms

#### Event Handling
- **User Interactions**: Click, form submission, navigation
- **API Events**: Success/error handling for async operations
- **Route Changes**: Navigation and route protection

### Styling Strategy

#### CSS Modules
- **Scoped Styles**: Prevents style conflicts
- **Module Naming**: `Component.module.scss` convention
- **Class Composition**: Combine with Material-UI classes

#### Material-UI Integration
- **Theme Consistency**: All components use MUI theme
- **Component Overrides**: Custom styling via theme
- **Responsive Design**: MUI breakpoint system

#### SCSS Features
- **Variables**: Consistent spacing and colors
- **Mixins**: Reusable style patterns
- **Nesting**: Organized style hierarchies