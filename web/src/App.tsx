import { useState, useCallback } from 'react';
import { AppSettings, BiteRecord, WeightEntry, SnackRefusalCount, BackupPayload } from './types';
import { useTheme } from './hooks/useTheme';
import { loadSettings, saveSettings, loadRecords, saveRecords, loadWeights, saveWeights, loadSnackRefusals, saveSnackRefusals } from './lib/storage';
import { HomePage } from './pages/HomePage';
import { TrendPage } from './pages/TrendPage';
import { SettingsPage } from './pages/SettingsPage';

type Tab = 'home' | 'trend' | 'settings';

export default function App() {
  const [settings, setSettings] = useState<AppSettings>(() => loadSettings());
  const [records, setRecords] = useState<BiteRecord[]>(() => loadRecords());
  const [weights, setWeights] = useState<WeightEntry[]>(() => loadWeights());
  const [snackRefusals, setSnackRefusals] = useState<SnackRefusalCount>(() => loadSnackRefusals());
  const [activeTab, setActiveTab] = useState<Tab>('home');

  useTheme(settings.themeMode);

  const handleUpdateSettings = useCallback((partial: Partial<AppSettings>) => {
    setSettings((prev) => {
      const next = { ...prev, ...partial };
      saveSettings(next);
      return next;
    });
  }, []);

  const handleUpdateRecords = useCallback((newRecords: BiteRecord[]) => {
    setRecords(newRecords);
    saveRecords(newRecords);
  }, []);

  const handleUpdateWeights = useCallback((newWeights: WeightEntry[]) => {
    setWeights(newWeights);
    saveWeights(newWeights);
  }, []);

  const handleUpdateSnackRefusals = useCallback((newRefusals: SnackRefusalCount) => {
    setSnackRefusals(newRefusals);
    saveSnackRefusals(newRefusals);
  }, []);

  const handleRecordWeight = useCallback((weightKg: number) => {
    const newEntry: WeightEntry = {
      timestamp: Date.now(),
      weightKg,
    };
    const newWeights = [...weights, newEntry];
    setWeights(newWeights);
    saveWeights(newWeights);
  }, [weights]);

  const handleImportData = useCallback((payload: BackupPayload) => {
    setSettings(payload.settings);
    saveSettings(payload.settings);
    setRecords(payload.records);
    saveRecords(payload.records);
    setWeights(payload.weights);
    saveWeights(payload.weights);
    setSnackRefusals(payload.snackRefusals);
    saveSnackRefusals(payload.snackRefusals);
  }, []);

  return (
    <div className="min-h-screen bg-[var(--color-bg)]">
      <div className="max-w-lg mx-auto px-4 pt-4">
        <h1 className="text-xl font-bold text-center mb-4">守住这口</h1>

        {activeTab === 'home' && (
          <HomePage
            settings={settings}
            records={records}
            snackRefusals={snackRefusals}
            onUpdateRecords={handleUpdateRecords}
            onUpdateSnackRefusals={handleUpdateSnackRefusals}
            onRecordWeight={settings.askWeightAfterCheckIn ? handleRecordWeight : undefined}
          />
        )}

        {activeTab === 'trend' && settings.weightTrendEnabled && (
          <TrendPage
            weights={weights}
            unit={settings.weightUnit}
            targetWeightKg={settings.targetWeightKg}
            onUpdateWeights={handleUpdateWeights}
          />
        )}

        {activeTab === 'settings' && (
          <SettingsPage
            settings={settings}
            records={records}
            weights={weights}
            snackRefusals={snackRefusals}
            onUpdateSettings={handleUpdateSettings}
            onImportData={handleImportData}
          />
        )}
      </div>

      <div className="fixed bottom-0 left-0 right-0 bg-[var(--color-bg)] border-t border-[var(--color-border)]">
        <div className="max-w-lg mx-auto flex">
          <button
            onClick={() => setActiveTab('home')}
            className={`flex-1 py-3 text-center text-sm transition-colors ${
              activeTab === 'home' ? 'text-[var(--color-primary)] font-medium' : 'text-[var(--color-text-secondary)]'
            }`}
          >
            主页
          </button>
          {settings.weightTrendEnabled && (
            <button
              onClick={() => setActiveTab('trend')}
              className={`flex-1 py-3 text-center text-sm transition-colors ${
                activeTab === 'trend' ? 'text-[var(--color-primary)] font-medium' : 'text-[var(--color-text-secondary)]'
              }`}
            >
              趋势
            </button>
          )}
          <button
            onClick={() => setActiveTab('settings')}
            className={`flex-1 py-3 text-center text-sm transition-colors ${
              activeTab === 'settings' ? 'text-[var(--color-primary)] font-medium' : 'text-[var(--color-text-secondary)]'
            }`}
          >
            设置
          </button>
        </div>
      </div>
    </div>
  );
}
