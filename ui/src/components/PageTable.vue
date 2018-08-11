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
              <div class="card">
                <div class="media">
                  <div class="media-left meida media-middle">
                    <span><i :class="`fa fa-${item.icon} f-s-40 color-${item.color}`"></i></span>
                  </div>
                  <div class="media-body media-text-right">
                    <h2>{{item.value}}</h2>
                    <p class="m-b-0">{{item.name}}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-body row">
          <div class="col col-md-7">
            <h4 class="card-title">Page Viewer Devices</h4>
            <div id="extra-area-chart"></div>
          </div>
          <div class="col col-md-1"/>
          <div class="col col-md-4 ">
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

  export default {
    components: {ElaTable},
    created() {
      this.fetchData()
    },
    mounted() {
      this.initChart()
    },
    data() {
      return {
        search: '',
        defaultUrl: '/#',
        premium: false,
        pages: []
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
          [{name: 'Conversions', icon: 'percent', color: 'warning', value: '75.6%'}]
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
      initChart() {
        window.Morris.Area({
          element: 'extra-area-chart',
          data: [{
            period: '2001',
            smartphone: 0,
            windows: 0,
            mac: 0
          }, {
            period: '2002',
            smartphone: 90,
            windows: 60,
            mac: 25
          }, {
            period: '2003',
            smartphone: 40,
            windows: 80,
            mac: 35
          }, {
            period: '2004',
            smartphone: 30,
            windows: 47,
            mac: 17
          }, {
            period: '2005',
            smartphone: 150,
            windows: 40,
            mac: 120
          }, {
            period: '2006',
            smartphone: 25,
            windows: 80,
            mac: 40
          }, {
            period: '2007',
            smartphone: 10,
            windows: 10,
            mac: 10
          }


          ],
          lineColors: ['#62d1f3', '#fc6180', '#ffb64d'],
          xkey: 'period',
          ykeys: ['smartphone', 'windows', 'mac'],
          labels: ['iPhone', 'Android', 'Web'],
          pointSize: 0,
          lineWidth: 0,
          resize: false,
          fillOpacity: 0.8,
          behaveLikeLine: true,
          gridLineColor: '#e0e0e0',
          hideHover: 'auto'

        });
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
