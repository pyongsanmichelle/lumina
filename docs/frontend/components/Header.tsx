const pageTitles: Record<string, string> = {
  dashboard: 'ダッシュボード',
  list: '受注一覧',
  detail: '受注詳細',
  form: '新規登録 / 編集',
  error: 'エラー画面プレビュー',
}

interface HeaderProps {
  currentPage: string
}

export default function Header({ currentPage }: HeaderProps) {
  return (
    <header
      className="fixed top-0 right-0 h-14 flex items-center justify-between px-6 border-b bg-white"
      style={{ left: 240, borderColor: '#e2e8f0', zIndex: 40 }}
    >
      <div className="flex items-center gap-2">
        <span className="text-xs" style={{ color: '#94a3b8' }}>管理システム</span>
        <span className="text-xs" style={{ color: '#cbd5e1' }}>/</span>
        <span className="text-sm font-semibold" style={{ color: '#0f172a' }}>{pageTitles[currentPage] ?? currentPage}</span>
      </div>

      <div className="flex items-center gap-3">
        {/* Search */}
        <div className="relative hidden md:block">
          <input
            type="text"
            placeholder="クイック検索..."
            className="text-sm pl-8 pr-3 py-1.5 rounded-lg border outline-none transition-all"
            style={{ borderColor: '#e2e8f0', background: '#f8fafc', color: '#0f172a', width: 200 }}
            onFocus={(e) => { (e.target as HTMLInputElement).style.borderColor = '#2563eb'; (e.target as HTMLInputElement).style.boxShadow = '0 0 0 3px rgba(37,99,235,0.1)' }}
            onBlur={(e) => { (e.target as HTMLInputElement).style.borderColor = '#e2e8f0'; (e.target as HTMLInputElement).style.boxShadow = 'none' }}
          />
          <span className="absolute left-2.5 top-1/2 -translate-y-1/2 text-xs" style={{ color: '#94a3b8' }}>🔍</span>
        </div>

        {/* Notification */}
        <button className="relative w-9 h-9 rounded-lg flex items-center justify-center transition-colors border"
          style={{ borderColor: '#e2e8f0', background: '#f8fafc' }}
          onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#f1f5f9')}
          onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = '#f8fafc')}
        >
          <span className="text-base">🔔</span>
          <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full" style={{ background: '#ef4444' }} />
        </button>

        {/* Help */}
        <button className="w-9 h-9 rounded-lg flex items-center justify-center transition-colors border text-sm"
          style={{ borderColor: '#e2e8f0', background: '#f8fafc', color: '#64748b' }}
          onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#f1f5f9')}
          onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = '#f8fafc')}
        >
          ?
        </button>

        {/* Avatar */}
        <div className="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-semibold cursor-pointer" style={{ background: '#2563eb' }}>
          山
        </div>
      </div>
    </header>
  )
}
