<template>
  <v-app :dark="portalData.darkTheme" class="leadlucky-app" v-if="userData.user">
    <SideNav :visible="drawer && !portalData.fullscreen"></SideNav>
    <v-toolbar v-if="!portalData.fullscreen" app fixed clipped-left>
      <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-toolbar-title>LeadLucky</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn @click="logout" round color="blue darken-3" class="white--text" to="/">
        Logout
        <v-icon right dark>account_circle</v-icon>
      </v-btn>
    </v-toolbar>
    <v-content>
      <router-view></router-view>
    </v-content>
  </v-app>
</template>


<script>
  import alertmessage from './alertmessage'
  import PageTable from './PageTable'
  import PageTemplates from './PageTemplates'
  import auth from '../auth'
  import EditPage from './EditPage'
  import PageAccount from './PageAccount'
  import PageUpgrade from './PageUpgrade'
  import axios from 'axios'
  import SideNav from './SideNav';
  import store from '../store';

  export default {
    name: 'app',
    components: {
      SideNav, alertmessage, PageTable, PageTemplates, EditPage, PageAccount, PageUpgrade
    },
    data: () => ({
      drawer: true,
      userData: store.userData,
      portalData: store.portalData
    }),
    created() {
      const router = this.$router;
      auth.refreshAuth((user) => {
        if(!user){
          router.history.push('/')
        }
      });

    },
    methods: {
      logout() {
        auth.logout()
      }
    }
  }
</script>

<style>
  .leadlucky-app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  /*margin-top: 60px;*/
  }
</style>
