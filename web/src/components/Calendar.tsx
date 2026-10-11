import { useMemo } from 'react';
import { monthGrid, weekGrid, getMonthName, getWeekdayNames, isToday } from '../lib/calendar';
import { BiteRecord } from '../types';

interface CalendarProps {
  year: number;
  month: number;
  mode: 'month' | 'week';
  selectedDate: string;
  records: BiteRecord[];
  onSelectDate: (dateKey: string) => void;
}

export function Calendar({ year, month, mode, selectedDate, records, onSelectDate }: CalendarProps) {
  const days = useMemo(() => {
    if (mode === 'week') {
      const selected = new Date(selectedDate);
      return weekGrid(selected);
    }
    return monthGrid(year, month);
  }, [year, month, mode, selectedDate]);

  const weekdays = getWeekdayNames();
  const recordMap = useMemo(() => {
    const map: Record<string, BiteRecord> = {};
    records.forEach((r) => {
      map[r.dateKey] = r;
    });
    return map;
  }, [records]);

  return (
    <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
      {mode === 'month' && (
        <div className="text-center font-semibold text-lg mb-3">
          {year}年 {getMonthName(month)}
        </div>
      )}

      <div className="grid grid-cols-7 gap-1 mb-2">
        {weekdays.map((d) => (
          <div key={d} className="text-center text-xs text-[var(--color-text-secondary)] py-1">
            {d}
          </div>
        ))}
      </div>

      <div className="grid grid-cols-7 gap-1">
        {days.map((day) => {
          const record = recordMap[day.dateKey];
          const today = isToday(day.dateKey);
          const selected = day.dateKey === selectedDate;

          return (
            <button
              key={day.dateKey}
              onClick={() => onSelectDate(day.dateKey)}
              className={`
                relative aspect-square flex flex-col items-center justify-center rounded-lg text-sm
                transition-all duration-150
                ${!day.inCurrentPeriod ? 'opacity-30' : ''}
                ${selected ? 'ring-2 ring-[var(--color-primary)]' : ''}
                ${today ? 'font-bold' : ''}
                hover:bg-[var(--color-bg-secondary)]
              `}
            >
              <span>{day.date.getDate()}</span>
              {record && (
                <span
                  className={`
                    w-2 h-2 rounded-full mt-0.5
                    ${record.status === 'kept' ? 'bg-[var(--color-success)]' : 'bg-[var(--color-danger)]'}
                  `}
                />
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}
