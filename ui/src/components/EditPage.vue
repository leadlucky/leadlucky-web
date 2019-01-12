<template>
  <div style="height: 100%; width: 100%; padding: 0;" class="container-fluid">
    <div class="row" style="height: 100%">
      <iframe ref="previewFrame" class="col-md-9" :style="`padding: 0; border: none; width: 100%; `"
              :src="`/#/${$route.params.themeName}`"></iframe>
      <div class="card col-md-3" style="max-height: 100%;margin: 0; overflow-y: scroll; padding: 0">

        <ela-tab :titles="['Page Info', 'Customize']">
          <div slot="content-0" class="col">
            <div class="form-group">
              <label>Page ID </label>
              <ela-input
                v-model="pageId.value"
                :validationError="pageId.validator(pageId.value)"
                type="text"
                placeholder="yourPageName"/>
            </div>
            <div class="form-group">
              <label>Redirect URL</label>
              <ela-input
                v-model="redirectUrl.value"
                :validationError="redirectUrl.validator(redirectUrl.value)"
                type="text"
                placeholder="https://www.yoursite.com/productpage"/>
            </div>
          </div>
          <div slot="content-1" class="col col">
            <div class="form-group" v-for="(f, i) in Object.keys(currentDataContracts)" v-if="isFieldEnabled(f)">
              <label>{{currentDataContracts[f].name}}</label>
              <ela-input
                v-if="currentDataContracts[f].type == String"
                v-model="pageData[f]"
                type="text"
                :placeholder="currentDataContracts[f].placeholder || ''"/>
              <input
                v-if="currentDataContracts[f].type == Boolean"
                v-model="pageData[f]"
                type="checkbox"
              />
            </div>
          </div>
        </ela-tab>
      </div>
    </div>
  </div>
</template>

<script>
  import PreviewPage from './PreviewPage';
  import Draggable from "./Draggable";

  import {validationMixin} from '@leadlucky/leadlucky-themes';
  import axios from 'axios'

  import auth from '../auth'
  import themes from '@leadlucky/leadlucky-themes';

  import store from '../store';
  import ElaInput from './ela/ElaInput.vue';
  import ElaTab from './ela/ElaTab.vue';

  export default {
    name: 'EditPage',
    mixins: [validationMixin],
    components: {
      Draggable,
      PreviewPage,
      ElaInput,
      ElaTab
    },
    mounted() {
      this.loadDefaults();
    },
    data() {
      return {
        dumdum: 'fff',
        // Stepper & Form
        valid: false,
        stepperStep: 1,
        errorText: '',
        //User Input
        pageData: {},
        redirectUrl: {
          value: '',
          validator: validationMixin.urlRule[0]
        },
        pageId: {
          value: '',
          validator: validationMixin.required('Page ID')[0]
        },
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
      pageData: {
        handler(n, _){
          const iframeWindow =  this.$refs.previewFrame.contentWindow;
          iframeWindow.postMessage(
            {customData: this.pageData},
            iframeWindow.origin
          )
        },
        deep: true
      },
      $route() {
        this.loadDefaults()
      }
    },
    methods: {
      getEditorHeight() {
        return (this.$refs.editor ? this.$refs.editor.clientHeight : 0)
      },
      isFieldEnabled(fieldName) {
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
        let newPageData = {};
        Object.keys(this.currentDataContracts).forEach(fieldName => {
          const chunk = {}
          newPageData[fieldName] = this.currentDataContracts[fieldName].default
        });
        this.pageData = newPageData
      },
      createPage() {
        let payload = {
          name: this.pageId.value,
          themeName: this.$route.params.themeName,
          data: JSON.stringify(Object.assign({redirectUrl: this.redirectUrl}, this.pageData))
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

    }
  }
</script>

<style scoped>

</style>
