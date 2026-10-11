import { useState } from 'react';
import { BiteStatus } from '../types';

interface CheckInDialogProps {
  isOpen: boolean;
  dateKey: string;
  onClose: () => void;
  onConfirm: (status: BiteStatus, note: string) => void;
  askWeight: boolean;
  onRecordWeight?: (weight: number) => void;
}

export function CheckInDialog({ isOpen, dateKey, onClose, onConfirm, askWeight, onRecordWeight }: CheckInDialogProps) {
  const [note, setNote] = useState('');
  const [weight, setWeight] = useState('');
  const [showWeight, setShowWeight] = useState(false);

  if (!isOpen) return null;

  const handleConfirm = (status: BiteStatus) => {
    if (askWeight && !showWeight) {
      setShowWeight(true);
      return;
    }

    if (showWeight && weight && onRecordWeight) {
      const w = parseFloat(weight);
      if (!isNaN(w) && w > 0 && w < 500) {
        onRecordWeight(w);
      }
    }

    onConfirm(status, note);
    setNote('');
    setWeight('');
    setShowWeight(false);
  };

  const handleCancel = () => {
    setNote('');
    setWeight('');
    setShowWeight(false);
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-[var(--color-bg)] rounded-2xl p-6 w-full max-w-sm shadow-xl">
        <h3 className="text-lg font-semibold text-center mb-4">{dateKey} 打卡</h3>

        {!showWeight ? (
          <>
            <div className="space-y-3 mb-4">
              <button
                onClick={() => handleConfirm('kept')}
                className="w-full py-3 bg-[var(--color-success)] text-white rounded-xl font-medium hover:opacity-90 transition-opacity"
              >
                守住了
              </button>
              <button
                onClick={() => handleConfirm('missed')}
                className="w-full py-3 bg-[var(--color-danger)] text-white rounded-xl font-medium hover:opacity-90 transition-opacity"
              >
                没守住
              </button>
            </div>

            <div className="mb-4">
              <textarea
                value={note}
                onChange={(e) => setNote(e.target.value)}
                placeholder="添加备注（可选）"
                className="w-full p-3 border border-[var(--color-border)] rounded-xl resize-none h-20 bg-[var(--color-bg-secondary)] text-[var(--color-text)]"
              />
            </div>
          </>
        ) : (
          <div className="mb-4">
            <label className="block text-sm text-[var(--color-text-secondary)] mb-2">记录体重</label>
            <input
              type="number"
              value={weight}
              onChange={(e) => setWeight(e.target.value)}
              placeholder="请输入体重"
              className="w-full p-3 border border-[var(--color-border)] rounded-xl bg-[var(--color-bg-secondary)] text-[var(--color-text)]"
              autoFocus
            />
            <div className="flex gap-2 mt-3">
              <button
                onClick={() => {
                  setWeight('');
                  handleConfirm('kept');
                }}
                className="flex-1 py-2 bg-[var(--color-bg-secondary)] rounded-xl text-sm"
              >
                跳过
              </button>
              <button
                onClick={() => handleConfirm('kept')}
                className="flex-1 py-2 bg-[var(--color-primary)] text-white rounded-xl text-sm"
              >
                确认
              </button>
            </div>
          </div>
        )}

        <button
          onClick={handleCancel}
          className="w-full py-2 text-[var(--color-text-secondary)] text-sm"
        >
          取消
        </button>
      </div>
    </div>
  );
}
