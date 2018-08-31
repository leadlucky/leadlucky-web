<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-sm-4" v-for="themeName in themeNames" :key="`template-${themeName}`">
        <div class="card">
          <div class="theme-preview-bg" :style="`background-image: url('${themes[themeName].imageUrl}')`"/>
          <div class="theme-preview" :style="`background-image: url('${themes[themeName].imageUrl}')`"/>
          <div class="card-body">
            <h3>{{themes[themeName].name}}</h3>
            <h5>{{themes[themeName].description}}</h5>
            <div class="btn-group">
              <button class="btn btn-info p-l-20 p-r-20" @click="$router.history.push(`/dashboard/edit/${themeName}`)">
                Use
              </button>
              <button class="btn btn-info p-l-20 p-r-20" @click="$router.history.push(`/${themeName}`)">
                Preview
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
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
    created() {
      this.fetchData()
    },
    methods: {
      fetchData() {
        axios.get(window.leadlucky.apiUrl + 'users/me', {
          headers: {"Authorization": "Bearer " + localStorage.getItem('access_token')}
        }).then((resp) => {
          this.user = JSON.parse(JSON.stringify(resp.data))

          if (resp.data.premiumStatus === "active") {
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


  .theme-preview {
    background: no-repeat center;
    background-size: contain;
    height: 300px;
    z-index: 10;
    margin-top: -300px;;
    float: right;
    margin-bottom: 10px;
  }

  .theme-preview-bg {
    background: repeat center;
    background-size: contain;
    height: 300px;
    float: left;
    filter: brightness(80%) blur(2px)
  }

</style>
