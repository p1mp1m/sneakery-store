<!-- 6285b810-afff-46ba-8c81-e63db5d6cff2 5cb5ee4f-7b24-4c5c-8880-05649795bde8 -->
# Rebuild CSS Structure - E-commerce Standard

## Cấu trúc đề xuất

```
sneakery-frontend/src/assets/styles/
├── 01-settings/
│   ├── _variables.css       # CSS Variables (colors, spacing, typography)
│   └── _tokens.css          # Design Tokens (shadows, transitions, z-index)
├── 02-base/
│   ├── _reset.css          # Normalize & box-sizing
│   ├── _typography.css     # Headings, paragraphs, links
│   └── _utilities.css      # Flex, grid, spacing, text utilities
├── 03-layout/
│   ├── _containers.css     # Max-width containers
│   └── _grid.css           # Grid systems
├── 04-components/
│   ├── _buttons.css        # All button variants
│   ├── _cards.css          # Card components
│   ├── _forms.css          # Input, select, checkbox, radio
│   ├── _tables.css         # Table styles
│   ├── _badges.css         # Status badges
│   ├── _modals.css         # Modal dialogs
│   ├── _alerts.css         # Alert/Toast notifications
│   ├── _loading.css        # Spinners, skeletons
│   └── _pagination.css     # Pagination controls
├── 05-admin/
│   ├── _admin-layout.css   # Sidebar, header, main content
│   ├── _admin-dashboard.css # Dashboard specific
│   ├── _admin-tables.css   # Enhanced admin tables
│   └── _admin-stats.css    # Stats cards, charts
├── 06-user/
│   ├── _user-layout.css    # Navbar, footer
│   ├── _user-home.css      # Homepage styles
│   ├── _user-products.css  # Product list/detail
│   ├── _user-cart.css      # Cart & checkout
│   └── _user-profile.css   # User dashboard
├── 07-themes/
│   └── _dark-theme.css     # Dark mode variables & overrides
└── main.css                # Master import file
```

## Implementation Strategy

### Phase 1: Settings & Tokens (Foundation)

**01-settings/_variables.css**

- Extract current CSS variables from style.css
- Organize into logical groups: Colors, Spacing, Typography, Borders, Shadows
- Improve naming convention (--color-primary, --space-4, --text-lg)

**01-settings/_tokens.css**

- Design tokens: gradients, transitions, z-index layers
- Enhanced color palette for dark theme
- Component-specific tokens

### Phase 2: Base Styles

**02-base/_reset.css**

- Modern CSS reset (box-sizing, margin, padding)
- Smooth scrolling, focus styles
- Accessibility improvements

**02-base/_typography.css**

- Font family imports (if needed)
- Heading styles (h1-h6)
- Paragraph, link, list styles
- Font weights and sizes

**02-base/_utilities.css**

- Flexbox utilities (.d-flex, .justify-center, .align-center)
- Grid utilities (.d-grid, .grid-cols-3)
- Spacing utilities (.mt-4, .mb-2, .gap-3)
- Text utilities (.text-center, .text-lg, .text-primary)
- Display utilities (.d-none, .d-block)

### Phase 3: Layout

**03-layout/_containers.css**

- .container variants (container, container-sm, container-lg)
- Max-width responsive containers
- Padding and centering

**03-layout/_grid.css**

- Product grids (.products-grid)
- Stats grids (.stats-grid)
- Responsive grid templates

### Phase 4: Components (Shared)

**04-components/_buttons.css**

- Base button styles (.btn)
- Variants: primary, secondary, outline, ghost, danger, success
- Sizes: sm, md, lg
- States: hover, active, disabled
- Icon buttons

**04-components/_cards.css**

- Base card (.card)
- Card sections: header, body, footer
- Enhanced cards (.card-enhanced, .card-gradient-border)
- Hover effects

**04-components/_forms.css**

- Form groups and labels
- Input, textarea, select styles
- Checkbox, radio custom styles
- Input states: focus, valid, invalid
- Enhanced inputs (.input-enhanced)

**04-components/_tables.css**

