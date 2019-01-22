<template>
  <v-container v-if="pageData.value" text-xs-left>
    <v-layout row>
      <v-flex xs12>
        <h1 id="page-title">Analytics - {{pageName}}</h1>
      </v-flex>
    </v-layout>
    <v-divider/>
    <br/>
    <v-layout row text-xs-center>
      <v-flex xs3 offset-xs1>
        <h2 class="data-title">Page Views:</h2>
        <br/>
        <p class="big-number">{{pageData.value.viewCount}}</p>
      </v-flex>
      <v-flex xs3>
        <h2 class="data-title">Emails Collected:</h2>
        <br/>
        <p class="big-number">{{pageData.value.emailCount}}</p>
      </v-flex>
      <v-flex xs3>
        <h2 class="data-title">Conversion Rate:</h2>
        <br/>
        <v-progress-circular
          :rotate="270"
          :size="160"
          :width="15"
          :value="Number(conversionRate)"
          :color="conversionColor"
        >
          <span style="color: black">{{ conversionRate }}%</span>
        </v-progress-circular>
      </v-flex>
    </v-layout>
    <v-card>
      <v-card-title>
        <v-layout row>
          <v-flex sm3>
            <v-select
              @input="modeChanged"
              v-model="mode"
              :items="['Month', 'Day']"
              label="Report Length"
            ></v-select>
          </v-flex>
          <v-spacer></v-spacer>
          <v-flex sm1>
            <v-select
              @input="fetchChartData"
              v-model="selectedDate.year"
              :items="years"
              label="Year"
            ></v-select>
          </v-flex>
          <v-divider class="mx-3" inset vertical/>
          <v-flex sm1 v-if="mode !== 'Year'">
            <v-select
              @input="fetchChartData"
              v-model="selectedDate.month"
              :items="months"
              label="Month"
            ></v-select>
          </v-flex>
          <v-divider class="mx-3" inset vertical/>
          <v-flex sm1 v-if="mode === 'Day'">
            <v-select
              @input="fetchChartData"
              v-model="selectedDate.day"
              :items="days"
              label="Day"
            ></v-select>
          </v-flex>
        </v-layout>
      </v-card-title>
      <v-card-media>
        <v-flex xs12 v-if="chartData.value">
          <g-chart :options="chartOptions" type="LineChart" :data="chartInfo"></g-chart>
        </v-flex>
        <v-flex text-xs-center v-else-if="chartData.loading">
          <v-progress-circular
            indeterminate
            color="primary"></v-progress-circular>
        </v-flex>
        <v-flex xs-4 offset-xs4 v-else-if="chartData.error">
          <v-alert :value="true" color="error" icon="warning" outline>
            {{chartData.error.message}}
          </v-alert>
        </v-flex>
      </v-card-media>
      <br/>
    </v-card>

  </v-container>
  <v-container fluid fill-height v-else-if="pageData.loading">
    <v-layout justify-center align-center>
      <v-flex text-xs-center>
        <v-progress-circular
          indeterminate
          color="primary"></v-progress-circular>
      </v-flex>
    </v-layout>
  </v-container>

  <v-container fluid fill-height v-else-if="pageData.error" style="color: red">
    <v-layout justify-center align-center>
      <v-alert :value="true" color="error" icon="warning" outline>
        {{pageData.error.message}}
      </v-alert>
    </v-layout>
  </v-container>
</template>
<script>
  import auth from '../auth';
  import axios from 'axios';
  import {GChart} from 'vue-google-charts'

  function daysInMonth(month, year) {
    return new Date(year, month, 0).getDate();
  }

  function pad(n, width, z) {
    z = z || '0';
    n = n + '';
    return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
  }

  export default {
    name: 'analytics',
    components: {
      GChart
    },
    created() {
      this.fetchData()
      this.fetchChartData()
    },
    data: () => ({
      mode: "Month",
      selectedDate: {
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        day: null
      },
      pageData: {
        loading: false,
        error: null,
        value: null,
      },
      chartData: {
        loading: false,
        error: null,
        value: null
      },
      chartOptions: {
        chart: {
          width: '100%',
          height: '1400px',
          hAxis: {
            title: 'Date'
          },
          vAxis: {
            title: 'Page Data'
          }
        },
      }
    }),
    computed: {
      years: () => Array.from({length: 2020 - 2017}, (x, i) => 2018 + i).reverse(),
      months: () => Array.from({length: 12}, (x, i) => i + 1),
      days() {
        return Array.from({length: daysInMonth(this.selectedDate.month, this.selectedDate.year)}, (x, i) => i + 1)
      },
      conversionRate() {
        if (this.pageData.value) {
          const {viewCount, emailCount} = this.pageData.value;
          return ((emailCount / viewCount) * 100).toFixed(2)
        }
        return null;
      },
      conversionColor() {
        const cr = Number(this.conversionRate || 0);
        if (cr < 10) return 'red';
        if (cr < 40) return 'yellow';
        return 'green';
      },
      pageName() {
        return this.$route.params.pageName
      },
      chartInfo() {
        const dayMode = this.mode === 'Day';
        let {year, month, day} = this.selectedDate;
        const headers = [
          "Timestamp",
          "Views",
          "Emails"
        ];
        const rows = [...new Array(dayMode ? 24 : daysInMonth(month, year)).keys()].map(u => {
          let ts = `${year}-${pad(month, 2)}-${pad(dayMode ? day : u + 1, 2)}`;
          if (dayMode) ts += ` ${pad(u, 2)}:00`
          return [
            dayMode ? `${pad(u, 2)}:00` : `${month}/${u + 1}`,
            (this.chartData.value[ts] || {}).views || 0,
            (this.chartData.value[ts] || {}).emails || 0
          ]
        });


        return [
          [...headers],
          ...rows
        ]
      }
    },
    methods: {
      modeChanged(){
        console.log('suh')
        if(this.mode === 'Day'){
          this.selectedDate.day = new Date().getDate()
        }
        this.fetchChartData()
      },
      fetchData() {
        const self = this;

        self.pageData.error = null;
        self.pageData.loading = true;

        axios.get(window.leadlucky.apiUrl + `pages/${self.pageName}`, {headers: auth.getAuthHeader()}).then(res => {
          self.pageData.value = res.data;
          self.loading = false;
        }).catch(err => {
          self.pageData.error = err;
          self.pageData.loading = false;
        });
      },
      fetchChartData() {
        const self = this;
        console.log('Refreshing...')
        self.chartData.error = null;
        self.chartData.value = null;
        self.chartData.loading = true;

        axios.get(
          window.leadlucky.apiUrl
          + `pages/${self.pageName}/stats/${self.selectedDate.year}-${pad(self.selectedDate.month,2)}${self.mode === 'Day' ? `-${pad(self.selectedDate.day,2)}`: ''}`,
          {headers: auth.getAuthHeader()}).then(res => {
          self.chartData.value = res.data;
          self.loading = false;
        }).catch(err => {
          self.chartData.error = err;
          self.chartData.loading = false;
        });
      }
    }
  }
</script>
<style lang="scss" scoped>
  #page-title {
    font-size: 48pt;
  }

  .big-number {
    font-size: 96pt;
  }

  .data-title {
    font-size: 24pt;
  }
</style>


