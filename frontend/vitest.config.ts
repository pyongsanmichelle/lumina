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
        'app/components/sample/SampleAlertBanner.vue',
        'app/components/sample/SampleHeader.vue',
        'app/components/sample/SampleKpiCard.vue',
        'app/components/sample/SampleNotificationItem.vue',
        'app/components/sample/SampleReadField.vue',
        'app/components/sample/SampleSectionTitle.vue',
        'app/components/sample/SampleSidebar.vue',
        'app/components/sample/form/SampleTextField.vue',
        'app/components/sample/form/SampleSelectField.vue',
        'app/components/sample/form/SampleTextareaField.vue',
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
    include: ['app/**/*.spec.ts'],
  },
})
