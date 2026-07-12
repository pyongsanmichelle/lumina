import { ref, readonly } from 'vue'
import type { ToastData } from '~/types/sample'

const TOAST_SYMBOL = Symbol('use-sample-toast')

export function useSampleToast() {
  const toasts = ref<ToastData[]>([])
  const timers = ref<Record<string, ReturnType<typeof setTimeout>>>({})

  function removeToast(id: string) {
    clearTimeout(timers.value[id])
    delete timers.value[id]
    toasts.value = toasts.value.filter((t: ToastData) => t.id !== id)
  }

  function addToast(toast: Omit<ToastData, 'id'>) {
    const id = crypto.randomUUID()
    toasts.value = [...toasts.value.slice(-4), { ...toast, id }]
    timers.value[id] = setTimeout(() => removeToast(id), 4500)
  }

  return {
    toasts: readonly(toasts),
    addToast,
    removeToast,
  }
}

export function useSampleToastProvider() {
  const toastState = useSampleToast()
  provide(TOAST_SYMBOL, toastState)
  return toastState
}

export function useSampleToastInject(): ReturnType<typeof useSampleToast> {
  const state = inject<ReturnType<typeof useSampleToast>>(TOAST_SYMBOL)
  if (!state) {
    throw new Error('useSampleToastInject must be used within a component that calls useSampleToastProvider')
  }
  return state
}
