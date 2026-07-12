import { config } from '@vue/test-utils'
import SampleSidebar from './app/components/sample/SampleSidebar.vue'
import SampleHeader from './app/components/sample/SampleHeader.vue'
import SampleKpiCard from './app/components/sample/SampleKpiCard.vue'
import SampleAreaChart from './app/components/sample/chart/SampleAreaChart.vue'
import SampleBarChart from './app/components/sample/chart/SampleBarChart.vue'
import SampleNotificationItem from './app/components/sample/SampleNotificationItem.vue'
import SampleSectionTitle from './app/components/sample/SampleSectionTitle.vue'
import SampleReadField from './app/components/sample/SampleReadField.vue'
import SampleAlertBanner from './app/components/sample/SampleAlertBanner.vue'
import SampleTextField from './app/components/sample/form/SampleTextField.vue'
import SampleSelectField from './app/components/sample/form/SampleSelectField.vue'
import SampleTextareaField from './app/components/sample/form/SampleTextareaField.vue'

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
}