- Base table styles
- Responsive tables
- Striped, bordered variants
- Enhanced admin tables

**04-components/_badges.css**

- Status badges (success, warning, error, info, pending)
- Badge sizes
- Pulse animations

**04-components/_modals.css**

- Modal overlay
- Modal container and sizes
- Modal header, body, footer
- Animations

**04-components/_alerts.css**

- Alert variants (success, warning, error, info)
- Toast notifications
- Alert animations

**04-components/_loading.css**

- Spinners (various sizes)
- Skeleton loaders
- Loading dots
- Shimmer effects

**04-components/_pagination.css**

- Pagination controls
- Page info display
- Button states

### Phase 5: Admin-Specific Styles

**05-admin/_admin-layout.css**

- Sidebar styles (collapsed/expanded states)
- Admin header
- Main content area
- Dropdown menus
- Responsive behavior

**05-admin/_admin-dashboard.css**

- Dashboard header with decorations
- Profile card
- Welcome section
- Quick actions grid

**05-admin/_admin-tables.css**

- Enhanced table styles for admin
- Bulk actions bar
- Filters section
- Table row hover effects

**05-admin/_admin-stats.css**

- Stat cards (multiple variants)
- Chart containers
- Icon backgrounds with gradients
- Animated borders

### Phase 6: User-Specific Styles

**06-user/_user-layout.css**

- Navbar (fixed, transparent with blur)
- Footer (multi-column)
- Social links
- User dropdown menu

**06-user/_user-home.css**

- Hero banner (2-column)
- Category cards
- Flash sale section
- Brand showcase
- Newsletter signup

**06-user/_user-products.css**

- Product cards
- Product grid
- Filter sidebar
- Product detail layout
- Image gallery, thumbnails

**06-user/_user-cart.css**

- Cart items list
- Cart summary
- Empty cart state
- Quantity controls

**06-user/_user-profile.css**

- User dashboard
- Profile stats
- Recent orders
- Action cards

### Phase 7: Dark Theme

**07-themes/_dark-theme.css**

- Dark color variables override
- Component dark mode adjustments
- Glassmorphism effects
- Purple accent colors
- Ensure consistency across all components

### Phase 8: Master Import

**main.css**

```css
/* Import in order */
@import '01-settings/_variables.css';
@import '01-settings/_tokens.css';
@import '02-base/_reset.css';
@import '02-base/_typography.css';
@import '02-base/_utilities.css';
/* ... all other imports in logical order */
```

## Migration Steps

1. Create new folder structure
2. Extract and organize content from current style.css into appropriate files
3. Update main.js to import new main.css
4. Delete old style.css
5. Test all pages (Admin: Dashboard, Products, Orders, Users, etc.)
6. Test all pages (User: Home, Products, Cart, Checkout, Profile)
7. Verify dark theme consistency
8. Optimize and remove duplicates

## Key Improvements

- **Modularity**: Each file has single responsibility
- **Maintainability**: Easy to find and update specific styles
- **Scalability**: Easy to add new components/pages
- **Performance**: Can lazy-load admin/user CSS if needed
- **Consistency**: Shared components ensure unified design
- **Dark Theme**: Centralized theme management
- **E-commerce optimized**: Structure suits product catalog, cart, checkout flows

## File Size Estimate

- Current: ~6765 lines in 1 file
- After: ~25 files, average 150-300 lines each
- Better organization, easier to navigate and maintain

### To-dos

- [ ] Create folder structure and empty files
- [ ] Extract and create 01-settings/_variables.css and _tokens.css
- [ ] Create 02-base/_reset.css, _typography.css, _utilities.css
- [ ] Create 03-layout/_containers.css and _grid.css
- [ ] Create 04-components/ (9 component files)
- [ ] Create 05-admin/ (4 admin-specific files)
- [ ] Create 06-user/ (5 user-specific files)
- [ ] Create 07-themes/_dark-theme.css
- [ ] Create main.css with all imports
- [ ] Update main.js to import new main.css
- [ ] Delete old style.css
- [ ] Test and verify all pages work correctly