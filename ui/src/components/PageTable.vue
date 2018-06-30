<template>
  <v-card>
    <v-card-title>
      Landing Pages
      <v-spacer></v-spacer>
      <v-text-field
        append-icon="search"
        label="Search"
        single-line
        hide-details
        v-model="search"
      ></v-text-field>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="tableData"
      :search="search"
    >
      <template slot="items" slot-scope="props">
        <td class="text-xs-left">{{ props.item.name }}</td>
        <td class="text-xs-center"><a :href="defaultUrl+props.item.URL">{{ props.item.URL }}</a></td>
        <td v-if="premium === true" class="text-xs-center">{{ props.item.conversions }}</td>
        <td v-if="premium === true" class="text-xs-center">{{ props.item.impressions }}</td>
        <td class="text-xs-center">{{ props.item.emails }} <a v-if="props.item.emails > 0"
                                                              @click="getEmails(props.item.name)">Download</a></td>
      </template>
      <v-alert slot="no-results" :value="true" color="error" icon="warning">
        Your search for "{{ search }}" found no results.
      </v-alert>
    </v-data-table>
  </v-card>
</template>

<script>
  import axios from 'axios'
  import auth from '../auth'

  export default {
    created() {
      this.fetchData()
      this.fetchPremiumData()
    },
    data() {
      return {
        search: '',
        defaultUrl: '/#',
        premium: false,
        headers: [
          {
            text: 'Name',
            align: 'left',
            value: 'name'
          },
          {text: 'URL', value: 'URL', align: 'center'},
          {text: 'Email Count', value: 'emails', align: 'center'}
        ],
        pages: []
      }
    },
    computed: {
      tableData: function() {
        return this.pages.map((page) => ({
            name: page.name,
            URL: `/${page.themeName}/${page.name}`,
            conversions: `${(page.emailCount / page.viewCount).toFixed(3) * 100}%`,
            impressions: page.viewCount,
            emails: page.emailCount
        }))
      }
    },
    methods: {
      fetchData() {
        axios.get(
          window.leadlucky.apiUrl + 'users/me/pages',
          {headers: auth.getAuthHeader()}
        ).then((resp) => {
          this.pages = resp.data;
        })
        .catch((err) => {
          console.log(err)
        })
      },
      getEmails(unique) {
        axios.get(window.leadlucky.apiUrl + 'users/txt/' + unique, {
          headers: {"Authorization": "Bearer " + localStorage.getItem('access_token')}, responseType: 'blob'
        }).then((resp) => {
          let blob = new Blob([resp.data], {type: 'text/plain'})
          let link = document.createElement('a')
          link.href = window.URL.createObjectURL(blob)
          link.download = unique + '.txt'
          link.click()
        })
          .catch((err) => {
            console.log(err)
          })
      },
      fetchPremiumData() {
        axios.get(window.leadlucky.apiUrl + 'users/me', {
          headers: {"Authorization": "Bearer " + localStorage.getItem('access_token')}
        }).then((resp) => {
          this.user = JSON.parse(JSON.stringify(resp.data))

          if (resp.data.premiumStatus === "active") {
            this.premium = true

            this.headers = [
              {
                text: 'Name',
                align: 'left',
                value: 'name'
              },
              {text: 'URL', value: 'URL', align: 'center'},
              {text: 'Conversion Rate', value: 'conversions', align: 'center'},
              {text: 'Impressions', value: 'impressions', align: 'center'},
              {text: 'Email Count', value: 'emails', align: 'center'}
            ]
          }

        })
          .catch((err) => {
            console.log(err)
            this.premium = false
          })
      }
    }
  }
</script>
