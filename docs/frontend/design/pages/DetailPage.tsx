import type { Page } from '../types'

interface DetailPageProps {
  onNavigate: (page: Page) => void
}

const detail = {
  id: 'ORD-2024-0892',
  status: '受注済',
  client: '株式会社山田商事',
  clientCode: 'CLI-00241',
  contact: '山田 太郎',
  phone: '03-1234-5678',
  email: 'yamada@yamada-shoji.co.jp',
  product: 'デスクトップPC Pro（モデル: DPX-5000）',
  qty: '5台',
  unitPrice: '¥256,000',
  amount: '¥1,280,000',
  tax: '¥128,000',
  total: '¥1,408,000',
  orderDate: '2024-07-08',
  deliveryDate: '2024-07-20',
  staff: '田中 健二',
  note: '納品時に立ち会いが必要です。担当者（山田様）への連絡後、搬入してください。梱包材の持ち帰りをお願いします。',
  created: '2024-07-08 10:34',
  updated: '2024-07-08 10:34',
}

function ReadField({ label, value, mono }: { label: string; value: string; mono?: boolean }) {
  return (
    <div>
      <div className="text-xs font-medium mb-1" style={{ color: '#64748b' }}>{label}</div>
      <div
        className="text-sm py-2 px-3 rounded-lg"
        style={{
          background: '#f8fafc',
          color: '#1e293b',
          fontFamily: mono ? 'var(--font-mono)' : undefined,
          border: '1px solid #e2e8f0',
        }}
      >
        {value}
      </div>
    </div>
  )
}

function SectionTitle({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex items-center gap-2 mb-4">
      <div className="w-1 h-4 rounded-full" style={{ background: '#2563eb' }} />
      <span className="text-sm font-semibold" style={{ color: '#0f172a' }}>{children}</span>
    </div>
  )
}

export default function DetailPage({ onNavigate }: DetailPageProps) {
  return (
    <div className="space-y-5">
      {/* Action bar */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => onNavigate('list')}
          className="flex items-center gap-1.5 text-sm transition-colors"
          style={{ color: '#64748b' }}
          onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.color = '#2563eb')}
          onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.color = '#64748b')}
        >
          ← 一覧に戻る
        </button>
        <div className="flex items-center gap-2">
          <button
            className="px-4 py-2 rounded-lg text-sm border transition-colors"
            style={{ borderColor: '#e2e8f0', color: '#475569' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#94a3b8'; (e.currentTarget as HTMLElement).style.color = '#1e293b' }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#e2e8f0'; (e.currentTarget as HTMLElement).style.color = '#475569' }}
          >
            印刷
          </button>
          <button
            onClick={() => onNavigate('form')}
            className="px-5 py-2 rounded-lg text-sm font-semibold text-white transition-all"
            style={{ background: '#2563eb' }}
            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#1d4ed8')}
            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = '#2563eb')}
          >
            編集する
          </button>
        </div>
      </div>

      {/* Header card */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <div className="flex items-start justify-between">
          <div>
            <div className="text-xs font-medium mb-1" style={{ color: '#94a3b8' }}>受注番号</div>
            <div className="text-2xl font-bold" style={{ color: '#0f172a', fontFamily: 'var(--font-mono)' }}>{detail.id}</div>
          </div>
          <div className="flex items-center gap-3">
            <div className="text-right text-xs" style={{ color: '#94a3b8' }}>
              <div>登録：{detail.created}</div>
              <div>更新：{detail.updated}</div>
            </div>
            <span
              className="inline-block text-sm font-semibold px-4 py-1.5 rounded-full"
              style={{ background: '#eff6ff', color: '#2563eb' }}
            >
              {detail.status}
            </span>
          </div>
        </div>
      </div>

      {/* 顧客情報 */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <SectionTitle>顧客情報</SectionTitle>
        <div className="grid gap-4" style={{ gridTemplateColumns: '1fr 1fr 1fr' }}>
          <ReadField label="顧客コード" value={detail.clientCode} mono />
          <ReadField label="顧客名" value={detail.client} />
          <ReadField label="担当者名" value={detail.contact} />
          <ReadField label="電話番号" value={detail.phone} />
          <ReadField label="メールアドレス" value={detail.email} />
        </div>
      </div>

      {/* 受注情報 */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <SectionTitle>受注情報</SectionTitle>
        <div className="grid gap-4 mb-4" style={{ gridTemplateColumns: '2fr 1fr 1fr 1fr' }}>
          <ReadField label="商品名" value={detail.product} />
          <ReadField label="数量" value={detail.qty} />
          <ReadField label="単価" value={detail.unitPrice} />
          <ReadField label="小計" value={detail.amount} />
        </div>
        <div className="grid gap-4" style={{ gridTemplateColumns: '1fr 1fr 1fr 1fr' }}>
          <ReadField label="消費税（10%）" value={detail.tax} />
          <ReadField label="合計金額" value={detail.total} />
          <ReadField label="受注日" value={detail.orderDate} />
          <ReadField label="納品予定日" value={detail.deliveryDate} />
        </div>
      </div>

      {/* 担当者・備考 */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <SectionTitle>その他</SectionTitle>
        <div className="grid gap-4 mb-4" style={{ gridTemplateColumns: '1fr 2fr' }}>
          <ReadField label="担当者" value={detail.staff} />
        </div>
        <div>
          <div className="text-xs font-medium mb-1" style={{ color: '#64748b' }}>備考</div>
          <div
            className="text-sm py-3 px-3 rounded-lg leading-relaxed"
            style={{ background: '#f8fafc', color: '#1e293b', border: '1px solid #e2e8f0', minHeight: 80 }}
          >
            {detail.note}
          </div>
        </div>
      </div>
    </div>
  )
}
