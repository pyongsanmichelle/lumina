import { useState } from 'react'

type ErrorType = '404' | '500'

interface ErrorPageProps {
  type?: ErrorType
  errorCode?: string
  onHome?: () => void
  onRetry?: () => void
}

const meta = {
  '404': {
    code: '404',
    label: 'Not Found',
    headline: 'お探しのページは\n見つかりませんでした',
    body: 'アクセスしようとしたページは存在しないか、移動・削除された可能性があります。\nURLをご確認のうえ、再度お試しください。',
    accent: '#2563eb',
    accentLight: '#eff6ff',
    svgColor: '#bfdbfe',
  },
  '500': {
    code: '500',
    label: 'Internal Server Error',
    headline: 'システムエラーが\n発生しました',
    body: '予期しないエラーが発生しました。しばらく時間をおいてから再度お試しください。\n問題が続く場合は、システム管理者へお問い合わせください。',
    accent: '#dc2626',
    accentLight: '#fef2f2',
    svgColor: '#fecaca',
  },
}

const SAMPLE_ERROR_CODE = 'ERR_INTERNAL_5023\nat fetchOrderData (api/orders.ts:142)\nat async DashboardPage.loadData (pages/dashboard.ts:38)'

/** Standalone error page — no sidebar/header */
export function ErrorPage({ type = '404', errorCode, onHome, onRetry }: ErrorPageProps) {
  const m = meta[type]
  const [codeOpen, setCodeOpen] = useState(false)

  return (
    <div
      className="min-h-screen flex flex-col items-center justify-center px-6 py-16"
      style={{ background: '#f8fafc', fontFamily: "'Inter', 'Noto Sans JP', system-ui, sans-serif" }}
    >
      {/* Visual */}
      <div className="relative mb-10 flex items-center justify-center">
        {/* Background circle */}
        <div
          className="absolute rounded-full"
          style={{ width: 220, height: 220, background: m.accentLight, opacity: 0.6 }}
        />
        {/* Error code large */}
        <div
          className="relative text-center"
          style={{ color: m.svgColor, fontSize: 120, fontWeight: 800, letterSpacing: '-0.05em', lineHeight: 1, userSelect: 'none' }}
        >
          {m.code}
        </div>
      </div>

      {/* Text */}
      <div className="text-center max-w-md">
        <div
          className="inline-block text-xs font-semibold tracking-widest uppercase mb-4 px-3 py-1 rounded-full"
          style={{ background: m.accentLight, color: m.accent }}
        >
          {m.label}
        </div>
        <h1
          className="text-2xl font-bold mb-4 whitespace-pre-line leading-snug"
          style={{ color: '#0f172a' }}
        >
          {m.headline}
        </h1>
        <p
          className="text-sm leading-relaxed whitespace-pre-line mb-8"
          style={{ color: '#64748b' }}
        >
          {m.body}
        </p>
      </div>

      {/* Error code detail (500 only) */}
      {type === '500' && (
        <div className="w-full max-w-md mb-6">
          <button
            onClick={() => setCodeOpen((o) => !o)}
            className="flex items-center gap-2 text-xs font-medium mb-2 transition-colors"
            style={{ color: '#94a3b8' }}
            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.color = '#64748b')}
            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.color = '#94a3b8')}
          >
            <span style={{ display: 'inline-block', transform: codeOpen ? 'rotate(90deg)' : 'none', transition: 'transform 0.15s' }}>▶</span>
            エラー詳細を{codeOpen ? '隠す' : '表示する'}
          </button>
          {codeOpen && (
            <pre
              className="w-full text-xs p-4 rounded-xl overflow-x-auto leading-relaxed"
              style={{
                background: '#0f172a',
                color: '#94a3b8',
                fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
                border: '1px solid #1e293b',
              }}
            >
              {errorCode ?? SAMPLE_ERROR_CODE}
            </pre>
          )}
        </div>
      )}

      {/* Actions */}
      <div className="flex items-center gap-3">
        {onHome !== undefined && (
          <button
            onClick={onHome}
            className="px-6 py-2.5 rounded-xl text-sm font-semibold text-white transition-all"
            style={{ background: m.accent }}
            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.opacity = '0.88')}
            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.opacity = '1')}
          >
            ホームに戻る
          </button>
        )}
        {onRetry !== undefined && (
          <button
            onClick={onRetry}
            className="px-6 py-2.5 rounded-xl text-sm font-semibold border transition-all"
            style={{ borderColor: '#e2e8f0', color: '#475569', background: 'white' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#94a3b8'; (e.currentTarget as HTMLElement).style.color = '#1e293b' }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#e2e8f0'; (e.currentTarget as HTMLElement).style.color = '#475569' }}
          >
            再試行する
          </button>
        )}
      </div>

      {/* Footer */}
      <p className="mt-12 text-xs" style={{ color: '#cbd5e1' }}>
        BizAdmin 管理システム — サポート: support@bizadmin.co.jp
      </p>
    </div>
  )
}

/** Side-by-side preview of both error pages for the design system demo */
export function ErrorPagePreview({ onHome }: { onHome: () => void }) {
  const [activeTab, setActiveTab] = useState<ErrorType>('404')

  return (
    <div>
      {/* Tab switcher */}
      <div className="flex items-center gap-2 mb-6">
        {(['404', '500'] as ErrorType[]).map((t) => (
          <button
            key={t}
            onClick={() => setActiveTab(t)}
            className="px-4 py-2 rounded-lg text-sm font-medium border transition-all"
            style={{
              background: activeTab === t ? '#0f172a' : 'white',
              color: activeTab === t ? '#f8fafc' : '#475569',
              borderColor: activeTab === t ? '#0f172a' : '#e2e8f0',
            }}
          >
            {t} {t === '404' ? 'Not Found' : 'Server Error'}
          </button>
        ))}
        <span className="ml-auto text-xs" style={{ color: '#94a3b8' }}>エラー画面プレビュー</span>
      </div>

      <div className="rounded-2xl overflow-hidden border" style={{ borderColor: '#e2e8f0' }}>
        <ErrorPage
          type={activeTab}
          onHome={onHome}
          onRetry={activeTab === '500' ? () => window.location.reload() : undefined}
        />
      </div>
    </div>
  )
}
