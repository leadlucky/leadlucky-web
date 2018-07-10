<template>
    <v-container v-if="pageData.value" text-xs-left>
        <v-layout row>
            <v-flex xs4 >
                <h1 id="page-title">{{pageName}}</h1>
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
        <v-layout row>
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
        </v-layout>
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
import { GChart } from 'vue-google-charts'

export default {
    name: 'analytics',
    components: {GChart},
    created() {
        this.fetchData()
        this.fetchChartData()
    },
    data: () => ({
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
            },
            colors: ['#a52714', '#097138']
          },
        }
    }),
    computed: {
        conversionRate(){
            if(this.pageData.value) {
                const {viewCount, emailCount} = this.pageData.value;
                return ((emailCount / viewCount) * 100).toFixed(2)
            }
            return null;
        },
        conversionColor(){
          const cr = Number(this.conversionRate || 0);
          if(cr < 10) return 'red';
          if(cr < 40) return 'yellow';
          return 'green';
        },
        pageName() {
            return this.$route.params.pageName
        },
        chartInfo(){
          const headers = ['Page Visit Data','Views', 'Emails'];
          const displayedData = this.chartData.value ? Object.keys(this.chartData.value).map(
            (timestamp) => [timestamp, this.chartData.value[timestamp].views, this.chartData.value[timestamp].emails]
          ) : []

          return [headers, ...displayedData]
        }
    },
    methods: {
        fetchData(){
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
        fetchChartData(){
          const self = this;

          self.chartData.error = null;
          self.pageData.loading = true;

          axios.get(window.leadlucky.apiUrl + `pages/${self.pageName}/countData/month/2018-06`, {headers: auth.getAuthHeader()}).then(res => {
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


