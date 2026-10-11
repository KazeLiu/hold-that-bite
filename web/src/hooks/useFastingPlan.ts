import { useMemo } from 'react';
import { FastingPlan, FastingDayPhase } from '../types';
import {
  MealTime,
  dayPhase as calcDayPhase,
  lastBiteTime as calcLastBiteTime,
  formatTime,
  formatLastBiteTime,
} from '../lib/fasting';

export function useFastingPlan(plan: FastingPlan, firstMealHour: number, firstMealMinute: number) {
  const firstMeal: MealTime = useMemo(() => ({ hour: firstMealHour, minute: firstMealMinute }), [firstMealHour, firstMealMinute]);

  const now = useMemo(() => {
    const d = new Date();
    return { hour: d.getHours(), minute: d.getMinutes() };
  }, []);

  const currentPhase: FastingDayPhase = useMemo(() => calcDayPhase(firstMeal, now, plan), [firstMeal, now, plan]);

  const lastBite = useMemo(() => calcLastBiteTime(firstMeal, plan), [firstMeal, plan]);

  const firstMealText = useMemo(() => formatTime(firstMeal.hour, firstMeal.minute), [firstMeal]);

  const lastBiteText = useMemo(() => formatLastBiteTime(lastBite), [lastBite]);

  return {
    firstMeal,
    currentPhase,
    lastBite,
    firstMealText,
    lastBiteText,
  };
}
