import { useRef } from 'react';
import { AppSettings, FASTING_PLANS, CalendarMode, ThemeMode, WeightUnit, BackupPayload } from '../types';
import { createBackupPayload, downloadBackup, readBackupFile } from '../lib/backup';

interface SettingsFormProps {
  settings: AppSettings;
  onUpdate: (settings: Partial<AppSettings>) => void;
  records: BackupPayload['records'];
  weights: BackupPayload['weights'];
  snackRefusals: BackupPayload['snackRefusals'];
  onImport: (payload: BackupPayload) => void;
}

export function SettingsForm({
  settings,
  onUpdate,
  records,
  weights,
  snackRefusals,
  onImport,
}: SettingsFormProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleExport = () => {
    const payload = createBackupPayload(settings, records, weights, snackRefusals);
    downloadBackup(payload);
  };

  const handleImport = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const payload = await readBackupFile(file);
    if (payload) {
      if (confirm('导入将覆盖当前数据，确定继续吗？')) {
        onImport(payload);
      }
    } else {
      alert('备份文件格式无效');
    }

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  return (
    <div className="space-y-4">
      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">日历显示</h3>
        <div className="flex gap-2">
          {(['month', 'week'] as CalendarMode[]).map((mode) => (
            <button
              key={mode}
              onClick={() => onUpdate({ calendarMode: mode })}
              className={`
                flex-1 py-2 rounded-lg text-sm transition-colors
                ${settings.calendarMode === mode
                  ? 'bg-[var(--color-primary)] text-white'
                  : 'bg-[var(--color-bg-secondary)] text-[var(--color-text-secondary)]'
                }
              `}
            >
              {mode === 'month' ? '月历' : '周历'}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">显示模式</h3>
        <div className="flex gap-2">
          {(['system', 'light', 'dark'] as ThemeMode[]).map((mode) => (
            <button
              key={mode}
              onClick={() => onUpdate({ themeMode: mode })}
              className={`
                flex-1 py-2 rounded-lg text-sm transition-colors
                ${settings.themeMode === mode
                  ? 'bg-[var(--color-primary)] text-white'
                  : 'bg-[var(--color-bg-secondary)] text-[var(--color-text-secondary)]'
                }
              `}
            >
              {mode === 'system' ? '跟随系统' : mode === 'light' ? '浅色' : '深色'}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">减肥安排</h3>
        <div className="grid grid-cols-5 gap-2">
          {FASTING_PLANS.map((plan) => (
            <button
              key={plan.value}
              onClick={() => onUpdate({ fastingPlan: plan.value })}
              className={`
                py-2 rounded-lg text-sm transition-colors
                ${settings.fastingPlan === plan.value
                  ? 'bg-[var(--color-primary)] text-white'
                  : 'bg-[var(--color-bg-secondary)] text-[var(--color-text-secondary)]'
                }
              `}
            >
              {plan.label}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">第一餐时间</h3>
        <input
          type="time"
          value={`${settings.firstMealHour.toString().padStart(2, '0')}:${settings.firstMealMinute.toString().padStart(2, '0')}`}
          onChange={(e) => {
            const [hour, minute] = e.target.value.split(':').map(Number);
            onUpdate({ firstMealHour: hour, firstMealMinute: minute });
          }}
          className="w-full p-2 border border-[var(--color-border)] rounded-lg bg-[var(--color-bg-secondary)] text-[var(--color-text)]"
        />
      </div>

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">体重趋势</h3>
        <label className="flex items-center justify-between mb-3">
          <span className="text-sm">显示趋势页</span>
          <input
            type="checkbox"
            checked={settings.weightTrendEnabled}
            onChange={(e) => onUpdate({ weightTrendEnabled: e.target.checked })}
            className="w-5 h-5 accent-[var(--color-primary)]"
          />
        </label>
        <label className="flex items-center justify-between mb-3">
          <span className="text-sm">打卡后询问体重</span>
          <input
            type="checkbox"
            checked={settings.askWeightAfterCheckIn}
            onChange={(e) => onUpdate({ askWeightAfterCheckIn: e.target.checked })}
            className="w-5 h-5 accent-[var(--color-primary)]"
          />
        </label>
        <div className="mb-3">
          <label className="text-sm block mb-1">目标体重</label>
          <input
            type="number"
            value={settings.targetWeightKg ?? ''}
            onChange={(e) => {
              const v = e.target.value ? parseFloat(e.target.value) : null;
              onUpdate({ targetWeightKg: v });
            }}
            placeholder="可选"
            className="w-full p-2 border border-[var(--color-border)] rounded-lg bg-[var(--color-bg-secondary)] text-[var(--color-text)]"
          />
        </div>
        <div className="flex gap-2">
          {(['kg', 'jin'] as WeightUnit[]).map((unit) => (
            <button
              key={unit}
              onClick={() => onUpdate({ weightUnit: unit })}
              className={`
                flex-1 py-2 rounded-lg text-sm transition-colors
                ${settings.weightUnit === unit
                  ? 'bg-[var(--color-primary)] text-white'
                  : 'bg-[var(--color-bg-secondary)] text-[var(--color-text-secondary)]'
                }
              `}
            >
              {unit === 'kg' ? 'kg' : '斤'}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">数据备份</h3>
        <div className="flex gap-2">
          <button
            onClick={handleExport}
            className="flex-1 py-2 bg-[var(--color-primary)] text-white rounded-lg text-sm"
          >
            导出数据
          </button>
          <button
            onClick={() => fileInputRef.current?.click()}
            className="flex-1 py-2 bg-[var(--color-bg-secondary)] text-[var(--color-text-secondary)] rounded-lg text-sm"
          >
            导入数据
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept=".json"
            onChange={handleImport}
            className="hidden"
          />
        </div>
      </div>
    </div>
  );
}
