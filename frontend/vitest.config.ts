import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'node:path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '~': resolve(__dirname, './app'),
    },
  },
  test: {
    environment: 'happy-dom',
    setupFiles: ['./vitest.setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      reportsDirectory: './coverage',
      reportOnFailure: true,
      all: true,
      include: [
        'app/components/SampleAlertBanner.vue',
        'app/components/SampleHeader.vue',
        'app/components/SampleKpiCard.vue',
        'app/components/SampleNotificationItem.vue',
        'app/components/SampleReadField.vue',
        'app/components/SampleSectionTitle.vue',
        'app/components/SampleSidebar.vue',
        'app/components/SampleToast.vue',
        'app/components/SampleTextField.vue',
        'app/components/SampleSelectField.vue',
        'app/components/SampleTextareaField.vue',
        'app/components/SampleAreaChart.vue',
        'app/components/SampleBarChart.vue',
        'app/pages/sample-design.vue',
      ],
      exclude: [],
      thresholds: {
        lines: 80,
        functions: 80,
        branches: 80,
        statements: 80,
      },
      ignoreEmptyLines: true,
    },
    include: ['tests/**/*.{test,spec}.ts'],
  },
})
