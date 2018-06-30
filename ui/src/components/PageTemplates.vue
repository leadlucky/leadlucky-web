<template>
  <v-container fluid grid-list-md>
    <v-layout row wrap>
      <v-flex xs12 sm4 v-for="themeName in themeNames" :key="`template-${themeName}`">
        <v-card>
          <v-card-media
            :src="themes[themeName].imageUrl"
            height="200px"
          >
          </v-card-media>
          <v-card-title primary-title>
            <div>
              <div class="headline text-xs-left">{{themes[themeName].name}}</div>
              <span class="grey--text">{{themes[themeName].description}}</span>
            </div>
          </v-card-title>
          <v-card-actions>
            <v-btn @click="$router.history.push(`/dashboard/edit/${themeName}`)" flat>Use</v-btn>
            <v-btn flat :to="`/${themeName}`" color="blue">Preview</v-btn>
            <v-spacer></v-spacer>
            <v-btn icon @click.native="show = !show">
              <v-icon>{{ show ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }}</v-icon>
            </v-btn>
          </v-card-actions>
          <v-slide-y-transition>
            <v-card-text v-show="show">
              Free Theme.
            </v-card-text>
          </v-slide-y-transition>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import axios from 'axios'
  import auth from '../auth'
  import themes from '@leadlucky/leadlucky-themes'

  export default {
    data: () => ({
      show: false,
      show2: false,
      show3: false,
      show4: false,
      show5: false,
      premium: false
    }),
    created(){
      this.fetchData()
    },
    methods: {
      fetchData() {
        axios.get(window.leadlucky.apiUrl+'users/me', {headers: {"Authorization": "Bearer " + localStorage.getItem('access_token')}
        }).then((resp) => {
          this.user = JSON.parse(JSON.stringify(resp.data))

          if(resp.data.premiumStatus === "active"){
            this.premium = true
          }

        })
          .catch((err) => {
            console.log(err)
            this.premium = false
          })
      }
    },
    computed: {
      themeNames: () => Object.keys(themes),
      themes: () => themes
    }
  }
</script>

<style>
  .smallspace{
    padding: 10px;
  }
</style>
