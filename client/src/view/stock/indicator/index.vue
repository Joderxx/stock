<template>
  <el-row>
    <el-col>
      <el-form inline label-width="80">
        <el-form-item label="股票代号">
          <stock-select v-model="queryForm.tsCode"></stock-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            :value="[queryForm.startDate, queryForm.endDate]"
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @input="handleDate"
            :clearable="false"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
          <el-button type="primary" v-if="queryForm.tsCode" @click="reload">加载历史</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col>
      <indicator-chart :data="table"></indicator-chart>
    </el-col>
  </el-row>
</template>

<script>
import {reloadIndicator, stockIndicatorList} from '@/api/stock'
import IndicatorChart from '@/view/stock/indicator/components/indicator-chart'

export default {
  components: {IndicatorChart},
  data () {
    return {
      queryForm: {
        tsCode: 'sz000001',
        endDate: this.moment().format('YYYY-MM-DD'),
        startDate: this.moment().add(-6, 'month').format('YYYY-MM-DD')
      },
      loading: false,
      table: []
    }
  },
  mounted () {
    this.handleQuery()
  },
  methods: {
    async handleQuery () {
      this.loading = true
      const res = await stockIndicatorList(this.queryForm)
      this.loading = false
      if (res) {
        this.table = res
      }
    },
    async reload () {
      const res = await reloadIndicator(this.queryForm)
      if (res) {
        this.$message.success('加载成功')
      } else {
        this.$message.error('加载失败')
      }
    },
    handleDate (e) {
      this.queryForm = {
        ...this.queryForm,
        startDate: e[0],
        endDate: e[1]
      }
    }
  },
  watch: {},
  computed: {}
}
</script>

<style scoped>

</style>
