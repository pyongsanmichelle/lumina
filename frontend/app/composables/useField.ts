import { computed, ref, type StyleValue } from 'vue'

/**
 * フォームフィールドで共通利用するラベル用の一意な id を生成する。
 */
export function useFieldId(label: string): string {
  return `field-${label}-${Math.random().toString(36).slice(2, 8)}`
}

/**
 * フォームフィールド共通のフォーカス状態と入力要素スタイルを提供する。
 *
 * @param getError エラーメッセージを返すゲッター（リアクティブに評価される）
 * @param extraStyle 入力要素ごとに追加したいスタイル（例: textarea の lineHeight）
 */
export function useFieldState(
  getError: () => string | undefined,
  extraStyle: Record<string, unknown> = {},
) {
  const focused = ref(false)

  const inputStyle = computed<StyleValue>(() => {
    const hasError = !!getError()
    return {
      borderColor: hasError ? '#ef4444' : focused.value ? '#2563eb' : '#e2e8f0',
      color: '#0f172a',
      background: 'white',
      boxShadow: hasError
        ? '0 0 0 3px rgba(239,68,68,0.12)'
        : focused.value
          ? '0 0 0 3px rgba(37,99,235,0.1)'
          : undefined,
      ...extraStyle,
    }
  })

  function onFocus() {
    focused.value = true
  }

  function onBlur() {
    focused.value = false
  }

  return { focused, inputStyle, onFocus, onBlur }
}
