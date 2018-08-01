<script>
  import themes from '@leadlucky/leadlucky-themes';

  export default {
    name: "preview-page",
    props: {
      themeName: {
        type: String,
        default: null,
      },
      customData: {
        type: Object,
        default: () => ({})
      },
      fullscreen: {
        type: Boolean,
        default: false,
      },
      forceUpdate: {
        type: Boolean,
        default: true
      }
    },
    watch: {
      forceUpdate: {
        deep: true, handler(v) {
          this.$forceUpdate()
        }
      }
    },
    computed: {
      component() {
        return themes[this.themeName].component
      }
    },
    render(createElement) {
      return createElement(this.component, {
        props: {propData: Object.assign({}, this.customData), themeName: this.themeName},
        attrs: {
          style: `padding: 0 0 0 0;`
        }
      })
    }
  }
</script>

