import { describe, it, expect } from 'vitest'
import { useCounter } from '~/composables/useCounter'

describe('useCounter', () => {
  it('defaults the count to 0', () => {
    const { count } = useCounter()
    expect(count.value).toBe(0)
  })

  it('uses the provided initial value', () => {
    const { count } = useCounter(5)
    expect(count.value).toBe(5)
  })

  it('increments the count', () => {
    const { count, increment } = useCounter()
    increment()
    increment()
    expect(count.value).toBe(2)
  })

  it('decrements the count', () => {
    const { count, decrement } = useCounter(3)
    decrement()
    expect(count.value).toBe(2)
  })

  it('keeps independent state per instance', () => {
    const a = useCounter(0)
    const b = useCounter(0)
    a.increment()
    expect(a.count.value).toBe(1)
    expect(b.count.value).toBe(0)
  })
})
