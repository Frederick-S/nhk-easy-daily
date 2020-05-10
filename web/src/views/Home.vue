<template>
  <div class="container">
    <b-navbar>
      <template slot="end">
        <b-navbar-item>
          <a href="https://github.com/Frederick-S/nhk-easy-daily">
            <b-icon icon="github"></b-icon>
          </a>
        </b-navbar-item>
        <b-navbar-item v-if="user.name">Welcome, {{ user.name }}</b-navbar-item>
      </template>
    </b-navbar>
    <div v-if="user.name" class="columns is-centered">
      <section class="column is-one-quarter">
        <b-field>
          <b-input placeholder="Send-to-Kindle e-mail address" icon="email"></b-input>
        </b-field>
        <b-field>
          <b-input placeholder="Evernote email address" icon="email"></b-input>
        </b-field>
        <b-field>
          <b-button type="is-primary">Subscribe</b-button>
        </b-field>
      </section>
    </div>
    <b-button v-else type="is-primary" icon-left="microsoft" @click="login">Login with Microsoft</b-button>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import HelloWorld from '@/components/HelloWorld.vue'
import GraphClient from '@/graph-client'
import * as Sentry from '@sentry/browser'
import MicrosoftUser from '@/data/microsoft-user'
import User from '@/data/user'

@Component({
  components: {
    HelloWorld,
  },
})
export default class Home extends Vue {
  private user = new User()

  public login() {
    const graphClient = new GraphClient()

    graphClient.login()
      .then(() => {
        graphClient.getMe()
          .then((me) => {
            const user = new User()
            user.name = me.displayName
            user.mail = me.mail

            this.user = user
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
