<template>
  <div ref="dragArea">
    <PreviewPage
      :themeName="$route.params.themeName"
      :customData="pageData"
      :fullscreen="portalData.fullscreen"
      :forceUpdate="flipper"/>
    <draggable :style="`opacity: ${controls ? 1 : 0.2};`" :initial-top="128" :initial-right="16" :width="450" :min-y="64">
      <v-card slot="stuff" style="max-height: 55vh; overflow-y: scroll;">
        <v-card-actions>
          <v-switch label="Fullscreen" v-model="portalData.fullscreen"></v-switch>
          <v-switch label="Controls" v-model="controls"></v-switch>
        </v-card-actions>
        <v-stepper v-model="stepperStep" v-if="controls">
          <v-stepper-header>
            <v-stepper-step step="1" :complete="stepperStep > 1" editable>Edit Page Info</v-stepper-step>
            <v-divider></v-divider>
            <v-stepper-step step="2" :complete="stepperStep > 2" editable>Set Page Details</v-stepper-step>
          </v-stepper-header>
          <v-stepper-items>
            <v-stepper-content step="1">
              <v-form v-model="valid">
                <span
                    v-for="fieldName in Object.keys(currentDataContracts)"
                    :key="`edit-${fieldName}`">
                  <v-switch v-if="currentDataContracts[fieldName].type === Boolean && isFieldEnabled(fieldName)"
                            :label="currentDataContracts[fieldName].name"
                            v-model="pageData[fieldName]"
                            @change="updatePageData(fieldName, $event)"/>
                  <v-text-field v-else-if="isFieldEnabled(fieldName)"
                                :label="currentDataContracts[fieldName].name"
                                @input="updatePageData(fieldName, $event)"
                                :value="pageData[fieldName]"
                                :prefix="currentDataContracts[fieldName].prefix"
                                :rules="required(currentDataContracts[fieldName].name)"
                                required>
                  </v-text-field>
                </span>
              </v-form>
              <v-btn color="primary" @click.native="stepperStep = 2" :disabled="!valid">Continue</v-btn>
              <v-btn flat to="/">Cancel</v-btn>
            </v-stepper-content>
            <v-stepper-content step="2">
              <v-text-field
                label="Redirect Url"
                v-model="redirectUrl"
                :rules="urlRule"
                required
              ></v-text-field>
              <v-form v-model="valid">
                <v-text-field
                  label="Page Id"
                  v-model="pageid"
                  :rules="pageIdRule"
                  prefix="/"
                  required
                ></v-text-field>
                <p class="text-xs-left" v-text="errorText" style="color: red"></p>
              </v-form>
              <v-btn v-if="stepperStep === 2" flat @click="stepperStep = 1">back</v-btn>
              <v-btn color="primary" @click.native="createPage" :disabled="!valid">Submit</v-btn>
            </v-stepper-content>
          </v-stepper-items>
        </v-stepper>
      </v-card>
    </draggable>
  </div>
</template>

<script>
  import PreviewPage from './PreviewPage';
  import Draggable from "./Draggable";

  import {validationMixin}  from '@leadlucky/leadlucky-themes';
  import axios from 'axios'

  import auth from '../auth'
  import themes from '@leadlucky/leadlucky-themes';

  import store from '../store';

  export default {
    name: 'EditPage',
    mixins: [validationMixin],
    components: {
      Draggable,
      PreviewPage
    },
    mounted() {
      this.loadDefaults();
    },
    data() {
      return {
        // Stepper & Form
        valid: false,
        stepperStep: 1,
        errorText: '',
        //User Input
        pageData: {},
        redirectUrl: 'https://google.com',
        pageid: 'mySite',
        // Editor Display
        controls: true,
        flipper: false,
        portalData: store.portalData
      }
    },
    computed: {
      currentDataContracts() {
        return themes[this.$route.params.themeName].component.dataContract;
      }
    },
    watch: {
      $route() {
        this.loadDefaults()
      }
    },
    methods: {
      isFieldEnabled(fieldName){

        return this.currentDataContracts[fieldName].dependsOn ?
            this.pageData[this.currentDataContracts[fieldName].dependsOn] : true
      },
      updatePageData(fieldName, value) {
        console.log(`${fieldName}: ${value}`)
        this.flipper = !this.flipper
        this.$set(this.pageData, fieldName, value)
      }
      ,
      loadDefaults() {
        this.pageData = {};
        const self = this;
        Object.keys(this.currentDataContracts).forEach(fieldName => {
          self.pageData[fieldName] = this.currentDataContracts[fieldName].default
        })
      }
      ,
      createPage() {
        let payload = {
          name: this.pageid,
          themeName: this.$route.params.themeName,
          data: JSON.stringify(Object.assign({redirectUrl: this.redirectUrl},this.pageData))
        };

        const self = this;

        axios.post(window.leadlucky.apiUrl + 'pages', payload, {
          headers: {"Authorization": "Bearer " + localStorage.getItem('access_token')}
        }).then(function (response) {
          if (response.data) {
            self.$router.history.push('/dashboard')
          } else {
            self.errorText = 'Page name already taken.'
          }
        });
      }
      ,
    }
  }
</script>

<style scoped>

</style>
