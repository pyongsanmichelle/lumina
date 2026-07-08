import { useState } from 'react'

export type AlertType = 'success' | 'warning' | 'error'

export interface AlertProps {
  type: AlertType
  title: string
  message?: string
  dismissible?: boolean
  onDismiss?: () => void
}

const config: Record<AlertType, {
  bg: string; border: string; icon: string; iconColor: string
  titleColor: string; msgColor: string; dismissColor: string
}> = {
  success: {
    bg: '#f0fdf4',
    border: '#86efac',
    icon: '✓',
    iconColor: '#16a34a',
    titleColor: '#15803d',
    msgColor: '#166534',
    dismissColor: '#16a34a',
  },
  warning: {
    bg: '#fffbeb',
    border: '#fcd34d',
    icon: '⚠',
    iconColor: '#d97706',
    titleColor: '#92400e',
    msgColor: '#78350f',
    dismissColor: '#d97706',
  },
  error: {
    bg: '#fef2f2',
    border: '#fca5a5',
    icon: '✕',
    iconColor: '#dc2626',
    titleColor: '#991b1b',
    msgColor: '#7f1d1d',
    dismissColor: '#dc2626',
  },
}

export function Alert({ type, title, message, dismissible = true, onDismiss }: AlertProps) {
  const [visible, setVisible] = useState(true)
  const c = config[type]

  if (!visible) return null

  function dismiss() {
    setVisible(false)
    onDismiss?.()
  }

  return (
    <div
      role="alert"
      className="flex items-start gap-3 px-4 py-3.5 rounded-xl"
      style={{ background: c.bg, border: `1px solid ${c.border}` }}
    >
      <span
        className="text-base leading-none mt-0.5 flex-shrink-0 font-bold"
        style={{ color: c.iconColor }}
        aria-hidden
      >
        {c.icon}
      </span>
      <div className="flex-1 min-w-0">
        <div className="text-sm font-semibold" style={{ color: c.titleColor }}>{title}</div>
        {message && (
          <div className="text-xs mt-0.5 leading-relaxed" style={{ color: c.msgColor }}>{message}</div>
        )}
      </div>
      {dismissible && (
        <button
          onClick={dismiss}
          aria-label="閉じる"
          className="flex-shrink-0 text-xs leading-none mt-0.5 transition-opacity opacity-50 hover:opacity-100"
          style={{ color: c.dismissColor }}
        >
          ✕
        </button>
      )}
    </div>
  )
}

/** Controlled variant — parent manages visibility */
export function AlertBanner({ type, title, message, onDismiss }: Omit<AlertProps, 'dismissible'>) {
  const c = config[type]
  return (
    <div
      role="alert"
      className="flex items-start gap-3 px-5 py-4 rounded-xl"
      style={{ background: c.bg, border: `1px solid ${c.border}` }}
    >
      <span className="text-lg leading-none flex-shrink-0 font-bold" style={{ color: c.iconColor }} aria-hidden>
        {c.icon}
      </span>
      <div className="flex-1 min-w-0">
        <div className="text-sm font-semibold" style={{ color: c.titleColor }}>{title}</div>
        {message && (
          <div className="text-xs mt-1 leading-relaxed" style={{ color: c.msgColor }}>{message}</div>
        )}
      </div>
      {onDismiss && (
        <button
          onClick={onDismiss}
          aria-label="閉じる"
          className="flex-shrink-0 text-sm leading-none opacity-50 hover:opacity-100 transition-opacity"
          style={{ color: c.dismissColor }}
        >
          ✕
        </button>
      )}
    </div>
  )
}
