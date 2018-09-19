<template>
  <div>
    <div class="row page-titles" style="position: fixed; width: 100%;">
      <div class="col-md-12s align-self-center">
        <h3 class="text-primary">Dashboard</h3>
      </div>
    </div>
    <div class="container-fluid">
      <div class="row" style="height: 56px">
        <!-- Spacer -->
      </div>
      <div class="row tallboi">
        <div class="col-md-9" style="padding-bottom: 30px">
          <div class="card tallboi">
            <div class="card-title">
              <h4>Pages</h4>
            </div>
            <div class="card-body">
              <ela-table
                :headers="headers"
                :items="tableData"
                :onSelect="selectTheme"
              />
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="row" v-for="items in infoCards">
            <div :class="`col col-md-${12 / items.length}`" v-for="item in items">
              <info-card
                :name="item.name" :value="item.value"
                :icon="item.icon" :color="item.color"/>
            </div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-body row">
          <div class="col col-md-9">
            <h4 class="card-title">Page Viewer Devices</h4>
            <area-chart
              :data="chartData"
              xkey="period"
              :ykeys="['smartphone', 'windows', 'mac']"
              :lineColors="['#62d1f3','#fc6180','#ffb64d']"
              :labels="['iPhone', 'Android', 'Web']"
            />
          </div>
          <div class="col col-md-3 ">
            <p class="m-t-30 f-w-600">iPhone<span class="pull-right">65%</span></p>
            <div class="progress">
              <div role="progressbar" style="width: 65%; height:8px;"
                   class="progress-bar bg-info wow animated progress-animated"><span
                class="sr-only">60% Complete</span></div>
            </div>
            <p class="m-t-30 f-w-600">Android<span class="pull-right">65%</span></p>
            <div class="progress">
              <div role="progressbar" style="width: 65%; height:8px;"
                   class="progress-bar bg-danger wow animated progress-animated"><span
                class="sr-only">60% Complete</span></div>
            </div>
            <p class="m-t-30 f-w-600">Web<span class="pull-right">65%</span></p>
            <div class="progress m-b-30">
              <div role="progressbar" style="width: 65%; height:8px;"
                   class="progress-bar bg-warning wow animated progress-animated"><span
                class="sr-only">60% Complete</span></div>
            </div>
            <button class="btn btn-info btn-block" @click="genData()">
              Update Data
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from 'axios'
  import auth from '../auth'
  import store from '../store'
  import ElaTable from './ela/ElaTable'
  import InfoCard from './ela/InfoCard'
  import AreaChart from './ela/AreaChart'

  export default {
    components: {ElaTable, AreaChart, InfoCard},
    created() {
      this.fetchData()
      this.genData()
    },
    mounted(){
      setTimeout(() => this.genData(), 500)
    },
    data() {
      return {
        search: '',
        defaultUrl: '/#',
        premium: false,
        pages: [],
        chartData: []
      }
    },
    computed: {
      headers: () => [
        {
          text: 'Name',
          value: 'name'
        },
        {text: 'URL', value: 'URL',},
        {text: 'Email Count', value: 'emails'},
        ...(store.userData.isPremium() ? [
          {text: 'Conversion Rate', value: 'conversions', align: 'center'},
          {text: 'Impressions', value: 'impressions', align: 'center'}
        ] : [])
      ],
      tableData: function () {
        return this.pages.map((page) => ({
          name: page.name,
          URL: `/${page.themeName}/${page.name}`,
          conversions: `${(page.emailCount / page.viewCount).toFixed(3) * 100}%`,
          impressions: page.viewCount,
          emails: page.emailCount
        }))
      },
      infoCards: function () {
        return [
          [{name: 'Views', icon: 'user', color: 'info', value: 10000}],
          [{name: 'Emails', icon: 'envelope', color: 'danger', value: 7560}],
          [{name: 'Conversion Rate', icon: 'percent', color: 'warning', value: '75.6%'}]
        ]
      }
    },
    methods: {
      fetchData() {
        axios.get(
          window.leadlucky.apiUrl + 'pages',
          {headers: auth.getAuthHeader()}
        ).then((resp) => {
          this.pages = resp.data;
        }).catch((err) => {
          console.log(err)
        })
      },
      genData(){
        this.chartData = [{
          period: '2001',
          smartphone: 0,
          windows: 0,
          mac: 0
        }, ...['2002','2003','2005','2010','2018'].map((year) => ({
          period: year,
          smartphone: Math.floor((Math.random() * 100)),
          windows: Math.floor((Math.random() * 100)),
          mac: Math.floor((Math.random() * 100))
        }))
        ]
      },
      selectTheme(tableRow) {
        this.$router.push(`/dashboard/analytics/${tableRow.name}`)
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
      }
    }
  }
</script>
