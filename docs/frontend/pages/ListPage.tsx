import { useState } from 'react'
import type { Page } from '../types'

const allOrders = [
  { id: 'ORD-2024-0892', client: '株式会社山田商事', product: 'デスクトップPC Pro × 5台', amount: '¥1,280,000', status: '受注済', date: '2024-07-08', staff: '田中 健二' },
  { id: 'ORD-2024-0891', client: '有限会社佐藤電機', product: 'ノートPC Ultra × 3台', amount: '¥780,000', status: '出荷済', date: '2024-07-07', staff: '鈴木 明子' },
  { id: 'ORD-2024-0890', client: 'ABC テクノロジー株式会社', product: 'サーバーラック一式', amount: '¥4,500,000', status: '完了', date: '2024-07-06', staff: '田中 健二' },
  { id: 'ORD-2024-0889', client: '東京商事株式会社', product: 'ネットワーク機器セット', amount: '¥960,000', status: 'キャンセル', date: '2024-07-05', staff: '高橋 裕子' },
  { id: 'ORD-2024-0888', client: '大阪システム株式会社', product: 'クラウドライセンス 年間', amount: '¥360,000', status: '完了', date: '2024-07-04', staff: '鈴木 明子' },
  { id: 'ORD-2024-0887', client: '名古屋産業株式会社', product: 'セキュリティソフト × 50本', amount: '¥250,000', status: '受注済', date: '2024-07-03', staff: '高橋 裕子' },
  { id: 'ORD-2024-0886', client: '九州物流株式会社', product: 'プリンター × 8台', amount: '¥480,000', status: '出荷済', date: '2024-07-02', staff: '田中 健二' },
  { id: 'ORD-2024-0885', client: '北海道システム株式会社', product: 'タブレット端末 × 20台', amount: '¥900,000', status: '完了', date: '2024-07-01', staff: '鈴木 明子' },
]

const statusColor: Record<string, { bg: string; text: string }> = {
  受注済: { bg: '#eff6ff', text: '#2563eb' },
  出荷済: { bg: '#fef3c7', text: '#92400e' },
  完了: { bg: '#dcfce7', text: '#16a34a' },
  キャンセル: { bg: '#fee2e2', text: '#dc2626' },
}

interface ListPageProps {
  onNavigate: (page: Page) => void
}

