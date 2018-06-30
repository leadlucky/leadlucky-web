<template>
  <div>
    <v-alert type="info" dismissible v-model="alert">
      We are currently in beta, if you find a bug please <a href="mailto:support@leadlucky.com" style="color: rgb(203,236,255)">let us know</a>(:
    </v-alert>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        alert: true
      }
    }
  }
</script>
