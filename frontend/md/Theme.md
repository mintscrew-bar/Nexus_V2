# Theme

## Material-UI Theme Configuration (`src/theme/theme.ts`)

### Theme Architecture
Comprehensive Material-UI theme setup providing consistent design system across the Nexus platform.

### Color Palette

#### Primary Colors
- **Main**: `#90caf9` (Light Blue)
  - Used for: Primary buttons, links, active states
  - Contrast: High contrast for accessibility
  - Variants: Light, main, dark automatically generated

#### Secondary Colors
- **Main**: `#f48fb1` (Pink)
  - Used for: Accent elements, secondary buttons, highlights
  - Purpose: Complementary color for visual hierarchy
  - Gaming aesthetic: Fits LoL/gaming platform branding

#### Background Colors
- **Default**: `#121212` (Dark Gray)
  - Primary background for all pages
  - Reduces eye strain for gaming sessions
  - Material Design dark theme standard

- **Paper**: `#1e1e1e` (Darker Gray)
  - Card backgrounds, modals, elevated surfaces
  - Creates depth and visual separation
  - Consistent with Material Design elevation

#### Text Colors
- **Primary**: High contrast white/light gray
- **Secondary**: Medium contrast for less important text
- **Disabled**: Low contrast for inactive elements

### Typography System

#### Font Family
- **Primary**: Inter (Google Fonts)
  - Modern, highly legible sans-serif
  - Optimized for digital interfaces
  - Excellent readability at all sizes

#### Font Weights
- **Light (300)**: Large headings, display text
- **Regular (400)**: Body text, descriptions
- **Medium (500)**: Subheadings, emphasis
- **Bold (700)**: Headings, important text

#### Type Scale
Material-UI's standardized type scale with custom adjustments:
- **h1-h6**: Hierarchical heading system
- **body1/body2**: Main content text
- **caption**: Small text, metadata
- **button**: Interactive element text

### Component Customization

#### Material-UI Component Overrides
Theme includes specific customizations for:

##### Buttons
- **Border Radius**: Slightly rounded for modern feel
- **Padding**: Comfortable touch targets
- **Hover States**: Smooth transitions
- **Focus Indicators**: Accessibility compliance

##### Cards
- **Elevation**: Consistent shadow system
- **Border Radius**: Uniform corner rounding
- **Background**: Paper color with transparency

##### Navigation
- **Active States**: Clear visual feedback
- **Hover Effects**: Subtle interaction feedback
- **Focus Management**: Keyboard navigation support

### Dark Mode Implementation

#### Design Philosophy
- **Gaming Focus**: Dark theme reduces eye strain during long gaming sessions
- **Modern Aesthetic**: Aligns with contemporary gaming platform design
- **Accessibility**: Maintains sufficient contrast ratios

#### Color Considerations
- **Contrast Ratios**: WCAG AA compliance minimum
- **Color Temperature**: Cooler tones for reduced eye strain
- **Hierarchy**: Clear visual hierarchy through color and contrast

### Responsive Design

#### Breakpoint System
Material-UI's standard breakpoint system:
- **xs**: 0px+ (mobile)
- **sm**: 600px+ (tablet)
- **md**: 900px+ (desktop)
- **lg**: 1200px+ (large desktop)
- **xl**: 1536px+ (extra large)

#### Typography Scaling
- **Mobile**: Smaller font sizes, adjusted line heights
- **Desktop**: Larger type scale, increased spacing
- **Dynamic Scaling**: Fluid typography between breakpoints

### Theme Usage

#### Component Integration
```tsx
import { useTheme } from '@mui/material/styles';

const MyComponent = () => {
  const theme = useTheme();

  return (
    <div style={{
      color: theme.palette.primary.main,
      backgroundColor: theme.palette.background.paper
    }}>
      Content
    </div>
  );
};
```

#### SCSS Integration
Theme values are available in SCSS through CSS custom properties:
```scss
.component {
  background-color: var(--mui-palette-background-paper);
  color: var(--mui-palette-text-primary);
}
```

### Gaming Platform Aesthetics

#### Visual Language
- **Modern Gaming**: Clean, high-tech aesthetic
- **LoL Integration**: Colors complementary to League of Legends branding
- **Professional**: Suitable for competitive gaming environment

#### Interactive Elements
- **Hover States**: Smooth transitions with color shifts
- **Focus States**: Clear keyboard navigation indicators
- **Active States**: Immediate visual feedback
- **Loading States**: Consistent animation patterns

### Accessibility Features

#### Color Accessibility
- **Contrast Ratios**: Minimum 4.5:1 for normal text
- **Color Independence**: Information not conveyed by color alone
- **Colorblind Support**: Distinguishable without color perception

#### Focus Management
- **Visible Focus**: Clear focus indicators
- **Logical Tab Order**: Keyboard navigation flow
- **Skip Links**: Navigation shortcuts for screen readers

#### Screen Reader Support
- **Semantic Elements**: Proper HTML structure
- **ARIA Labels**: Descriptive element labeling
- **State Communication**: Dynamic content updates

### Theme Customization

#### Extension Points
The theme is designed for easy customization:
- **Custom Palette**: Add brand-specific colors
- **Component Overrides**: Modify specific component styles
- **Typography**: Adjust font stacks and sizing
- **Spacing**: Modify the spacing scale

#### Environment Variations
- **Development**: Enhanced debugging features
- **Production**: Optimized for performance
- **Testing**: Consistent rendering across environments

### Performance Considerations

#### CSS-in-JS Optimization
- **Style Caching**: Efficient style generation
- **Tree Shaking**: Unused style removal
- **Critical CSS**: Above-fold style prioritization

#### Bundle Size
- **Selective Imports**: Only import used theme features
- **Component Splitting**: Lazy load theme-heavy components
- **Optimization**: Minimize theme object size