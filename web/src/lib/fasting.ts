import { FastingPlan, FastingDayPhase, FASTING_PLANS } from '../types';

export interface MealTime {
  hour: number;
  minute: number;
}

export interface LastBiteTime {
  hour: number;
  minute: number;
  isNextDay: boolean;
}

export function getPlanConfig(plan: FastingPlan) {
  return FASTING_PLANS.find((p) => p.value === plan)!;
}

export function getTotalMinutes(time: MealTime): number {
  return time.hour * 60 + time.minute;
}

export function lastBiteTime(firstMeal: MealTime, plan: FastingPlan): LastBiteTime {
  const config = getPlanConfig(plan);
  const totalMinutes = getTotalMinutes(firstMeal) + config.eatingHours * 60;
  const isNextDay = totalMinutes >= 1440;
  const remainingMinutes = totalMinutes % 1440;
  return {
    hour: Math.floor(remainingMinutes / 60),
    minute: remainingMinutes % 60,
    isNextDay,
  };
}

export function latestSameDayFirstMeal(plan: FastingPlan): MealTime {
  const config = getPlanConfig(plan);
  const latestMinutes = 1440 - config.eatingHours * 60;
  return {
    hour: Math.floor(latestMinutes / 60),
    minute: latestMinutes % 60,
  };
}

export function isSameDayPlan(firstMeal: MealTime, plan: FastingPlan): boolean {
  const lastBite = lastBiteTime(firstMeal, plan);
  return !lastBite.isNextDay;
}

export function dayPhase(
  firstMeal: MealTime,
  currentTime: MealTime,
  plan: FastingPlan
): FastingDayPhase {
  const currentMinutes = getTotalMinutes(currentTime);
  const firstMealMinutes = getTotalMinutes(firstMeal);
  const lastBite = lastBiteTime(firstMeal, plan);
  const lastBiteMinutes = lastBite.hour * 60 + lastBite.minute;

  if (lastBite.isNextDay) {
    if (currentMinutes >= firstMealMinutes) {
      return 'eating_window';
    }
    if (currentMinutes < lastBiteMinutes) {
      return 'eating_window';
    }
    return 'after_last_bite';
  }

  if (currentMinutes < firstMealMinutes) {
    return 'before_first_meal';
  }
  if (currentMinutes < lastBiteMinutes) {
    return 'eating_window';
  }
  return 'after_last_bite';
}

export function isEatingWindow(firstMeal: MealTime, currentTime: MealTime, plan: FastingPlan): boolean {
  return dayPhase(firstMeal, currentTime, plan) === 'eating_window';
}

export function formatTime(hour: number, minute: number): string {
  return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
}

export function formatLastBiteTime(lastBite: LastBiteTime): string {
  const time = formatTime(lastBite.hour, lastBite.minute);
  return lastBite.isNextDay ? `次日 ${time}` : time;
}