export default function ListPage({ onNavigate }: ListPageProps) {
  const [keyword, setKeyword] = useState('')
  const [status, setStatus] = useState('')
  const [includeCancel, setIncludeCancel] = useState(false)
  const [page, setPage] = useState(1)
  const perPage = 5

  const filtered = allOrders.filter((o) => {
    if (status && o.status !== status) return false
    if (!includeCancel && o.status === 'キャンセル') return false
    if (keyword && !o.client.includes(keyword) && !o.id.includes(keyword) && !o.product.includes(keyword)) return false
    return true
  })

  const totalPages = Math.ceil(filtered.length / perPage)
  const shown = filtered.slice((page - 1) * perPage, page * perPage)

  function search() { setPage(1) }

  return (
    <div className="space-y-5">
      {/* Search Form */}
      <div className="bg-white rounded-xl border p-5" style={{ borderColor: '#e2e8f0' }}>
        <div className="text-sm font-semibold mb-4" style={{ color: '#0f172a' }}>検索条件</div>
        <div className="grid gap-4" style={{ gridTemplateColumns: '1fr 1fr 1fr auto' }}>
          {/* Keyword */}
          <div>
            <label className="block text-xs font-medium mb-1.5" style={{ color: '#475569' }}>キーワード</label>
            <input
              type="text"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="受注番号・顧客名・商品名"
              className="w-full text-sm px-3 py-2 rounded-lg border outline-none transition-all"
              style={{ borderColor: '#e2e8f0', color: '#0f172a' }}
              onFocus={(e) => { (e.target as HTMLInputElement).style.borderColor = '#2563eb'; (e.target as HTMLInputElement).style.boxShadow = '0 0 0 3px rgba(37,99,235,0.1)' }}
              onBlur={(e) => { (e.target as HTMLInputElement).style.borderColor = '#e2e8f0'; (e.target as HTMLInputElement).style.boxShadow = 'none' }}
            />
          </div>

          {/* Status */}
          <div>
            <label className="block text-xs font-medium mb-1.5" style={{ color: '#475569' }}>ステータス</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="w-full text-sm px-3 py-2 rounded-lg border outline-none transition-all appearance-none"
              style={{ borderColor: '#e2e8f0', color: '#0f172a', background: 'white' }}
              onFocus={(e) => { (e.target as HTMLSelectElement).style.borderColor = '#2563eb'; (e.target as HTMLSelectElement).style.boxShadow = '0 0 0 3px rgba(37,99,235,0.1)' }}
              onBlur={(e) => { (e.target as HTMLSelectElement).style.borderColor = '#e2e8f0'; (e.target as HTMLSelectElement).style.boxShadow = 'none' }}
            >
              <option value="">すべて</option>
              <option value="受注済">受注済</option>
              <option value="出荷済">出荷済</option>
              <option value="完了">完了</option>
              <option value="キャンセル">キャンセル</option>
            </select>
          </div>

          {/* Checkbox */}
          <div className="flex flex-col justify-end pb-0.5">
            <label className="flex items-center gap-2 text-sm cursor-pointer" style={{ color: '#475569' }}>
              <input
                type="checkbox"
                checked={includeCancel}
                onChange={(e) => setIncludeCancel(e.target.checked)}
                className="w-4 h-4 rounded"
                style={{ accentColor: '#2563eb' }}
              />
              キャンセル含む
            </label>
          </div>

          {/* Search button */}
          <div className="flex flex-col justify-end">
            <button
              onClick={search}
              className="px-6 py-2 rounded-lg text-sm font-semibold text-white transition-all"
              style={{ background: '#2563eb' }}
              onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#1d4ed8')}
              onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = '#2563eb')}
            >
              検索
            </button>
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl border overflow-hidden" style={{ borderColor: '#e2e8f0' }}>
        <div className="flex items-center justify-between px-5 py-3.5 border-b" style={{ borderColor: '#f1f5f9' }}>
          <div className="text-sm font-semibold" style={{ color: '#0f172a' }}>
            検索結果
            <span className="ml-2 text-xs font-normal" style={{ color: '#94a3b8' }}>{filtered.length}件</span>
          </div>
          <button
            onClick={() => onNavigate('form')}
            className="flex items-center gap-1.5 px-4 py-1.5 rounded-lg text-xs font-semibold text-white transition-all"
            style={{ background: '#2563eb' }}
            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#1d4ed8')}
            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = '#2563eb')}
          >
            + 新規登録
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr style={{ background: '#f8fafc' }}>
                {['受注番号', '顧客名', '商品', '金額', '担当者', 'ステータス', '受注日', '操作'].map((h) => (
                  <th key={h} className="text-left px-4 py-3 text-xs font-semibold" style={{ color: '#64748b', borderBottom: '1px solid #e2e8f0' }}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {shown.map((o, i) => (
                <tr
                  key={o.id}
                  className="transition-colors cursor-pointer"
                  style={{ borderBottom: i < shown.length - 1 ? '1px solid #f8fafc' : 'none' }}
                  onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#f8fafc')}
                  onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = 'transparent')}
                >
                  <td className="px-4 py-3.5">
                    <span className="font-mono text-xs font-medium" style={{ color: '#2563eb' }}>{o.id}</span>
                  </td>
                  <td className="px-4 py-3.5 font-medium" style={{ color: '#1e293b' }}>{o.client}</td>
                  <td className="px-4 py-3.5" style={{ color: '#475569', maxWidth: 200 }}>
                    <span className="truncate block" style={{ maxWidth: 200 }}>{o.product}</span>
                  </td>
                  <td className="px-4 py-3.5 font-semibold tabular-nums" style={{ color: '#0f172a' }}>{o.amount}</td>
                  <td className="px-4 py-3.5 text-xs" style={{ color: '#64748b' }}>{o.staff}</td>
                  <td className="px-4 py-3.5">
                    <span
                      className="inline-block text-xs font-semibold px-2.5 py-1 rounded-full"
                      style={statusColor[o.status] ?? { bg: '#f1f5f9', text: '#64748b' }}
                    >
                      {o.status}
                    </span>
                  </td>
                  <td className="px-4 py-3.5 text-xs tabular-nums" style={{ color: '#64748b' }}>{o.date}</td>
                  <td className="px-4 py-3.5">
                    <div className="flex gap-2">
                      <button
                        onClick={() => onNavigate('detail')}
                        className="text-xs px-2.5 py-1 rounded border transition-colors"
                        style={{ borderColor: '#e2e8f0', color: '#475569' }}
                        onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#2563eb'; (e.currentTarget as HTMLElement).style.color = '#2563eb' }}
                        onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#e2e8f0'; (e.currentTarget as HTMLElement).style.color = '#475569' }}
                      >
                        照会
                      </button>
                      <button
                        onClick={() => onNavigate('form')}
                        className="text-xs px-2.5 py-1 rounded border transition-colors"
                        style={{ borderColor: '#e2e8f0', color: '#475569' }}
                        onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#2563eb'; (e.currentTarget as HTMLElement).style.color = '#2563eb' }}
                        onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#e2e8f0'; (e.currentTarget as HTMLElement).style.color = '#475569' }}
                      >
                        編集
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {shown.length === 0 && (
                <tr>
                  <td colSpan={8} className="px-4 py-12 text-center text-sm" style={{ color: '#94a3b8' }}>
                    該当するデータがありません
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-5 py-3.5 border-t" style={{ borderColor: '#f1f5f9' }}>
            <div className="text-xs" style={{ color: '#94a3b8' }}>
              {(page - 1) * perPage + 1} 〜 {Math.min(page * perPage, filtered.length)} 件 / 全 {filtered.length} 件
            </div>
            <div className="flex items-center gap-1">
              <button
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page === 1}
                className="w-8 h-8 flex items-center justify-center rounded text-xs transition-colors"
                style={{ color: page === 1 ? '#cbd5e1' : '#475569', border: '1px solid #e2e8f0' }}
              >
                ‹
              </button>
              {Array.from({ length: totalPages }, (_, i) => i + 1).map((p) => (
                <button
                  key={p}
                  onClick={() => setPage(p)}
                  className="w-8 h-8 flex items-center justify-center rounded text-xs font-medium transition-all"
                  style={{
                    background: p === page ? '#2563eb' : 'transparent',
                    color: p === page ? '#fff' : '#475569',
                    border: `1px solid ${p === page ? '#2563eb' : '#e2e8f0'}`,
                  }}
                >
                  {p}
                </button>
              ))}
              <button
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                disabled={page === totalPages}
                className="w-8 h-8 flex items-center justify-center rounded text-xs transition-colors"
                style={{ color: page === totalPages ? '#cbd5e1' : '#475569', border: '1px solid #e2e8f0' }}
              >
                ›
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
