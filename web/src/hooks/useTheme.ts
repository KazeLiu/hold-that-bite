import { useEffect } from 'react';
import { ThemeMode } from '../types';

export function useTheme(themeMode: ThemeMode): void {
  useEffect(() => {
    const root = document.documentElement;

    const applyTheme = (dark: boolean) => {
      if (dark) {
        root.classList.add('dark');
      } else {
        root.classList.remove('dark');
      }
    };

    if (themeMode === 'dark') {
      applyTheme(true);
    } else if (themeMode === 'light') {
      applyTheme(false);
    } else {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      applyTheme(mediaQuery.matches);

      const handler = (e: MediaQueryListEvent) => applyTheme(e.matches);
      mediaQuery.addEventListener('change', handler);
      return () => mediaQuery.removeEventListener('change', handler);
    }
  }, [themeMode]);
}
