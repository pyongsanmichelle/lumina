import type { Page } from '../types'

const navItems: { id: Page; label: string; icon: string; group?: string }[] = [
  { id: 'dashboard', label: 'ダッシュボード', icon: '⊞', group: 'メイン' },
  { id: 'list', label: '受注一覧', icon: '≡', group: 'データ管理' },
  { id: 'detail', label: '詳細照会', icon: '◉', group: 'データ管理' },
  { id: 'form', label: '新規登録', icon: '+', group: 'データ管理' },
]

const groups = ['メイン', 'データ管理']

interface SidebarProps {
  currentPage: Page
  onNavigate: (page: Page) => void
}

export default function Sidebar({ currentPage, onNavigate }: SidebarProps) {
  return (
    <aside className="fixed left-0 top-0 h-full w-60 flex flex-col" style={{ background: '#0f172a', zIndex: 50 }}>
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-5 border-b" style={{ borderColor: '#1e293b' }}>
        <div className="w-8 h-8 rounded-lg flex items-center justify-center text-white text-sm font-bold" style={{ background: '#2563eb' }}>
          B
        </div>
        <div>
          <div className="text-white text-sm font-semibold leading-tight">BizAdmin</div>
          <div className="text-xs" style={{ color: '#64748b' }}>管理システム</div>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 overflow-y-auto">
        {groups.map((group) => {
          const items = navItems.filter((i) => i.group === group)
          return (
            <div key={group} className="mb-5">
              <div className="px-3 mb-2 text-xs font-semibold uppercase tracking-widest" style={{ color: '#475569' }}>
                {group}
              </div>
              {items.map((item) => {
                const active = currentPage === item.id
                return (
                  <button
                    key={item.id}
                    onClick={() => onNavigate(item.id)}
                    className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 mb-0.5 text-left"
                    style={{
                      background: active ? '#2563eb' : 'transparent',
                      color: active ? '#fff' : '#94a3b8',
                    }}
                    onMouseEnter={(e) => {
                      if (!active) (e.currentTarget as HTMLElement).style.background = '#1e293b'
                      if (!active) (e.currentTarget as HTMLElement).style.color = '#e2e8f0'
                    }}
                    onMouseLeave={(e) => {
                      if (!active) (e.currentTarget as HTMLElement).style.background = 'transparent'
                      if (!active) (e.currentTarget as HTMLElement).style.color = '#94a3b8'
                    }}
                  >
                    <span className="w-5 h-5 flex items-center justify-center text-base leading-none flex-shrink-0">
                      {item.icon}
                    </span>
                    {item.label}
                  </button>
                )
              })}
            </div>
          )
        })}
      </nav>

      {/* Footer */}
      <div className="px-4 py-4 border-t" style={{ borderColor: '#1e293b' }}>
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0" style={{ background: '#334155' }}>
            山田
          </div>
          <div className="flex-1 min-w-0">
            <div className="text-xs font-medium text-white truncate">山田 太郎</div>
            <div className="text-xs truncate" style={{ color: '#64748b' }}>管理者</div>
          </div>
          <button className="text-xs transition-colors" style={{ color: '#64748b' }}
            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.color = '#94a3b8')}
            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.color = '#64748b')}
          >
            ⏻
          </button>
        </div>
      </div>
    </aside>
  )
}
