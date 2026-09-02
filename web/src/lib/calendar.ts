export interface CalendarDay {
  date: Date;
  dateKey: string;
  inCurrentPeriod: boolean;
}

export function dateKey(date: Date): string {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function parseDateKey(key: string): Date {
  const [year, month, day] = key.split('-').map(Number);
  return new Date(year, month - 1, day);
}

export function todayKey(): string {
  return dateKey(new Date());
}

export function monthGrid(year: number, month: number): CalendarDay[] {
  const firstDay = new Date(year, month, 1);

  let startDay = firstDay.getDay();
  if (startDay === 0) startDay = 7;
  const startDate = new Date(firstDay);
  startDate.setDate(startDate.getDate() - (startDay - 1));

  const days: CalendarDay[] = [];
  const current = new Date(startDate);

  for (let i = 0; i < 42; i++) {
    days.push({
      date: new Date(current),
      dateKey: dateKey(current),
      inCurrentPeriod: current.getMonth() === month,
    });
    current.setDate(current.getDate() + 1);
  }

  return days;
}

export function weekGrid(selectedDate: Date): CalendarDay[] {
  const date = new Date(selectedDate);
  const day = date.getDay();
  const startOffset = day === 0 ? 6 : day - 1;
  const startDate = new Date(date);
  startDate.setDate(startDate.getDate() - startOffset);

  const days: CalendarDay[] = [];
  const current = new Date(startDate);

  for (let i = 0; i < 7; i++) {
    days.push({
      date: new Date(current),
      dateKey: dateKey(current),
      inCurrentPeriod: true,
    });
    current.setDate(current.getDate() + 1);
  }

  return days;
}

export function getMonthName(month: number): string {
  const names = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'];
  return names[month];
}

export function getWeekdayNames(): string[] {
  return ['一', '二', '三', '四', '五', '六', '日'];
}

export function isToday(dateKeyStr: string): boolean {
  return dateKeyStr === todayKey();
}

export function isFuture(dateKeyStr: string): boolean {
  return dateKeyStr > todayKey();
}
