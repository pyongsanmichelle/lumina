import { useState } from 'react'
import type { Page } from './types'
import Sidebar from './components/Sidebar'
import Header from './components/Header'
import { ToastProvider } from './components/ui/Toast'
import DashboardPage from './pages/DashboardPage'
import ListPage from './pages/ListPage'
import DetailPage from './pages/DetailPage'
import FormPage from './pages/FormPage'
import { ErrorPagePreview } from './pages/ErrorPage'

type ExtendedPage = Page | 'error'

const navPage = (p: ExtendedPage): Page =>
  p === 'error' ? 'dashboard' : p

function AppShell({ page, setPage }: { page: ExtendedPage; setPage: (p: ExtendedPage) => void }) {
  function renderPage() {
    switch (page) {
      case 'dashboard': return <DashboardPage />
      case 'list': return <ListPage onNavigate={setPage} />
      case 'detail': return <DetailPage onNavigate={setPage} />
      case 'form': return <FormPage onNavigate={setPage} />
      case 'error': return <ErrorPagePreview onHome={() => setPage('dashboard')} />
    }
  }

  return (
    <div className="flex min-h-screen" style={{ background: '#f8fafc' }}>
      <Sidebar currentPage={navPage(page)} onNavigate={setPage} />
      <Header currentPage={page} />
      <main className="flex-1 overflow-y-auto" style={{ marginLeft: 240, paddingTop: 56 }}>
        <div className={page === 'error' ? '' : 'p-6 max-w-screen-xl'}>
          {renderPage()}
        </div>
      </main>
    </div>
  )
}

export default function App() {
  const [page, setPage] = useState<ExtendedPage>('dashboard')

  return (
    <ToastProvider>
      <AppShell page={page} setPage={setPage} />
      {/* Floating nav pill for error page demo */}
      <div
        className="fixed flex items-center gap-2"
        style={{ bottom: 20, left: '50%', transform: 'translateX(-50%)', zIndex: 100 }}
      >
        <button
          onClick={() => setPage('error')}
          className="px-4 py-2 rounded-full text-xs font-semibold shadow-lg border transition-all"
          style={{
            background: page === 'error' ? '#0f172a' : 'white',
            color: page === 'error' ? '#f8fafc' : '#475569',
            borderColor: page === 'error' ? '#0f172a' : '#e2e8f0',
          }}
          onMouseEnter={(e) => { if (page !== 'error') (e.currentTarget as HTMLElement).style.borderColor = '#94a3b8' }}
          onMouseLeave={(e) => { if (page !== 'error') (e.currentTarget as HTMLElement).style.borderColor = '#e2e8f0' }}
        >
          🚨 エラー画面プレビュー
        </button>
      </div>
    </ToastProvider>
  )
}
