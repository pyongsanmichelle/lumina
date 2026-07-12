<template>
  <svg
    :width="width"
    :height="height"
    :viewBox="`0 0 ${width} ${height}`"
    xmlns="http://www.w3.org/2000/svg"
  >
    <!-- Grid lines -->
    <line
      v-for="(y, i) in gridYs"
      :key="'grid-' + i"
      :x1="0"
      :y1="y"
      :x2="width"
      :y2="y"
      stroke="#f1f5f9"
      stroke-dasharray="3 3"
    />
    <!-- Target line (dashed) -->
    <polyline
      :points="targetPoints"
      fill="none"
      stroke="#e2e8f0"
      stroke-width="2"
    />
    <!-- Area fill -->
    <path
      :d="areaPath"
      fill="url(#salesGrad)"
    />
    <!-- Sales line -->
    <polyline
      :points="salesPoints"
      fill="none"
      stroke="#2563eb"
      stroke-width="2"
    />
    <!-- Dots -->
    <circle
      v-for="(pt, i) in salesCoords"
      :key="'dot-' + i"
      :cx="pt.x"
      :cy="pt.y"
      r="3"
      fill="#2563eb"
    />
    <!-- X-axis labels -->
    <text
      v-for="(d, i) in data"
      :key="'xlabel-' + i"
      :x="xPos(i)"
      :y="height + 14"
      text-anchor="middle"
      font-size="11"
      fill="#94a3b8"
    >
      {{ d.month }}
    </text>
    <!-- Y-axis labels -->
    <text
      v-for="(label, i) in yLabels"
      :key="'ylabel-' + i"
      x="-6"
      :y="yPos(label.value) + 4"
      text-anchor="end"
      font-size="10"
      fill="#94a3b8"
    >
      {{ label.text }}
    </text>
    <defs>
      <linearGradient id="salesGrad" x1="0" y1="0" x2="0" y2="1">
        <stop offset="5%" stop-color="#2563eb" stop-opacity="0.15" />
        <stop offset="95%" stop-color="#2563eb" stop-opacity="0" />
      </linearGradient>
    </defs>
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { fmtCurrency } from '~/types/sample'

interface SalesPoint {
  month: string
  売上: number
  目標: number
}

interface Props {
  data: SalesPoint[]
  width?: number
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  width: 600,
  height: 200,
})

const padding = { top: 10, right: 10, bottom: 30, left: 50 }
const innerW = computed(() => props.width - padding.left - padding.right)
const innerH = computed(() => props.height - padding.top - padding.bottom)

const maxVal = computed(() => {
  let m = 0
  for (const d of props.data) {
    if (d.売上 > m) m = d.売上
    if (d.目標 > m) m = d.目標
  }
  return m * 1.1
})

function xPos(i: number): number {
  const len = props.data.length
  return padding.left + (len > 1 ? (i / (len - 1)) * innerW.value : innerW.value / 2)
}

function yPos(v: number): number {
  return padding.top + innerH.value - (v / maxVal.value) * innerH.value
}

interface Coord {
  x: number
  y: number
}

const salesCoords = computed<Coord[]>(() => {
  return props.data.map((d, i) => ({
    x: xPos(i),
    y: yPos(d.売上),
  }))
})

const targetCoords = computed<Coord[]>(() => {
  return props.data.map((d, i) => ({
    x: xPos(i),
    y: yPos(d.目標),
  }))
})

const salesPoints = computed(() =>
  salesCoords.value.map((p) => `${p.x},${p.y}`).join(' ')
)

const targetPoints = computed(() =>
  targetCoords.value.map((p) => `${p.x},${p.y}`).join(' ')
)

const areaPath = computed(() => {
  const coords = salesCoords.value
  if (coords.length === 0) return ''
  let path = `M ${coords[0].x},${coords[0].y} `
  for (let i = 1; i < coords.length; i++) {
    path += `L ${coords[i].x},${coords[i].y} `
  }
  // Close to bottom for fill
  const last = coords[coords.length - 1]
  path += `L ${last.x},${padding.top + innerH.value} L ${coords[0].x},${padding.top + innerH.value} Z`
  return path
})

const gridYs = computed(() => {
  const lines: number[] = []
  for (let i = 0; i <= 4; i++) {
    lines.push(padding.top + (innerH.value / 4) * i)
  }
  return lines
})

interface YLabel {
  text: string
  value: number
}

const yLabels = computed<YLabel[]>(() => {
  const labels: YLabel[] = []
  for (let i = 0; i <= 4; i++) {
    const v = (maxVal.value / 4) * (4 - i)
    labels.push({ text: fmtCurrency(v), value: v })
  }
  return labels
})
</script>