<template>
  <div>
    <v-container p1>
      <v-card>
        <v-card-title>
          <h3>Account Settings</h3>
        </v-card-title>
        <v-card-text>
          <v-text-field
            label="username"
            :value="userData.user.username"
            disabled/>
          <v-text-field
            label="Email Address"
            :value="userData.user.email"
            disabled/>
          <v-switch
            label="Premium Status"
            v-model="premium"
            @change="premiumRedirect()"
            :disabled="premium"/>
        </v-card-text>
      </v-card>
      <br/>
      <v-card>
        <v-card-title>
          <h3>Portal Settings</h3>
        </v-card-title>
        <v-card-text>
          <v-switch
            label="Dark Theme"
            v-model="portalData.darkTheme"
            @change="saveTheme"/>
        </v-card-text>
      </v-card>
      <br/>
      <v-card>
        <v-card-title>
          <h3>Referral Settings</h3>
        </v-card-title>
        <v-card-text>
          <h1>Balance: ${{userData.user.balance}}</h1>
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>

<script>
  import store from '../store'

  export default {
    data() {
      return {
        userData: store.userData,
        portalData: store.portalData,
        premium: false,
      }
    },
    created(){
      this.premium = store.userData.user.premiumStatus === 'active'
    },
    methods: {
      saveTheme(){
        localStorage.setItem("darkTheme", store.portalData.darkTheme)
      },
      premiumRedirect(){
        this.$router.history.push('/dashboard/upgrade')
      }
    }
  }
</script>
