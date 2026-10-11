import { useMemo } from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ReferenceLine,
  ResponsiveContainer,
} from 'recharts';
import { WeightEntry, WeightUnit } from '../types';

interface WeightChartProps {
  weights: WeightEntry[];
  unit: WeightUnit;
  targetWeightKg: number | null;
}

export function WeightChart({ weights, unit, targetWeightKg }: WeightChartProps) {
  const data = useMemo(() => {
    return weights
      .slice()
      .sort((a, b) => a.timestamp - b.timestamp)
      .map((w) => ({
        date: new Date(w.timestamp).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }),
        weight: unit === 'jin' ? w.weightKg * 2 : w.weightKg,
        timestamp: w.timestamp,
      }));
  }, [weights, unit]);

  const unitLabel = unit === 'jin' ? '斤' : 'kg';

  if (data.length === 0) {
    return (
      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm text-center text-[var(--color-text-secondary)]">
        暂无体重记录
      </div>
    );
  }

  const targetDisplay = targetWeightKg
    ? unit === 'jin'
      ? targetWeightKg * 2
      : targetWeightKg
    : null;

  return (
    <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
      <h3 className="font-medium mb-3">体重趋势</h3>
      <ResponsiveContainer width="100%" height={200}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
          <XAxis
            dataKey="date"
            tick={{ fontSize: 10, fill: 'var(--color-text-secondary)' }}
          />
          <YAxis
            unit={unitLabel}
            tick={{ fontSize: 10, fill: 'var(--color-text-secondary)' }}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: 'var(--color-bg)',
              border: '1px solid var(--color-border)',
              borderRadius: '8px',
            }}
          />
          <Line
            type="monotone"
            dataKey="weight"
            stroke="var(--color-primary)"
            strokeWidth={2}
            dot={{ r: 3 }}
          />
          {targetDisplay && (
            <ReferenceLine
              y={targetDisplay}
              stroke="var(--color-warning)"
              strokeDasharray="5 5"
              label={{ value: '目标', fill: 'var(--color-warning)', fontSize: 10 }}
            />
          )}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
