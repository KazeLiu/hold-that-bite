import { AppSettings, BiteRecord, WeightEntry, SnackRefusalCount, DEFAULT_SETTINGS } from '../types';

const PREFIX = 'htb_';

function getKey(key: string): string {
  return `${PREFIX}${key}`;
}

export function loadSettings(): AppSettings {
  try {
    const raw = localStorage.getItem(getKey('settings'));
    if (raw) {
      return { ...DEFAULT_SETTINGS, ...JSON.parse(raw) };
    }
  } catch {
    // ignore
  }
  return { ...DEFAULT_SETTINGS };
}

export function saveSettings(settings: AppSettings): void {
  localStorage.setItem(getKey('settings'), JSON.stringify(settings));
}

export function loadRecords(): BiteRecord[] {
  try {
    const raw = localStorage.getItem(getKey('records'));
    if (raw) {
      return JSON.parse(raw);
    }
  } catch {
    // ignore
  }
  return [];
}

export function saveRecords(records: BiteRecord[]): void {
  localStorage.setItem(getKey('records'), JSON.stringify(records));
}

export function loadWeights(): WeightEntry[] {
  try {
    const raw = localStorage.getItem(getKey('weights'));
    if (raw) {
      return JSON.parse(raw);
    }
  } catch {
    // ignore
  }
  return [];
}

export function saveWeights(weights: WeightEntry[]): void {
  localStorage.setItem(getKey('weights'), JSON.stringify(weights));
}

export function loadSnackRefusals(): SnackRefusalCount {
  try {
    const raw = localStorage.getItem(getKey('snack_refusals'));
    if (raw) {
      return JSON.parse(raw);
    }
  } catch {
    // ignore
  }
  return {};
}

export function saveSnackRefusals(refusals: SnackRefusalCount): void {
  localStorage.setItem(getKey('snack_refusals'), JSON.stringify(refusals));
}

export function clearAllData(): void {
  const keys = Object.keys(localStorage).filter((k) => k.startsWith(PREFIX));
  keys.forEach((k) => localStorage.removeItem(k));
}
