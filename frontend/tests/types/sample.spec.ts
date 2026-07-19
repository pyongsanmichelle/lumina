import { describe, it, expect } from 'vitest'
import {
  fmtCurrency,
  validateForm,
  EMPTY_FORM_VALUES,
  type FormValues,
} from '~/types/sample'

function makeValidValues(overrides: Partial<FormValues> = {}): FormValues {
  return {
    ...EMPTY_FORM_VALUES,
    clientName: '株式会社テスト',
    contact: '担当 太郎',
    product: 'テスト商品',
    qty: '1',
    unitPrice: '1000',
    orderDate: '2024-07-01',
    status: '受注済',
    staff: '田中 健二',
    ...overrides,
  }
}

describe('fmtCurrency', () => {
  it('formats millions with one decimal and M suffix', () => {
    expect(fmtCurrency(1_000_000)).toBe('¥1.0M')
    expect(fmtCurrency(4_800_000)).toBe('¥4.8M')
  })

  it('formats thousands with no decimals and K suffix', () => {
    expect(fmtCurrency(1_000)).toBe('¥1K')
    expect(fmtCurrency(48_200)).toBe('¥48K')
  })

  it('formats values below 1000 as raw yen', () => {
    expect(fmtCurrency(0)).toBe('¥0')
    expect(fmtCurrency(999)).toBe('¥999')
  })

  it('uses the boundary branch at exactly 1000 and 1000000', () => {
    expect(fmtCurrency(999_999)).toBe('¥1000K')
    expect(fmtCurrency(1_000_000)).toBe('¥1.0M')
  })
})

describe('validateForm', () => {
  it('returns no errors for a fully valid form', () => {
    expect(validateForm(makeValidValues())).toEqual({})
  })

  it('requires clientName, contact, product, orderDate, status and staff', () => {
    const errors = validateForm(EMPTY_FORM_VALUES)
    expect(errors.clientName).toBe('この項目は必須入力です')
    expect(errors.contact).toBe('この項目は必須入力です')
    expect(errors.product).toBe('この項目は必須入力です')
    expect(errors.qty).toBe('この項目は必須入力です')
    expect(errors.unitPrice).toBe('この項目は必須入力です')
    expect(errors.orderDate).toBe('この項目は必須入力です')
    expect(errors.status).toBe('ステータスを選択してください')
    expect(errors.staff).toBe('担当者を選択してください')
  })

  it('treats whitespace-only required text fields as empty', () => {
    const errors = validateForm(makeValidValues({ clientName: '   ', contact: '  ', product: ' ' }))
    expect(errors.clientName).toBe('この項目は必須入力です')
    expect(errors.contact).toBe('この項目は必須入力です')
    expect(errors.product).toBe('この項目は必須入力です')
  })

  it('flags an invalid email format but allows an empty email', () => {
    expect(validateForm(makeValidValues({ email: 'not-an-email' })).email).toBe(
      'メールアドレスの形式が正しくありません',
    )
    expect(validateForm(makeValidValues({ email: 'ok@example.com' })).email).toBeUndefined()
    expect(validateForm(makeValidValues({ email: '' })).email).toBeUndefined()
  })

  it('rejects a quantity below 1', () => {
    expect(validateForm(makeValidValues({ qty: '0' })).qty).toBe('1以上の値を入力してください')
    expect(validateForm(makeValidValues({ qty: '1' })).qty).toBeUndefined()
  })

  it('rejects a negative unit price', () => {
    expect(validateForm(makeValidValues({ unitPrice: '-1' })).unitPrice).toBe(
      '0以上の値を入力してください',
    )
    expect(validateForm(makeValidValues({ unitPrice: '0' })).unitPrice).toBeUndefined()
  })

  it('rejects a delivery date earlier than the order date', () => {
    const errors = validateForm(
      makeValidValues({ orderDate: '2024-07-10', deliveryDate: '2024-07-01' }),
    )
    expect(errors.deliveryDate).toBe('受注日より後の日付を指定してください')
  })

  it('accepts a delivery date on or after the order date', () => {
    expect(
      validateForm(makeValidValues({ orderDate: '2024-07-10', deliveryDate: '2024-07-10' }))
        .deliveryDate,
    ).toBeUndefined()
    expect(
      validateForm(makeValidValues({ orderDate: '2024-07-10', deliveryDate: '2024-07-20' }))
        .deliveryDate,
    ).toBeUndefined()
  })
})
