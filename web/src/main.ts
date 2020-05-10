import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import * as Sentry from '@sentry/browser'
import { Vue as VueIntegration } from '@sentry/integrations'

Vue.config.productionTip = false

Sentry.init({
  dsn: 'https://275b51467f11450089593d9b53f5ac21@o286444.ingest.sentry.io/5232986',
  integrations: [new VueIntegration({
    Vue,
    attachProps: true,
    logErrors: true
  })]
})

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app')
