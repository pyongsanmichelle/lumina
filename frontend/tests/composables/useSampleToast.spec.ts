import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { defineComponent, h, provide, inject } from 'vue'
import { mount } from '@vue/test-utils'

// `useSampleToastProvider`/`Inject` rely on Nuxt auto-imported `provide`/`inject`.
// Expose them globally so the composable behaves as it does under Nuxt.
;(globalThis as unknown as { provide: typeof provide }).provide = provide
;(globalThis as unknown as { inject: typeof inject }).inject = inject
import {
  useSampleToast,
  useSampleToastProvider,
  useSampleToastInject,
} from '~/composables/useSampleToast'

describe('useSampleToast', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.restoreAllMocks()
    vi.useRealTimers()
  })

  it('starts with an empty list of toasts', () => {
    const { toasts } = useSampleToast()
    expect(toasts.value).toEqual([])
  })

  it('adds a toast with a generated id', () => {
    const { toasts, addToast } = useSampleToast()
    addToast({ type: 'success', title: '保存しました' })
    expect(toasts.value).toHaveLength(1)
    expect(toasts.value[0]).toMatchObject({ type: 'success', title: '保存しました' })
    expect(typeof toasts.value[0]!.id).toBe('string')
    expect(toasts.value[0]!.id.length).toBeGreaterThan(0)
  })

  it('auto-removes a toast after the timeout elapses', () => {
    const { toasts, addToast } = useSampleToast()
    addToast({ type: 'warning', title: '警告' })
    expect(toasts.value).toHaveLength(1)
    vi.advanceTimersByTime(4500)
    expect(toasts.value).toHaveLength(0)
  })

  it('keeps at most 5 toasts, dropping the oldest', () => {
    const { toasts, addToast } = useSampleToast()
    for (let i = 0; i < 6; i++) {
      addToast({ type: 'success', title: `toast-${i}` })
    }
    expect(toasts.value).toHaveLength(5)
    expect(toasts.value[0]!.title).toBe('toast-1')
    expect(toasts.value[4]!.title).toBe('toast-5')
  })

  it('removes a specific toast by id and clears its timer', () => {
    const clearSpy = vi.spyOn(globalThis, 'clearTimeout')
    const { toasts, addToast, removeToast } = useSampleToast()
    addToast({ type: 'error', title: 'エラー' })
    const id = toasts.value[0]!.id
    removeToast(id)
    expect(toasts.value).toHaveLength(0)
    expect(clearSpy).toHaveBeenCalled()
  })
})

describe('useSampleToast provide/inject', () => {
  it('injects the provided toast state into descendants', () => {
    let injected: ReturnType<typeof useSampleToast> | undefined

    const Child = defineComponent({
      setup() {
        injected = useSampleToastInject()
        return () => h('div')
      },
    })

    const Parent = defineComponent({
      setup() {
        const state = useSampleToastProvider()
        state.addToast({ type: 'success', title: 'from-provider' })
        return () => h(Child)
      },
    })

    mount(Parent)
    expect(injected).toBeDefined()
    expect(injected!.toasts.value[0]!.title).toBe('from-provider')
  })

  it('throws when injected without a provider', () => {
    const Orphan = defineComponent({
      setup() {
        useSampleToastInject()
        return () => h('div')
      },
    })

    expect(() => mount(Orphan)).toThrow(/useSampleToastProvider/)
  })
})
