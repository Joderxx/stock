<template>
  <chart :options="options" height="400px"></chart>
</template>

<script>
import Chart from '@/component/chart/index'
export default {
  name: 'indicator-chart',
  components: {Chart},
  props: {
    data: {
      type: Array,
      default: () => []
    }
  },
  data: () => ({
    chart: null
  }),
  mounted () {
  },
  methods: {},
  watch: {
  },
  computed: {
    options () {
      return {
        title: {
          text: ''
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['市盈率', '前复权价格']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '20%'
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: this.data.map(e => e.date)
          }
        ],
        yAxis: {
          type: 'value'
        },
        dataZoom: [
          {
            type: 'slider',
            start: 30,
            end: 70
          }
        ],
        series: [
          {
            name: '市盈率',
            type: 'line',
            data: this.data.map(e => e.pe)
          },
          {
            name: '前复权价格',
            type: 'line',
            data: this.data.map(e => e.price)
          }
        ]
      }
    }
  }
}
</script>

<style scoped>

</style>
