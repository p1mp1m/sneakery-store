# Sneakery Store - Frontend

Frontend application for Sneakery Store built with Vue 3, Vite, and Tailwind CSS.

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ and npm/yarn
- Backend API running on `http://localhost:8080` (or configure via environment variables)

### Installation

1. Install dependencies:
```bash
npm install
```

2. Create `.env` file (copy from `.env.example`):
```bash
cp .env.example .env
```

3. Configure environment variables in `.env`:
```env
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Sneakery Store
VITE_APP_VERSION=1.0.0
```

### Development

Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### Build

Build for production:
```bash
npm run build
```

The production build will be in the `dist/` directory.

### Preview Production Build

Preview the production build locally:
```bash
npm run preview
```

## 📁 Project Structure

```
sneakery-frontend/
├── src/
│   ├── assets/          # Static assets (images, components, layouts, styles)
│   ├── components/      # Reusable Vue components
│   ├── composables/     # Vue composition functions
│   ├── config/          # Configuration files (API endpoints)
│   ├── routers/         # Vue Router configuration
│   ├── services/        # API service layer
│   ├── stores/          # Pinia state management
│   ├── utils/           # Utility functions
│   └── views/           # Page components
├── public/              # Public static files
├── .env.example         # Environment variables template
├── package.json         # Dependencies and scripts
├── vite.config.js       # Vite configuration
└── tailwind.config.js   # Tailwind CSS configuration
```

## 🛠️ Tech Stack

- **Vue 3** - Progressive JavaScript framework
- **Vite** - Next generation frontend tooling
- **Pinia** - State management
- **Vue Router** - Routing
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client
- **Chart.js** - Charts and graphs
- **Material Icons** - Icon library

## 🔧 Configuration

### API Configuration

API endpoints are centralized in `src/config/api.js`. The configuration automatically handles:
- Development: Uses Vite proxy (no need for full URL)
- Production: Uses `VITE_API_URL` environment variable

### Environment Variables

- `VITE_API_URL` - Backend API URL (default: `http://localhost:8080`)
- `VITE_APP_NAME` - Application name
- `VITE_APP_VERSION` - Application version

## 📝 Code Quality

### Logging

Use the centralized logger utility instead of `console.*`:
```javascript
import logger from '@/utils/logger'

logger.log('Info message')
logger.warn('Warning message')
logger.error('Error message')
```

### API Calls

Always use centralized API config:
```javascript
import { API_ENDPOINTS, buildApiUrl } from '@/config/api'

// Use API_ENDPOINTS for endpoints
const response = await axios.get(API_ENDPOINTS.PRODUCTS.BASE)

// Use buildApiUrl for dynamic URLs
const url = buildApiUrl(API_ENDPOINTS.PRODUCTS.BY_ID(productId))
```

### Error Handling

- Use ErrorBoundary component for global error catching
- Use toastService for user-friendly error messages
- Always handle errors in try-catch blocks

### Security

- Always sanitize user input before rendering with `v-html`
- Use `escapeHtml()` or `highlightSearchSafely()` from `@/utils/sanitize`
- Validate all form inputs on both client and server side

## 🎨 Styling

The project uses Tailwind CSS with custom design tokens defined in:
- `src/assets/styles/_design-tokens.css` - CSS variables
- `tailwind.config.js` - Tailwind configuration

### Dark Mode

Dark mode is supported via Tailwind's `dark:` prefix. The theme is managed by the `theme` store.

## 📦 Key Features

- **Authentication** - Login, register, password reset
- **Product Management** - Browse, search, filter products
- **Shopping Cart** - Add to cart, checkout (guest and authenticated)
- **Wishlist** - Save favorite products
- **Orders** - View order history
- **Admin Panel** - Full admin dashboard for managing products, orders, users
- **Responsive Design** - Mobile-first approach
- **Dark Mode** - Full dark mode support

## 🔒 Security Best Practices

1. **Input Validation**: All forms have client-side validation
2. **XSS Protection**: User input is sanitized before rendering
3. **Authentication**: JWT tokens stored securely in localStorage
4. **API Security**: All API calls include authentication headers
5. **Error Handling**: Errors are logged but sensitive info is not exposed

## 🐛 Troubleshooting

### API Connection Issues

- Check that backend is running on the configured port
- Verify `VITE_API_URL` in `.env` file
- Check browser console for CORS errors

### Build Issues

- Clear `node_modules` and reinstall: `rm -rf node_modules && npm install`
- Clear Vite cache: `rm -rf .vite && npm run dev`

## 📄 License

This project is proprietary software.

