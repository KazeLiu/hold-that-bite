import { useState, useMemo } from 'react';
import { BiteRecord, AppSettings, BiteStatus, SnackRefusalCount } from '../types';
import { Calendar } from '../components/Calendar';
import { CheckInDialog } from '../components/CheckInDialog';
import { SnackRefusal } from '../components/SnackRefusal';
import { useFastingPlan } from '../hooks/useFastingPlan';
import { todayKey, isFuture } from '../lib/calendar';
import { FASTING_PLANS } from '../types';

interface HomePageProps {
  settings: AppSettings;
  records: BiteRecord[];
  snackRefusals: SnackRefusalCount;
  onUpdateRecords: (records: BiteRecord[]) => void;
  onUpdateSnackRefusals: (refusals: SnackRefusalCount) => void;
  onRecordWeight?: (weight: number) => void;
}

export function HomePage({
  settings,
  records,
  snackRefusals,
  onUpdateRecords,
  onUpdateSnackRefusals,
  onRecordWeight,
}: HomePageProps) {
  const [selectedDate, setSelectedDate] = useState(todayKey());
  const [showCheckIn, setShowCheckIn] = useState(false);

  const now = useMemo(() => {
    const d = new Date();
    return { year: d.getFullYear(), month: d.getMonth() };
  }, []);

  const { currentPhase, firstMealText, lastBiteText } = useFastingPlan(
    settings.fastingPlan,
    settings.firstMealHour,
    settings.firstMealMinute
  );

  const todayRecord = useMemo(() => {
    return records.find((r) => r.dateKey === selectedDate) || null;
  }, [records, selectedDate]);

  const snackCount = snackRefusals[selectedDate] || 0;

  const planConfig = FASTING_PLANS.find((p) => p.value === settings.fastingPlan)!;

  const handleCheckIn = (status: BiteStatus, note: string) => {
    const newRecord: BiteRecord = {
      dateKey: selectedDate,
      status,
      note,
    };
    const existing = records.findIndex((r) => r.dateKey === selectedDate);
    let newRecords: BiteRecord[];
    if (existing >= 0) {
      newRecords = [...records];
      newRecords[existing] = newRecord;
    } else {
      newRecords = [...records, newRecord];
    }
    onUpdateRecords(newRecords);
    setShowCheckIn(false);
  };

  const handleAddSnack = () => {
    const newRefusals = { ...snackRefusals };
    newRefusals[selectedDate] = (newRefusals[selectedDate] || 0) + 1;
    onUpdateSnackRefusals(newRefusals);
  };

  const handleUndoSnack = () => {
    const newRefusals = { ...snackRefusals };
    if (newRefusals[selectedDate] && newRefusals[selectedDate] > 0) {
      newRefusals[selectedDate]--;
      if (newRefusals[selectedDate] === 0) {
        delete newRefusals[selectedDate];
      }
    }
    onUpdateSnackRefusals(newRefusals);
  };

  const phaseText = (() => {
    switch (currentPhase) {
      case 'before_first_meal':
        return `第一餐前 (${firstMealText} 开始)`;
      case 'eating_window':
        return `进食窗口中 (最后一口 ${lastBiteText})`;
      case 'after_last_bite':
        return `禁食窗口中 (下一餐 ${firstMealText})`;
    }
  })();

  return (
    <div className="space-y-4 pb-4">
      <Calendar
        year={now.year}
        month={now.month}
        mode={settings.calendarMode}
        selectedDate={selectedDate}
        records={records}
        onSelectDate={setSelectedDate}
      />

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <div className="text-center mb-2">
          <div className="text-sm text-[var(--color-text-secondary)]">{selectedDate}</div>
          <div className="font-medium">{phaseText}</div>
          <div className="text-xs text-[var(--color-text-secondary)] mt-1">
            {planConfig.label} · 第一餐 {firstMealText} · 最后一口 {lastBiteText}
          </div>
        </div>

        {todayRecord ? (
          <div className="text-center py-3">
            <span
              className={`
                inline-block px-4 py-2 rounded-full text-white font-medium
                ${todayRecord.status === 'kept' ? 'bg-[var(--color-success)]' : 'bg-[var(--color-danger)]'}
              `}
            >
              {todayRecord.status === 'kept' ? '守住了' : '没守住'}
            </span>
            {todayRecord.note && (
              <div className="text-sm text-[var(--color-text-secondary)] mt-2">{todayRecord.note}</div>
            )}
          </div>
        ) : (
          !isFuture(selectedDate) && (
            <button
              onClick={() => setShowCheckIn(true)}
              className="w-full py-3 bg-[var(--color-primary)] text-white rounded-xl font-medium hover:opacity-90 transition-opacity"
            >
              打卡
            </button>
          )
        )}
      </div>

      <SnackRefusal count={snackCount} onAdd={handleAddSnack} onUndo={handleUndoSnack} />

      <CheckInDialog
        isOpen={showCheckIn}
        dateKey={selectedDate}
        onClose={() => setShowCheckIn(false)}
        onConfirm={handleCheckIn}
        askWeight={settings.askWeightAfterCheckIn}
        onRecordWeight={onRecordWeight}
      />
    </div>
  );
}
