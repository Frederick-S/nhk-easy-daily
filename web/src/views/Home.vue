<template>
  <div class="home">
    <b-button type="is-primary" icon-left="microsoft" @click="login">Login with Microsoft</b-button>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import HelloWorld from '@/components/HelloWorld.vue'
import GraphClient from '@/graph-client'
import * as Sentry from '@sentry/browser'

@Component({
  components: {
    HelloWorld,
  },
})
export default class Home extends Vue {
  public login() {
    const graphClient = new GraphClient()

    graphClient.login()
      .then(() => {
        graphClient.getMe()
          .then((me) => {
          })
          .catch((error) => {
            Sentry.captureException(error)
          })
      })
      .catch((error) => {
        Sentry.captureException(error)
      })
  }
}
</script>
