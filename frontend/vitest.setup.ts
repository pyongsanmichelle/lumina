// Vitest/JSDOM(happy-dom)環境におけるNuxtクライアントフラグのデフォルト設定
globalThis.__IMPORT_META_CLIENT__ = true;
globalThis.__IMPORT_META_SERVER__ = false;

import { config } from '@vue/test-utils';
import SampleSidebar from '~/components/SampleSidebar.vue';
import SampleHeader from '~/components/SampleHeader.vue';
import SampleKpiCard from '~/components/SampleKpiCard.vue';
import SampleAreaChart from '~/components/SampleAreaChart.vue';
import SampleBarChart from '~/components/SampleBarChart.vue';
import SampleNotificationItem from '~/components/SampleNotificationItem.vue';
import SampleSectionTitle from '~/components/SampleSectionTitle.vue';
import SampleReadField from '~/components/SampleReadField.vue';
import SampleAlertBanner from '~/components/SampleAlertBanner.vue';
import SampleTextField from '~/components/SampleTextField.vue';
import SampleSelectField from '~/components/SampleSelectField.vue';
import SampleTextareaField from '~/components/SampleTextareaField.vue';

config.global.components = {
  SampleSidebar,
  SampleHeader,
  SampleKpiCard,
  SampleAreaChart,
  SampleBarChart,
  SampleNotificationItem,
  SampleSectionTitle,
  SampleReadField,
  SampleAlertBanner,
  SampleTextField,
  SampleSelectField,
  SampleTextareaField,
};
