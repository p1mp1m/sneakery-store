/**
 * Logger Utility
 * Centralized logging with environment-aware behavior
 */

const isDevelopment = import.meta.env.DEV;
const isProduction = import.meta.env.PROD;

class Logger {
  log(...args) {
    if (isDevelopment) {
      console.log(...args);
    }
  }

  info(...args) {
    if (isDevelopment) {
      console.info(...args);
    }
  }

  warn(...args) {
    if (isDevelopment || isProduction) {
      console.warn(...args);
    }
  }

  error(...args) {
    // Always log errors, even in production
    console.error(...args);
  }

  debug(...args) {
    if (isDevelopment) {
      console.debug(...args);
    }
  }

  group(...args) {
    if (isDevelopment) {
      console.group(...args);
    }
  }

  groupEnd() {
    if (isDevelopment) {
      console.groupEnd();
    }
  }
}

export default new Logger();

