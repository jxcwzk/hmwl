# iOS Design Guidelines Reference

## Core Design Principles

### 1. Clarity
- Text should be legible at all sizes
- Icons should be precise and clear
- Adornments should be subtle and appropriate
- Focus on content, not decoration

### 2. Deference to Content
- Fluid motion helps people understand the results of their actions
- Crisp, beautiful images and graphics enhance the experience
- The interface should never compete with the content

### 3. Depth
- Visual layers and realistic motion convey hierarchy and vitality
- Translucent materials blur the content behind them
- Shadows and elevation create a sense of depth

## Color System

### iOS System Colors
```css
/* Primary Colors */
--ios-blue: #007AFF;
--ios-green: #34C759;
--ios-indigo: #5856D6;
--ios-orange: #FF9500;
--ios-pink: #FF2D55;
--ios-purple: #AF52DE;
--ios-red: #FF3B30;
--ios-teal: #5AC8FA;
--ios-yellow: #FFCC00;

/* Gray Scale */
--ios-gray: #8E8E93;
--ios-gray2: #AEAEB2;
--ios-gray3: #C7C7CC;
--ios-gray4: #D1D1D6;
--ios-gray5: #E5E5EA;
--ios-gray6: #F2F2F7;

/* Semantic Colors */
--ios-background: #F2F2F7;
--ios-surface: #FFFFFF;
--ios-text-primary: #000000;
--ios-text-secondary: #3C3C43;
--ios-text-tertiary: #3C3C4399;
--ios-separator: #C6C6C8;
```

### Color Usage Guidelines
- Use system colors for consistency
- Maintain proper contrast ratios (4.5:1 for normal text)
- Use color semantically (blue for primary actions, red for destructive)
- Consider accessibility and color blindness

## Typography

### Font Family
```css
font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
```

### Type Scale
```css
/* Large Titles */
--ios-large-title: 34px;   /* Bold, 41px line-height */

/* Titles */
--ios-title1: 28px;         /* Bold, 34px line-height */
--ios-title2: 22px;         /* Bold, 28px line-height */
--ios-title3: 20px;         /* Semibold, 25px line-height */

/* Headline */
--ios-headline: 17px;       /* Semibold, 22px line-height */

/* Body */
--ios-body: 17px;           /* Regular, 22px line-height */
--ios-callout: 16px;        /* Regular, 21px line-height */
--ios-subhead: 15px;        /* Regular, 20px line-height */
--ios-footnote: 13px;       /* Regular, 18px line-height */

/* Caption */
--ios-caption1: 12px;       /* Regular, 16px line-height */
--ios-caption2: 11px;       /* Regular, 13px line-height */
```

### Typography Guidelines
- Use appropriate type scale for hierarchy
- Maintain proper line height (1.2-1.5)
- Use font weight for emphasis, not size alone
- Ensure legibility at all sizes

## Spacing System

### 8pt Grid System
```css
--ios-spacing-xs: 4px;
--ios-spacing-sm: 8px;
--ios-spacing-md: 16px;
--ios-spacing-lg: 24px;
--ios-spacing-xl: 32px;
--ios-spacing-2xl: 48px;
```

### Spacing Guidelines
- Use consistent spacing throughout
- Maintain visual rhythm
- Use larger spacing for section breaks
- Keep related elements closer together

## Border Radius

### Corner Radius
```css
--ios-radius-xs: 4px;
--ios-radius-sm: 8px;
--ios-radius-md: 12px;
--ios-radius-lg: 16px;
--ios-radius-xl: 20px;
--ios-radius-2xl: 28px;
--ios-radius-full: 9999px;
```

### Radius Guidelines
- Use 8-12px for cards and buttons
- Use 4-8px for small elements
- Use 16-20px for large containers
- Use full radius for circular elements

## Shadows & Elevation

### Shadow System
```css
--ios-shadow-xs: 0 1px 2px rgba(0,0,0,0.05);
--ios-shadow-sm: 0 2px 4px rgba(0,0,0,0.1);
--ios-shadow-md: 0 4px 6px rgba(0,0,0,0.1);
--ios-shadow-lg: 0 10px 15px rgba(0,0,0,0.1);
--ios-shadow-xl: 0 20px 25px rgba(0,0,0,0.15);
```

### Shadow Guidelines
- Use subtle shadows for depth
- Larger shadows for elevated elements
- Consistent shadow direction (top-left light source)
- Avoid harsh, dark shadows

## Animations & Transitions

### Timing Functions
```css
--ios-ease-out: cubic-bezier(0.0, 0.0, 0.2, 1);
--ios-ease-in-out: cubic-bezier(0.4, 0.0, 0.2, 1);
--ios-spring: cubic-bezier(0.175, 0.885, 0.32, 1.275);
```

### Duration Guidelines
```css
--ios-duration-fast: 150ms;
--ios-duration-normal: 300ms;
--ios-duration-slow: 500ms;
```

### Animation Guidelines
- Use smooth, natural animations
- Keep animations under 500ms
- Use spring physics for natural feel
- Provide feedback for user actions

## Component Guidelines

### Buttons
- Minimum touch target: 44x44px
- Rounded corners: 12px
- Subtle shadows for depth
- Smooth hover/active states

### Cards
- White background on gray background
- Rounded corners: 12-16px
- Subtle shadows
- Consistent padding: 16-24px

### Inputs
- Clean, minimal borders
- iOS-style focus states
- Proper error states
- Smooth validation feedback

### Navigation
- Large, clear titles
- Smooth page transitions
- Consistent back button behavior
- Proper hierarchy

## Layout Guidelines

### Safe Areas
- Respect device safe areas
- Account for notches and home indicators
- Maintain proper margins
- Test on different devices

### Responsive Design
- Use flexible layouts
- Implement proper breakpoints
- Maintain consistency across sizes
- Optimize for touch targets

## Accessibility

### Color Contrast
- Normal text: 4.5:1 ratio
- Large text: 3:1 ratio
- UI components: 3:1 ratio

### Touch Targets
- Minimum size: 44x44px
- Adequate spacing between targets
- Clear visual feedback
- Proper hit testing

### Screen Reader Support
- Proper semantic HTML
- ARIA labels where needed
- Logical reading order
- Descriptive alt text

## Performance

### Optimization
- Use hardware acceleration
- Optimize images and assets
- Minimize repaints and reflows
- Use efficient animations

### Loading States
- Show loading indicators
- Use skeleton screens
- Provide feedback
- Handle errors gracefully

## Testing

### Device Testing
- Test on real iOS devices
- Check different screen sizes
- Test in different orientations
- Verify performance

### User Testing
- Conduct usability tests
- Gather feedback
- Iterate based on results
- Measure success metrics