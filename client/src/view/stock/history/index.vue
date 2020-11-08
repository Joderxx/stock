<template>
  <el-row>
    <el-col>
      <el-form inline label-width="80">
        <el-form-item label="股票代号">
          <stock-select v-model="queryForm.code"></stock-select>
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
        <el-form-item >
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col>
      <stock-chart :chart-data="tableData"></stock-chart>
    </el-col>
    <el-col>
      <s-table :table-data="tableData">
        <el-table-column label="代号" prop="code"></el-table-column>
        <el-table-column label="时间" prop="day"></el-table-column>
        <el-table-column label="开盘价" prop="open" align="right"></el-table-column>
        <el-table-column label="收盘价" prop="close" align="right"></el-table-column>
        <el-table-column label="最高价" prop="high" align="right"></el-table-column>
        <el-table-column label="最低价" prop="low" align="right"></el-table-column>
        <el-table-column label="成交量" prop="volume" align="right"></el-table-column>
      </s-table>
    </el-col>
  </el-row>
</template>

<script>
import {stockHistory} from '@/api/stock'

export default {
  data: () => ({
    queryForm: {
      code: '',
      startDate: '',
      endDate: ''
    },
    tableData: []
  }),
  mounted () {
    const params = this.$route.query
    this.queryForm = {
      code: 'sz000001',
      endDate: params.endDate || this.moment().format('YYYY-MM-DD'),
      startDate: params.startDate || this.moment().add(-3, 'month').format('YYYY-MM-DD')
    }
    this.handleQuery()
  },
  methods: {
    handleQuery () {
      stockHistory(this.queryForm)
        .then(res => {
          res.forEach(e => {
            e.open = e.open.toFixed(2)
            e.close = e.close.toFixed(2)
            e.high = e.high.toFixed(2)
            e.low = e.low.toFixed(2)
            e.volume = e.volume.toFixed(0)
          })
          this.tableData = res
        })
    },
    handleDate (e) {
      this.queryForm = {
        ...this.queryForm,
        startDate: e[0],
        endDate: e[1]
      }
    }
  },
  computed: {}
}
</script>

<style scoped>

</style>
