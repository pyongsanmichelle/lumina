import type { InputHTMLAttributes, SelectHTMLAttributes, TextareaHTMLAttributes, ReactNode } from 'react'

export interface FieldProps {
  label: string
  required?: boolean
  error?: string
  hint?: string
}

/** Badge shown beside label */
function RequiredBadge({ required }: { required?: boolean }) {
  return required ? (
    <span className="text-xs font-semibold px-1.5 py-0.5 rounded" style={{ background: '#fee2e2', color: '#dc2626' }}>必須</span>
  ) : (
    <span className="text-xs px-1.5 py-0.5 rounded" style={{ background: '#f1f5f9', color: '#94a3b8' }}>任意</span>
  )
}

function FieldLabel({ label, required, htmlFor }: { label: string; required?: boolean; htmlFor?: string }) {
  return (
    <label htmlFor={htmlFor} className="flex items-center gap-1.5 text-xs font-medium mb-1.5" style={{ color: '#475569' }}>
      {label}
      <RequiredBadge required={required} />
    </label>
  )
}

function FieldError({ error }: { error?: string }) {
  if (!error) return null
  return (
    <p role="alert" className="flex items-center gap-1 mt-1.5 text-xs font-medium" style={{ color: '#dc2626' }}>
      <span aria-hidden>!</span> {error}
    </p>
  )
}

function FieldHint({ hint }: { hint?: string }) {
  if (!hint) return null
  return <p className="mt-1 text-xs" style={{ color: '#94a3b8' }}>{hint}</p>
}

function inputStyle(error?: string): React.CSSProperties {
  return {
    borderColor: error ? '#ef4444' : '#e2e8f0',
    color: '#0f172a',
    background: 'white',
    boxShadow: error ? '0 0 0 3px rgba(239,68,68,0.12)' : undefined,
    outline: 'none',
    transition: 'border-color 0.15s, box-shadow 0.15s',
  }
}

function focusCls(e: React.FocusEvent<HTMLElement>, hasError?: boolean) {
  if (hasError) return
  ;(e.target as HTMLElement).style.borderColor = '#2563eb'
  ;(e.target as HTMLElement).style.boxShadow = '0 0 0 3px rgba(37,99,235,0.1)'
}

function blurCls(e: React.FocusEvent<HTMLElement>, hasError?: boolean) {
  ;(e.target as HTMLElement).style.borderColor = hasError ? '#ef4444' : '#e2e8f0'
  ;(e.target as HTMLElement).style.boxShadow = hasError ? '0 0 0 3px rgba(239,68,68,0.12)' : 'none'
}

const baseInputCls = 'w-full text-sm px-3 py-2.5 rounded-lg border'

/* ─────────── TextField ─────────── */
export function TextField({
  label, required, error, hint, id, ...props
}: FieldProps & InputHTMLAttributes<HTMLInputElement>) {
  const fieldId = id ?? `field-${label}`
  return (
    <div>
      <FieldLabel label={label} required={required} htmlFor={fieldId} />
      <input
        id={fieldId}
        aria-invalid={!!error}
        aria-describedby={error ? `${fieldId}-err` : hint ? `${fieldId}-hint` : undefined}
        className={baseInputCls}
        style={inputStyle(error)}
        onFocus={(e) => focusCls(e, !!error)}
        onBlur={(e) => blurCls(e, !!error)}
        {...props}
      />
      <FieldError error={error} />
      <FieldHint hint={hint} />
    </div>
  )
}

/* ─────────── SelectField ─────────── */
export function SelectField({
  label, required, error, hint, children, id, ...props
}: FieldProps & SelectHTMLAttributes<HTMLSelectElement> & { children: ReactNode }) {
  const fieldId = id ?? `field-${label}`
  return (
    <div>
      <FieldLabel label={label} required={required} htmlFor={fieldId} />
      <select
        id={fieldId}
        aria-invalid={!!error}
        className={baseInputCls + ' appearance-none'}
        style={inputStyle(error)}
        onFocus={(e) => focusCls(e, !!error)}
        onBlur={(e) => blurCls(e, !!error)}
        {...props}
      >
        {children}
      </select>
      <FieldError error={error} />
      <FieldHint hint={hint} />
    </div>
  )
}

/* ─────────── TextareaField ─────────── */
export function TextareaField({
  label, required, error, hint, id, ...props
}: FieldProps & TextareaHTMLAttributes<HTMLTextAreaElement>) {
  const fieldId = id ?? `field-${label}`
  return (
    <div>
      <FieldLabel label={label} required={required} htmlFor={fieldId} />
      <textarea
        id={fieldId}
        aria-invalid={!!error}
        className={baseInputCls + ' resize-y'}
        style={{ ...inputStyle(error), lineHeight: 1.6 }}
        onFocus={(e) => focusCls(e, !!error)}
        onBlur={(e) => blurCls(e, !!error)}
        {...props}
      />
      <FieldError error={error} />
      <FieldHint hint={hint} />
    </div>
  )
}
