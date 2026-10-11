import { getSnackEncouragement } from '../types';

interface SnackRefusalProps {
  count: number;
  onAdd: () => void;
  onUndo: () => void;
}

export function SnackRefusal({ count, onAdd, onUndo }: SnackRefusalProps) {
  const encouragement = getSnackEncouragement(count);

  return (
    <div className="bg-[var(--color-bg)] rounded-xl p-4 shadow-sm">
      <div className="flex items-center justify-between mb-2">
        <span className="text-sm text-[var(--color-text-secondary)]">拒绝零食</span>
        {count > 0 && (
          <span className="text-xs text-[var(--color-primary)]">今日 {count} 次</span>
        )}
      </div>

      <div className="flex items-center gap-3">
        <button
          onClick={onAdd}
          className="flex-1 py-3 bg-[var(--color-primary)] text-white rounded-xl font-medium hover:opacity-90 transition-opacity active:scale-95"
        >
          拒绝零食 +1
        </button>
        {count > 0 && (
          <button
            onClick={onUndo}
            className="px-4 py-3 bg-[var(--color-bg-secondary)] rounded-xl text-sm text-[var(--color-text-secondary)] hover:bg-[var(--color-border)]"
          >
            撤销
          </button>
        )}
      </div>

      {encouragement.shortLabel && (
        <div className="mt-3 text-center">
          <span className="text-sm font-medium text-[var(--color-primary)]">
            {encouragement.shortLabel}
          </span>
          <span className="text-xs text-[var(--color-text-secondary)] ml-2">
            {encouragement.detail}
          </span>
        </div>
      )}
    </div>
  );
}
