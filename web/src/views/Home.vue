<template>
  <div class="home">
    <button @click="login">Login</button>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import HelloWorld from '@/components/HelloWorld.vue'
import GraphClient from '@/graph-client'

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
          .then(me => {
            console.log(me)
          })
          .catch(error => {
            console.error(error)
          })
      })
      .catch(error => {
        console.error(error)
      })
  }
}
</script>
