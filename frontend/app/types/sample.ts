/** 画面表示用ダミーデータの型定義（API通信を想定しない） */

export type SamplePage = 'dashboard' | 'list' | 'detail' | 'form'

export type ErrorType = '404' | '500'

export interface SalesData {
  month: string
  売上: number
  目標: number
}

export interface CategoryData {
  name: string
  value: number
}

export interface KpiData {
  label: string
  value: string
  delta: string
  deltaUp: boolean
  sub: string
}

export interface NotificationItem {
  id: number
  type: 'warning' | 'info' | 'success' | 'error'
  title: string
  body: string
  time: string
}

export interface OrderItem {
  id: string
  client: string
  product: string
  amount: string
  status: string
  date: string
  staff: string
}

export interface OrderDetail {
  id: string
  status: string
  client: string
  clientCode: string
  contact: string
  phone: string
  email: string
  product: string
  qty: string
  unitPrice: string
  amount: string
  tax: string
  total: string
  orderDate: string
  deliveryDate: string
  staff: string
  note: string
  created: string
  updated: string
}

export interface FormValues {
  clientCode: string
  clientName: string
  contact: string
  phone: string
  email: string
  product: string
  qty: string
  unitPrice: string
  orderDate: string
  deliveryDate: string
  status: string
  staff: string
  note: string
}

export type FormErrors = Partial<Record<keyof FormValues, string>>

export interface ToastData {
  id: string
  type: 'success' | 'warning' | 'error'
  title: string
  message?: string
}

export const ALERT_TYPE_COLORS: Record<NotificationItem['type'], string> = {
  warning: '#f59e0b',
  info: '#2563eb',
  success: '#22c55e',
  error: '#ef4444',
}

export const STATUS_COLORS: Record<string, { bg: string; text: string }> = {
  '受注済': { bg: '#eff6ff', text: '#2563eb' },
  '出荷済': { bg: '#fef3c7', text: '#92400e' },
  '完了': { bg: '#dcfce7', text: '#16a34a' },
  'キャンセル': { bg: '#fee2e2', text: '#dc2626' },
}

export const KPI_DATA: KpiData[] = [
  { label: '今月の売上', value: '¥48,200,000', delta: '+12.4%', deltaUp: true, sub: '前月比' },
  { label: '新規受注件数', value: '142件', delta: '+8件', deltaUp: true, sub: '今月' },
  { label: '未処理案件', value: '23件', delta: '-5件', deltaUp: true, sub: '昨日比' },
  { label: '顧客満足度', value: '4.7 / 5.0', delta: '+0.2', deltaUp: true, sub: '今月平均' },
]

export const SALES_DATA: SalesData[] = [
  { month: '1月', 売上: 3200000, 目標: 3000000 },
  { month: '2月', 売上: 2800000, 目標: 3000000 },
  { month: '3月', 売上: 3600000, 目標: 3200000 },
  { month: '4月', 売上: 4100000, 目標: 3500000 },
  { month: '5月', 売上: 3900000, 目標: 3800000 },
  { month: '6月', 売上: 4500000, 目標: 4000000 },
  { month: '7月', 売上: 4800000, 目標: 4200000 },
]

export const CATEGORY_DATA: CategoryData[] = [
  { name: 'ソフトウェア', value: 42 },
  { name: 'ハードウェア', value: 28 },
  { name: 'サービス', value: 18 },
  { name: 'サポート', value: 12 },
]

export const NOTIFICATIONS: NotificationItem[] = [
  { id: 1, type: 'warning', title: '在庫アラート', body: '商品ID #2041「デスクトップPC Pro」の在庫が残り3台です。', time: '5分前' },
  { id: 2, type: 'info', title: '新規受注', body: '株式会社山田商事より受注 #ORD-2024-0892 が届きました。', time: '23分前' },
  { id: 3, type: 'success', title: '決済完了', body: '受注 #ORD-2024-0881 の入金が確認されました（¥1,280,000）。', time: '1時間前' },
  { id: 4, type: 'info', title: 'レポート生成完了', body: '2024年6月度 月次売上レポートが生成されました。', time: '3時間前' },
]

export const ALL_ORDERS: OrderItem[] = [
  { id: 'ORD-2024-0892', client: '株式会社山田商事', product: 'デスクトップPC Pro × 5台', amount: '¥1,280,000', status: '受注済', date: '2024-07-08', staff: '田中 健二' },
  { id: 'ORD-2024-0891', client: '有限会社佐藤電機', product: 'ノートPC Ultra × 3台', amount: '¥780,000', status: '出荷済', date: '2024-07-07', staff: '鈴木 明子' },
  { id: 'ORD-2024-0890', client: 'ABC テクノロジー株式会社', product: 'サーバーラック一式', amount: '¥4,500,000', status: '完了', date: '2024-07-06', staff: '田中 健二' },
  { id: 'ORD-2024-0889', client: '東京商事株式会社', product: 'ネットワーク機器セット', amount: '¥960,000', status: 'キャンセル', date: '2024-07-05', staff: '高橋 裕子' },
  { id: 'ORD-2024-0888', client: '大阪システム株式会社', product: 'クラウドライセンス 年間', amount: '¥360,000', status: '完了', date: '2024-07-04', staff: '鈴木 明子' },
  { id: 'ORD-2024-0887', client: '名古屋産業株式会社', product: 'セキュリティソフト × 50本', amount: '¥250,000', status: '受注済', date: '2024-07-03', staff: '高橋 裕子' },
  { id: 'ORD-2024-0886', client: '九州物流株式会社', product: 'プリンター × 8台', amount: '¥480,000', status: '出荷済', date: '2024-07-02', staff: '田中 健二' },
  { id: 'ORD-2024-0885', client: '北海道システム株式会社', product: 'タブレット端末 × 20台', amount: '¥900,000', status: '完了', date: '2024-07-01', staff: '鈴木 明子' },
]

export const ORDER_DETAIL: OrderDetail = {
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

export function fmtCurrency(v: number): string {
  if (v >= 1000000) return `¥${(v / 1000000).toFixed(1)}M`
  if (v >= 1000) return `¥${(v / 1000).toFixed(0)}K`
  return `¥${v}`
}

export function validateForm(values: FormValues): FormErrors {
  const errors: FormErrors = {}

  if (!values.clientName.trim()) errors.clientName = 'この項目は必須入力です'
  if (!values.contact.trim()) errors.contact = 'この項目は必須入力です'

  if (values.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
    errors.email = 'メールアドレスの形式が正しくありません'
  }

  if (!values.product.trim()) errors.product = 'この項目は必須入力です'

  if (!values.qty) {
    errors.qty = 'この項目は必須入力です'
  } else if (Number(values.qty) < 1) {
    errors.qty = '1以上の値を入力してください'
  }

  if (!values.unitPrice) {
    errors.unitPrice = 'この項目は必須入力です'
  } else if (Number(values.unitPrice) < 0) {
    errors.unitPrice = '0以上の値を入力してください'
  }

  if (!values.orderDate) errors.orderDate = 'この項目は必須入力です'

  if (values.deliveryDate && values.orderDate && values.deliveryDate < values.orderDate) {
    errors.deliveryDate = '受注日より後の日付を指定してください'
  }

  if (!values.status) errors.status = 'ステータスを選択してください'
  if (!values.staff) errors.staff = '担当者を選択してください'

  return errors
}

export const EMPTY_FORM_VALUES: FormValues = {
  clientCode: '', clientName: '', contact: '', phone: '', email: '',
  product: '', qty: '', unitPrice: '', orderDate: '', deliveryDate: '',
  status: '', staff: '', note: '',
}