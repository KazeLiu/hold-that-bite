export type FastingPlan = '12+12' | '14+10' | '16+8' | '18+6' | '20+4';

export type BiteStatus = 'kept' | 'missed';

export type CalendarMode = 'month' | 'week';

export type ThemeMode = 'system' | 'light' | 'dark';

export type WeightUnit = 'kg' | 'jin';

export type FastingDayPhase = 'before_first_meal' | 'eating_window' | 'after_last_bite';

export interface BiteRecord {
  dateKey: string;
  status: BiteStatus;
  note: string;
}

export interface WeightEntry {
  timestamp: number;
  weightKg: number;
}

export interface AppSettings {
  calendarMode: CalendarMode;
  fastingPlan: FastingPlan;
  firstMealHour: number;
  firstMealMinute: number;
  weightTrendEnabled: boolean;
  askWeightAfterCheckIn: boolean;
  targetWeightKg: number | null;
  weightUnit: WeightUnit;
  themeMode: ThemeMode;
}

export interface SnackRefusalCount {
  [dateKey: string]: number;
}

export interface BackupPayload {
  version: number;
  exportedAt: number;
  settings: AppSettings;
  records: BiteRecord[];
  weights: WeightEntry[];
  snackRefusals: SnackRefusalCount;
}

export const DEFAULT_SETTINGS: AppSettings = {
  calendarMode: 'month',
  fastingPlan: '16+8',
  firstMealHour: 9,
  firstMealMinute: 0,
  weightTrendEnabled: false,
  askWeightAfterCheckIn: false,
  targetWeightKg: null,
  weightUnit: 'kg',
  themeMode: 'system',
};

export const FASTING_PLANS: { value: FastingPlan; label: string; fastingHours: number; eatingHours: number }[] = [
  { value: '12+12', label: '12+12', fastingHours: 12, eatingHours: 12 },
  { value: '14+10', label: '14+10', fastingHours: 14, eatingHours: 10 },
  { value: '16+8', label: '16+8', fastingHours: 16, eatingHours: 8 },
  { value: '18+6', label: '18+6', fastingHours: 18, eatingHours: 6 },
  { value: '20+4', label: '20+4', fastingHours: 20, eatingHours: 4 },
];

export function getSnackEncouragement(count: number): { shortLabel: string; detail: string } {
  if (count >= 10) return { shortLabel: '硬控住了', detail: '今天防线拉满！' };
  if (count >= 5) return { shortLabel: '防线拉满', detail: '非常稳！' };
  if (count >= 3) return { shortLabel: '今天很稳', detail: '继续保持！' };
  if (count >= 1) return { shortLabel: '小胜利', detail: '每一次拒绝都是胜利' };
  return { shortLabel: '', detail: '' };
}
