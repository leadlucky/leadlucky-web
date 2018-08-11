<template>
  <div :id="elementId"/>
</template>
<script>


  export default {
    name: 'morris-chart',
    props: {
      data: {
        type: Array,
        default: () => []
      },
      xkey: {
        type: String,
        required: true
      },
      ykeys: {
        type: Array,
        required: true
      },
      lineColors: {
        type: Array,
        default: () => null
      },
      labels: {
        type: Array,
        default: () => null
      },
      fillOpacity: {
        type: Number,
        default: 0.8
      },
      pointSize: {
        type: Number,
        default: 0
      },
      lineWidth: {
        type: Number,
        default: 0
      },
      gridLineColor: {
        type: String,
        default: '#e0e0e0'
      },
      smooth: {
        type: Boolean,
        default: true
      }
    },
    created(){
      window.addEventListener('resize', this.renderChart)
    },
    mounted(){
      this.renderChart();
      this.$nextTick()
    },
    updated(){
      this.renderChart();
    },
    watch: {
      data: {
        handler(n, _){
          this.renderChart()
        },
        deep: true
      }
    },
    methods: {
      renderChart(){
        document.getElementById(this.elementId).innerHTML = '';
        let opts = {
          element: this.elementId,
          data: this.data,
          xkey: 'period',
          ykeys: ['smartphone', 'windows', 'mac'],
          labels: ['iPhone', 'Android', 'Web'],
          pointSize: this.pointSize,
          lineWidth: this.lineWidth,
          resize: false,
          fillOpacity: this.fillOpacity,
          behaveLikeLine: true,
          gridLineColor: this.gridLineColor,
          smooth: this.smooth,
          hideHover: 'auto'
        };
        ['lineColors', 'labels'].forEach((propName) => {
          if(this[propName]){
            opts[propName] = this[propName]
          }
        });

        window.Morris.Area(opts);
        console.log('chart rendered ' + this.elementId)
      }
    },
    computed: {
      elementId(){
        return `m-chart-${this._uid}`;
      }
    }
  }
</script>
