import { AppSettings, BiteRecord, WeightEntry, SnackRefusalCount, BackupPayload } from '../types';
import { SettingsForm } from '../components/SettingsForm';

interface SettingsPageProps {
  settings: AppSettings;
  records: BiteRecord[];
  weights: WeightEntry[];
  snackRefusals: SnackRefusalCount;
  onUpdateSettings: (settings: Partial<AppSettings>) => void;
  onImportData: (payload: BackupPayload) => void;
}

export function SettingsPage({
  settings,
  records,
  weights,
  snackRefusals,
  onUpdateSettings,
  onImportData,
}: SettingsPageProps) {
  return (
    <div className="pb-4">
      <SettingsForm
        settings={settings}
        onUpdate={onUpdateSettings}
        records={records}
        weights={weights}
        snackRefusals={snackRefusals}
        onImport={onImportData}
      />
    </div>
  );
}
