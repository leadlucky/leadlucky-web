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
          <p>Your link (click to copy): <a @click="copyLink">{{refLink}}</a></p>
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
        refLink: ""
      }
    },
    created(){
      this.premium = store.userData.user.premiumStatus === 'active'
      this.refLink = window.location.origin+'/#/referral/'+store.userData.user.username
    },
    methods: {
      saveTheme(){
        localStorage.setItem("darkTheme", store.portalData.darkTheme)
      },
      premiumRedirect(){
        this.$router.history.push('/dashboard/upgrade')
      },
      copyLink(){
        // Create a dummy input to copy the string array inside it
        var dummy = document.createElement("input");
        // Add it to the document
        document.body.appendChild(dummy);
        // Set its ID
        dummy.setAttribute("id", "dummy_id");
        // Output the array into it
        document.getElementById("dummy_id").value=this.refLink;
        // Select it
        dummy.select();
        // Copy its contents
        document.execCommand("copy");
        // Remove it as its not needed anymore
        document.body.removeChild(dummy);
      }
    }
  }
</script>
