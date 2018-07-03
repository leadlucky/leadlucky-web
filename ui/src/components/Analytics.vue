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
                <h2 class="data-title">Email Count:</h2>
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
                :value="conversionRate"
                color="teal"
                >
                {{ conversionRate }}%
                </v-progress-circular>
            </v-flex>
        </v-layout>
    </v-container>
    <v-container fluid fill-height v-else-if="pageData.loading">
        <v-layout justify-center align-center>
            <v-flex text-xs-center>
                <v-progress-circular
                indeterminate
                color="primary"
                ></v-progress-circular>
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

export default {
    name: 'analytics',
    created() {
        this.fetchData()
    },
    data: () => ({
        pageData: {
            loading: false,
            error: null,
            value: null
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
        pageName() {
            return this.$route.params.pageName
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


