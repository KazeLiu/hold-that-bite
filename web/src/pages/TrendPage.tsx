import { useState } from 'react';
import { WeightEntry, WeightUnit } from '../types';
import { WeightChart } from '../components/WeightChart';

interface TrendPageProps {
  weights: WeightEntry[];
  unit: WeightUnit;
  targetWeightKg: number | null;
  onUpdateWeights: (weights: WeightEntry[]) => void;
}

export function TrendPage({ weights, unit, targetWeightKg, onUpdateWeights }: TrendPageProps) {
  const [inputWeight, setInputWeight] = useState('');

  const handleAddWeight = () => {
    const w = parseFloat(inputWeight);
    if (isNaN(w) || w <= 0 || w >= 500) return;

    const newEntry: WeightEntry = {
      timestamp: Date.now(),
      weightKg: unit === 'jin' ? w / 2 : w,
    };

    onUpdateWeights([...weights, newEntry]);
    setInputWeight('');
  };

  const handleDeleteWeight = (timestamp: number) => {
    onUpdateWeights(weights.filter((w) => w.timestamp !== timestamp));
  };

  const displayWeights = weights
    .slice()
    .sort((a, b) => b.timestamp - a.timestamp)
    .map((w) => ({
      ...w,
      displayWeight: unit === 'jin' ? w.weightKg * 2 : w.weightKg,
    }));

  const unitLabel = unit === 'jin' ? '斤' : 'kg';

  return (
    <div className="space-y-4 pb-4">
      <WeightChart weights={weights} unit={unit} targetWeightKg={targetWeightKg} />

      <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
        <h3 className="font-medium mb-3">记录体重</h3>
        <div className="flex gap-2">
          <input
            type="number"
            value={inputWeight}
            onChange={(e) => setInputWeight(e.target.value)}
            placeholder={`请输入体重 (${unitLabel})`}
            className="flex-1 p-3 border border-[var(--color-border)] rounded-xl bg-[var(--color-bg-secondary)] text-[var(--color-text)]"
          />
          <button
            onClick={handleAddWeight}
            className="px-6 py-3 bg-[var(--color-primary)] text-white rounded-xl font-medium"
          >
            记录
          </button>
        </div>
      </div>

      {displayWeights.length > 0 && (
        <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
          <h3 className="font-medium mb-3">历史记录</h3>
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {displayWeights.map((w) => (
              <div
                key={w.timestamp}
                className="flex items-center justify-between py-2 border-b border-[var(--color-border)] last:border-0"
              >
                <div>
                  <span className="font-medium">{w.displayWeight.toFixed(1)} {unitLabel}</span>
                  <span className="text-xs text-[var(--color-text-secondary)] ml-2">
                    {new Date(w.timestamp).toLocaleDateString('zh-CN')}
                  </span>
                </div>
                <button
                  onClick={() => handleDeleteWeight(w.timestamp)}
                  className="text-xs text-[var(--color-danger)]"
                >
                  删除
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
