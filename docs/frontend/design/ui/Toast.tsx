import { createContext, useContext, useState, useCallback, useRef, type ReactNode } from 'react'

export type ToastType = 'success' | 'warning' | 'error'

export interface Toast {
  id: string
  type: ToastType
  title: string
  message?: string
}

interface ToastContextValue {
  addToast: (toast: Omit<Toast, 'id'>) => void
  removeToast: (id: string) => void
}

const ToastContext = createContext<ToastContextValue | null>(null)

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be inside ToastProvider')
  return ctx
}

const config: Record<ToastType, { bg: string; border: string; icon: string; iconBg: string; title: string }> = {
  success: {
    bg: '#f0fdf4',
    border: '#bbf7d0',
    icon: '✓',
    iconBg: '#22c55e',
    title: '#15803d',
  },
  warning: {
    bg: '#fffbeb',
    border: '#fde68a',
    icon: '!',
    iconBg: '#f59e0b',
    title: '#92400e',
  },
  error: {
    bg: '#fef2f2',
    border: '#fecaca',
    icon: '×',
    iconBg: '#ef4444',
    title: '#991b1b',
  },
}

function ToastItem({ toast, onRemove }: { toast: Toast; onRemove: () => void }) {
  const c = config[toast.type]
  return (
    <div
      role="alert"
      aria-live="polite"
      className="flex items-start gap-3 px-4 py-3.5 rounded-xl shadow-lg max-w-sm w-full"
      style={{
        background: c.bg,
        border: `1px solid ${c.border}`,
        animation: 'toast-in 0.25s ease-out',
      }}
    >
      <div
        className="w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 text-white text-xs font-bold mt-0.5"
        style={{ background: c.iconBg }}
      >
        {c.icon}
      </div>
      <div className="flex-1 min-w-0">
        <div className="text-sm font-semibold" style={{ color: c.title }}>{toast.title}</div>
        {toast.message && (
          <div className="text-xs mt-0.5 leading-relaxed" style={{ color: c.title, opacity: 0.8 }}>{toast.message}</div>
        )}
      </div>
      <button
        onClick={onRemove}
        className="flex-shrink-0 text-xs leading-none mt-0.5 transition-opacity opacity-50 hover:opacity-100"
        style={{ color: c.title }}
        aria-label="閉じる"
      >
        ✕
      </button>
    </div>
  )
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([])
  const timers = useRef<Record<string, ReturnType<typeof setTimeout>>>({})

  const removeToast = useCallback((id: string) => {
    clearTimeout(timers.current[id])
    delete timers.current[id]
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const addToast = useCallback((toast: Omit<Toast, 'id'>) => {
    const id = crypto.randomUUID()
    setToasts((prev) => [...prev.slice(-4), { ...toast, id }])
    timers.current[id] = setTimeout(() => removeToast(id), 4500)
  }, [removeToast])

  return (
    <ToastContext.Provider value={{ addToast, removeToast }}>
      {children}
      {/* Toast portal — fixed top-right */}
      <div
        aria-live="polite"
        className="fixed flex flex-col gap-2.5"
        style={{ top: 72, right: 24, zIndex: 200, pointerEvents: 'none' }}
      >
        {toasts.map((t) => (
          <div key={t.id} style={{ pointerEvents: 'auto' }}>
            <ToastItem toast={t} onRemove={() => removeToast(t.id)} />
          </div>
        ))}
      </div>
      <style>{`
        @keyframes toast-in {
          from { opacity: 0; transform: translateX(16px) scale(0.97); }
          to   { opacity: 1; transform: translateX(0)    scale(1); }
        }
      `}</style>
    </ToastContext.Provider>
  )
}
