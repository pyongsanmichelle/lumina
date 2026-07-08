import {
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'

const salesData = [
  { month: '1月', 売上: 3200000, 目標: 3000000 },
  { month: '2月', 売上: 2800000, 目標: 3000000 },
  { month: '3月', 売上: 3600000, 目標: 3200000 },
  { month: '4月', 売上: 4100000, 目標: 3500000 },
  { month: '5月', 売上: 3900000, 目標: 3800000 },
  { month: '6月', 売上: 4500000, 目標: 4000000 },
  { month: '7月', 売上: 4800000, 目標: 4200000 },
]

const categoryData = [
  { name: 'ソフトウェア', value: 42 },
  { name: 'ハードウェア', value: 28 },
  { name: 'サービス', value: 18 },
  { name: 'サポート', value: 12 },
]

const notifications = [
  { id: 1, type: 'warning', title: '在庫アラート', body: '商品ID #2041「デスクトップPC Pro」の在庫が残り3台です。', time: '5分前' },
  { id: 2, type: 'info', title: '新規受注', body: '株式会社山田商事より受注 #ORD-2024-0892 が届きました。', time: '23分前' },
  { id: 3, type: 'success', title: '決済完了', body: '受注 #ORD-2024-0881 の入金が確認されました（¥1,280,000）。', time: '1時間前' },
  { id: 4, type: 'info', title: 'レポート生成完了', body: '2024年6月度 月次売上レポートが生成されました。', time: '3時間前' },
]

const typeColor: Record<string, string> = {
  warning: '#f59e0b',
  info: '#2563eb',
  success: '#22c55e',
  error: '#ef4444',
}

const kpis = [
  { label: '今月の売上', value: '¥48,200,000', delta: '+12.4%', deltaUp: true, sub: '前月比' },
  { label: '新規受注件数', value: '142件', delta: '+8件', deltaUp: true, sub: '今月' },
  { label: '未処理案件', value: '23件', delta: '-5件', deltaUp: true, sub: '昨日比' },
  { label: '顧客満足度', value: '4.7 / 5.0', delta: '+0.2', deltaUp: true, sub: '今月平均' },
]

function fmt(v: number) {
  if (v >= 1000000) return `¥${(v / 1000000).toFixed(1)}M`
  if (v >= 1000) return `¥${(v / 1000).toFixed(0)}K`
  return `¥${v}`
}

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      {/* KPI Cards */}
      <div className="grid grid-cols-2 gap-4" style={{ gridTemplateColumns: 'repeat(4, 1fr)' }}>
        {kpis.map((kpi) => (
          <div key={kpi.label} className="bg-white rounded-xl p-5 border" style={{ borderColor: '#e2e8f0' }}>
            <div className="text-xs font-medium mb-3" style={{ color: '#64748b' }}>{kpi.label}</div>
            <div className="text-2xl font-bold mb-2" style={{ color: '#0f172a' }}>{kpi.value}</div>
            <div className="flex items-center gap-1.5">
              <span
                className="text-xs font-semibold px-1.5 py-0.5 rounded"
                style={{
                  background: kpi.deltaUp ? '#dcfce7' : '#fee2e2',
                  color: kpi.deltaUp ? '#16a34a' : '#dc2626',
                }}
              >
                {kpi.deltaUp ? '▲' : '▼'} {kpi.delta}
              </span>
              <span className="text-xs" style={{ color: '#94a3b8' }}>{kpi.sub}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid gap-4" style={{ gridTemplateColumns: '2fr 1fr' }}>
        {/* Area Chart */}
        <div className="bg-white rounded-xl p-5 border" style={{ borderColor: '#e2e8f0' }}>
          <div className="flex items-center justify-between mb-5">
            <div>
              <div className="text-sm font-semibold" style={{ color: '#0f172a' }}>月次売上推移</div>
              <div className="text-xs mt-0.5" style={{ color: '#94a3b8' }}>2024年 1月 〜 7月</div>
            </div>
            <div className="flex items-center gap-4 text-xs" style={{ color: '#64748b' }}>
              <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full inline-block" style={{ background: '#2563eb' }} />実績</span>
              <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full inline-block" style={{ background: '#e2e8f0' }} />目標</span>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={salesData} margin={{ top: 0, right: 0, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="salesGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#2563eb" stopOpacity={0.15} />
                  <stop offset="95%" stopColor="#2563eb" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 11, fill: '#94a3b8' }} axisLine={false} tickLine={false} />
              <YAxis tickFormatter={fmt} tick={{ fontSize: 10, fill: '#94a3b8' }} axisLine={false} tickLine={false} width={52} />
              <Tooltip
                formatter={(v: number) => [`¥${v.toLocaleString()}`, '']}
                contentStyle={{ border: '1px solid #e2e8f0', borderRadius: 8, fontSize: 12, boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}
              />
              <Area type="monotone" dataKey="目標" stroke="#e2e8f0" strokeWidth={2} fill="none" dot={false} />
              <Area type="monotone" dataKey="売上" stroke="#2563eb" strokeWidth={2} fill="url(#salesGrad)" dot={{ r: 3, fill: '#2563eb', strokeWidth: 0 }} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Bar Chart */}
        <div className="bg-white rounded-xl p-5 border" style={{ borderColor: '#e2e8f0' }}>
          <div className="text-sm font-semibold mb-1" style={{ color: '#0f172a' }}>カテゴリ別構成比</div>
          <div className="text-xs mb-5" style={{ color: '#94a3b8' }}>売上シェア（%）</div>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={categoryData} layout="vertical" margin={{ top: 0, right: 16, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" horizontal={false} />
              <XAxis type="number" tick={{ fontSize: 10, fill: '#94a3b8' }} axisLine={false} tickLine={false} domain={[0, 50]} />
              <YAxis type="category" dataKey="name" tick={{ fontSize: 11, fill: '#64748b' }} axisLine={false} tickLine={false} width={72} />
              <Tooltip
                formatter={(v: number) => [`${v}%`, 'シェア']}
                contentStyle={{ border: '1px solid #e2e8f0', borderRadius: 8, fontSize: 12 }}
              />
              <Bar dataKey="value" fill="#2563eb" radius={[0, 4, 4, 0]} barSize={16} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Notifications */}
      <div className="bg-white rounded-xl border" style={{ borderColor: '#e2e8f0' }}>
        <div className="flex items-center justify-between px-5 py-4 border-b" style={{ borderColor: '#f1f5f9' }}>
          <div className="text-sm font-semibold" style={{ color: '#0f172a' }}>最近のお知らせ</div>
          <button className="text-xs font-medium transition-colors" style={{ color: '#2563eb' }}>すべて見る →</button>
        </div>
        <div>
          {notifications.map((n, i) => (
            <div
              key={n.id}
              className="flex items-start gap-4 px-5 py-4 transition-colors cursor-pointer"
              style={{ borderBottom: i < notifications.length - 1 ? '1px solid #f8fafc' : 'none' }}
              onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#f8fafc')}
              onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = 'transparent')}
            >
              <div
                className="w-2 h-2 rounded-full mt-1.5 flex-shrink-0"
                style={{ background: typeColor[n.type] }}
              />
              <div className="flex-1 min-w-0">
                <div className="text-sm font-medium" style={{ color: '#1e293b' }}>{n.title}</div>
                <div className="text-xs mt-0.5 leading-relaxed" style={{ color: '#64748b' }}>{n.body}</div>
              </div>
              <div className="text-xs flex-shrink-0 mt-0.5" style={{ color: '#94a3b8' }}>{n.time}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
