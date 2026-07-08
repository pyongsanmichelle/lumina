import { useState } from 'react'
import type { Page } from '../types'
import { TextField, SelectField, TextareaField } from '../components/ui/FormField'
import { AlertBanner } from '../components/ui/Alert'
import { useToast } from '../components/ui/Toast'

interface FormPageProps {
  onNavigate: (page: Page) => void
}

interface FormValues {
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

type FormErrors = Partial<Record<keyof FormValues, string>>

function validate(values: FormValues): FormErrors {
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

function SectionTitle({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex items-center gap-2 mb-4">
      <div className="w-1 h-4 rounded-full" style={{ background: '#2563eb' }} />
      <span className="text-sm font-semibold" style={{ color: '#0f172a' }}>{children}</span>
    </div>
  )
}

const EMPTY: FormValues = {
  clientCode: '', clientName: '', contact: '', phone: '', email: '',
  product: '', qty: '', unitPrice: '', orderDate: '', deliveryDate: '',
  status: '', staff: '', note: '',
}

export default function FormPage({ onNavigate }: FormPageProps) {
  const { addToast } = useToast()
  const [form, setForm] = useState<FormValues>(EMPTY)
  const [errors, setErrors] = useState<FormErrors>({})
  const [submitted, setSubmitted] = useState(false)
  const [apiError, setApiError] = useState(false)

  function set(k: keyof FormValues, v: string) {
    const next = { ...form, [k]: v }
    setForm(next)
    // Re-validate touched field live after first submit attempt
    if (submitted) {
      const e = validate(next)
      setErrors(e)
    }
  }

  function handleSave(e: React.FormEvent) {
    e.preventDefault()
    setSubmitted(true)
    const errs = validate(form)
    setErrors(errs)

    if (Object.keys(errs).length > 0) {
      addToast({
        type: 'error',
        title: '入力エラーがあります',
        message: '赤くハイライトされた項目を確認してください。',
      })
      return
    }

    // Simulate API error scenario (toggle for demo)
    if (apiError) {
      addToast({
        type: 'error',
        title: '通信に失敗しました',
        message: 'サーバーとの接続が切断されました。再度お試しください。',
      })
      return
    }

    addToast({
      type: 'success',
      title: 'データを保存しました',
      message: '受注情報が正常に登録されました。',
    })
    setForm(EMPTY)
    setErrors({})
    setSubmitted(false)
  }

  const errorCount = Object.keys(errors).length

  return (
    <form onSubmit={handleSave} noValidate className="space-y-5">
      {/* Back + demo toggles */}
      <div className="flex items-center justify-between">
        <button
          type="button"
          onClick={() => onNavigate('list')}
          className="flex items-center gap-1.5 text-sm transition-colors"
          style={{ color: '#64748b' }}
          onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.color = '#2563eb')}
          onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.color = '#64748b')}
        >
          ← 一覧に戻る
        </button>
        {/* Demo controls */}
        <label className="flex items-center gap-2 text-xs cursor-pointer select-none" style={{ color: '#94a3b8' }}>
          <input
            type="checkbox"
            checked={apiError}
            onChange={(e) => setApiError(e.target.checked)}
            style={{ accentColor: '#ef4444' }}
          />
          APIエラーをシミュレート
        </label>
      </div>

      {/* Alert banner: validation summary */}
      {submitted && errorCount > 0 && (
        <AlertBanner
          type="error"
          title={`${errorCount}件の入力エラーがあります`}
          message="すべての必須項目を正しく入力してから保存してください。"
          onDismiss={() => { setErrors({}); setSubmitted(false) }}
        />
      )}

      {/* Warning: session expiry demo */}
      <AlertBanner
        type="warning"
        title="セッションの期限が近づいています"
        message="あと10分でセッションが切れます。作業中のデータを保存してください。"
      />

      {/* 顧客情報 */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <SectionTitle>顧客情報</SectionTitle>
        <div className="grid gap-4" style={{ gridTemplateColumns: '1fr 2fr 1fr' }}>
          <TextField
            label="顧客コード"
            value={form.clientCode}
            onChange={(e) => set('clientCode', e.target.value)}
            placeholder="CLI-00000"
            error={errors.clientCode}
            hint="既存の顧客は自動補完されます"
          />
          <TextField
            label="顧客名"
            required
            value={form.clientName}
            onChange={(e) => set('clientName', e.target.value)}
            placeholder="株式会社〇〇商事"
            error={errors.clientName}
          />
          <TextField
            label="担当者名"
            required
            value={form.contact}
            onChange={(e) => set('contact', e.target.value)}
            placeholder="山田 太郎"
            error={errors.contact}
          />
          <TextField
            label="電話番号"
            type="tel"
            value={form.phone}
            onChange={(e) => set('phone', e.target.value)}
            placeholder="03-0000-0000"
            error={errors.phone}
          />
          <div style={{ gridColumn: 'span 2' }}>
            <TextField
              label="メールアドレス"
              type="email"
              value={form.email}
              onChange={(e) => set('email', e.target.value)}
              placeholder="example@company.co.jp"
              error={errors.email}
            />
          </div>
        </div>
      </div>

      {/* 受注情報 */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <SectionTitle>受注情報</SectionTitle>
        <div className="grid gap-4 mb-4" style={{ gridTemplateColumns: '3fr 1fr 1fr' }}>
          <TextField
            label="商品名"
            required
            value={form.product}
            onChange={(e) => set('product', e.target.value)}
            placeholder="商品名・型番を入力"
            error={errors.product}
          />
          <TextField
            label="数量"
            required
            type="number"
            min={1}
            value={form.qty}
            onChange={(e) => set('qty', e.target.value)}
            placeholder="1"
            error={errors.qty}
          />
          <TextField
            label="単価（税抜）"
            required
            type="number"
            min={0}
            value={form.unitPrice}
            onChange={(e) => set('unitPrice', e.target.value)}
            placeholder="0"
            error={errors.unitPrice}
            hint="円単位で入力"
          />
        </div>
        <div className="grid gap-4" style={{ gridTemplateColumns: '1fr 1fr 1fr 1fr' }}>
          <TextField
            label="受注日"
            required
            type="date"
            value={form.orderDate}
            onChange={(e) => set('orderDate', e.target.value)}
            error={errors.orderDate}
          />
          <TextField
            label="納品予定日"
            type="date"
            value={form.deliveryDate}
            onChange={(e) => set('deliveryDate', e.target.value)}
            error={errors.deliveryDate}
          />
          <SelectField
            label="ステータス"
            required
            value={form.status}
            onChange={(e) => set('status', e.target.value)}
            error={errors.status}
          >
            <option value="">選択してください</option>
            <option value="受注済">受注済</option>
            <option value="出荷済">出荷済</option>
            <option value="完了">完了</option>
            <option value="キャンセル">キャンセル</option>
          </SelectField>
          <SelectField
            label="担当者"
            required
            value={form.staff}
            onChange={(e) => set('staff', e.target.value)}
            error={errors.staff}
          >
            <option value="">選択してください</option>
            <option value="田中 健二">田中 健二</option>
            <option value="鈴木 明子">鈴木 明子</option>
            <option value="高橋 裕子">高橋 裕子</option>
          </SelectField>
        </div>
      </div>

      {/* 備考 */}
      <div className="bg-white rounded-xl border px-6 py-5" style={{ borderColor: '#e2e8f0' }}>
        <SectionTitle>備考・メモ</SectionTitle>
        <TextareaField
          label="備考"
          value={form.note}
          onChange={(e) => set('note', e.target.value)}
          placeholder="特記事項・納品時の注意点などを入力してください"
          rows={5}
          error={errors.note}
        />
      </div>

      {/* Actions */}
      <div className="bg-white rounded-xl border px-6 py-4 flex items-center justify-between" style={{ borderColor: '#e2e8f0' }}>
        <div className="text-xs" style={{ color: '#94a3b8' }}>
          <span className="font-semibold" style={{ color: '#ef4444' }}>*</span> 必須項目をすべて入力してください
        </div>
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={() => onNavigate('list')}
            className="px-6 py-2.5 rounded-lg text-sm border transition-colors"
            style={{ borderColor: '#e2e8f0', color: '#475569' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#94a3b8'; (e.currentTarget as HTMLElement).style.color = '#1e293b' }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.borderColor = '#e2e8f0'; (e.currentTarget as HTMLElement).style.color = '#475569' }}
          >
            キャンセル
          </button>
          <button
            type="submit"
            className="px-8 py-2.5 rounded-lg text-sm font-semibold text-white transition-all"
            style={{ background: '#2563eb' }}
            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#1d4ed8')}
            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = '#2563eb')}
          >
            保存する
          </button>
        </div>
      </div>
    </form>
  )
}
