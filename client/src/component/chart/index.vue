<template>
  <div ref="chart" class="chart" :style="{height, width}"></div>
</template>

<script>
import echarts from 'echarts'
export default {
  name: 'chart',
  props: {
    options: {
      type: Object,
      default: () => ({})
    },
    height: {
      type: [Number, String],
      default: 'auto'
    },
    width: {
      type: [Number, String],
      default: 'auto'
    }
  },
  data: () => ({
    chart: null
  }),
  mounted () {
    this.$nextTick(() => {
      this.chart = echarts.init(this.$refs.chart)
    })
  },
  methods: {},
  watch: {
    chart () {
      if (this.chart && this.options) {
        this.chart.setOption(this.options)
      }
    },
    options () {
      if (this.chart && this.options) {
        this.chart.clear()
        this.chart.setOption(this.options)
      }
    }
  },
  computed: {}
}
</script>

<style scoped>

</style>
