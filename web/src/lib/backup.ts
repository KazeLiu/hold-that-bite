import { BackupPayload, AppSettings, BiteRecord, WeightEntry, SnackRefusalCount } from '../types';

export function createBackupPayload(
  settings: AppSettings,
  records: BiteRecord[],
  weights: WeightEntry[],
  snackRefusals: SnackRefusalCount
): BackupPayload {
  return {
    version: 1,
    exportedAt: Date.now(),
    settings,
    records,
    weights,
    snackRefusals,
  };
}

export function encodeBackup(payload: BackupPayload): string {
  return JSON.stringify(payload, null, 2);
}

export function decodeBackup(json: string): BackupPayload | null {
  try {
    const data = JSON.parse(json);
    if (!data.version || !data.settings || !Array.isArray(data.records)) {
      return null;
    }
    return {
      version: data.version,
      exportedAt: data.exportedAt || Date.now(),
      settings: data.settings,
      records: data.records,
      weights: Array.isArray(data.weights) ? data.weights : [],
      snackRefusals: data.snackRefusals || {},
    };
  } catch {
    return null;
  }
}

export function downloadBackup(payload: BackupPayload, filename?: string): void {
  const json = encodeBackup(payload);
  const blob = new Blob([json], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename || `hold-that-bite-backup-${new Date().toISOString().slice(0, 10)}.json`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

export function readBackupFile(file: File): Promise<BackupPayload | null> {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = decodeBackup(reader.result as string);
      resolve(result);
    };
    reader.onerror = () => resolve(null);
    reader.readAsText(file);
  });
}
