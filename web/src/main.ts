import Vue from 'vue'
import Buefy from 'buefy'
import 'buefy/dist/buefy.css'
import * as Sentry from '@sentry/browser'
import { Vue as VueIntegration } from '@sentry/integrations'
import App from './App.vue'
import router from './router'
import store from './store'

Sentry.init({
  dsn: 'https://275b51467f11450089593d9b53f5ac21@o286444.ingest.sentry.io/5232986',
  integrations: [new VueIntegration({
    Vue,
    attachProps: true,
    logErrors: true,
  })],
})

Vue.config.productionTip = false
Vue.use(Buefy)

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app')
