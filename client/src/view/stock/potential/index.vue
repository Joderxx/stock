<template>
  <el-row>
    <el-col>
      <el-form inline label-width="80">
        <el-form-item label="过去月数">
          <el-input v-model.number="queryForm.month"></el-input>
        </el-form-item>
        <template v-if="queryForm.predict">
          <el-form-item label="过去预估月数">
            <el-input v-model.number="queryForm.predictMonth"></el-input>
          </el-form-item>
          <el-form-item label="当前基准日期">
            <el-date-picker v-model="queryForm.currDate" value-format="yyyy-MM-dd"></el-date-picker>
          </el-form-item>
        </template>
        <el-form-item label="是否预估">
          <el-checkbox v-model="queryForm.predict"></el-checkbox>
        </el-form-item>
        <el-form-item >
          <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col>
      <el-form inline label-width="80">
        <el-form-item label="最大值天数范围">
          <el-input v-model.number="filter.max[0]" class="input-number"></el-input>
          <span>~</span>
          <el-input v-model.number="filter.max[1]" class="input-number"></el-input>
        </el-form-item>
        <el-form-item label="最小值天数范围">
          <el-input v-model.number="filter.min[0]" class="input-number"></el-input>
          <span>~</span>
          <el-input v-model.number="filter.min[1]" class="input-number"></el-input>
        </el-form-item>
        <el-form-item >
          <el-checkbox v-model="filter.lowMean">价格小于均值</el-checkbox>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col>
      <s-table :table-data="table">
        <el-table-column label="代号" prop="stockCode"></el-table-column>
        <el-table-column label="名称" prop="stockName"></el-table-column>
        <el-table-column :label="`过去最大值`" prop="max" align="right"></el-table-column>
        <el-table-column :label="`百分比`" align="right">
          <template slot-scope="{ row }">
            <span>{{percentDiff(row.max, row.curr)}}</span>
          </template>
        </el-table-column>
        <el-table-column :label="`距离天数`" prop="diffMaxDay" align="right"></el-table-column>
        <el-table-column :label="`过去最小值`" prop="min" align="right"></el-table-column>
        <el-table-column :label="`百分比`" align="right">
          <template slot-scope="{ row }">
            <span>{{percentDiff(row.curr, row.min)}}</span>
          </template>
        </el-table-column>
        <el-table-column :label="`距离天数`" prop="diffMinDay" align="right"></el-table-column>
        <el-table-column :label="`过去均值`" prop="mean" align="right">
          <template slot-scope="{ row }">
            <span>{{row.mean.toFixed(2)}}</span>
          </template>
        </el-table-column>
        <el-table-column label="未来均值" prop="predMean" align="right">
          <template slot-scope="{ row }">
            <span>{{row.predMean.toFixed(2)}}</span>
          </template>
        </el-table-column>
        <el-table-column label="当前价格" prop="curr" align="right"></el-table-column>
        <template v-if="queryForm.predict">
          <el-table-column :label="`未来最大值`" prop="predMax" align="right"></el-table-column>
          <el-table-column label="涨幅">
            <template slot-scope="{ row }">
              <span>{{percentDiff(row.predMax, row.curr)}}</span>
            </template>
          </el-table-column>
          <el-table-column :label="`距离天数`" prop="predDiffMaxDay" align="right"></el-table-column>
          <el-table-column label="未来最小值" prop="predMin" align="right"></el-table-column>
          <el-table-column label="涨幅" align="right">
            <template slot-scope="{ row }">
              <span>{{percentDiff(row.predMin, row.curr)}}</span>
            </template>
          </el-table-column>
          <el-table-column :label="`距离天数`" prop="predDiffMinDay" align="right" sortable></el-table-column>
        </template>
      </s-table>
    </el-col>
  </el-row>
</template>

<script>
import {stockPotentialList} from '@/api/stock'

export default {
  data () {
    return {
      queryForm: {
        month: 12,
        predictMonth: 6,
        currDate: this.moment().add(-1, 'day').format('YYYY-MM-DD'),
        predict: true
      },
      filter: {
        min: [5, 150],
        max: [50, 150],
        lowMean: false
      },
      loading: false,
      tableData: []
    }
  },
  mounted () {
    this.handleQuery()
  },
  methods: {
    async handleQuery () {
      this.loading = true
      const res = await stockPotentialList(this.queryForm)
      if (res) {
        this.tableData = res
      }
      this.loading = false
    },
    percentDiff (a, b) {
      return ((a - b) * 100.0 / b).toFixed(2) + '%'
    }
  },
  watch: {},
  computed: {
    table () {
      const arr = this.tableData
        .filter(e => (e.diffMaxDay >= this.filter.max[0] && e.diffMaxDay <= this.filter.max[1]) && (e.diffMinDay >= this.filter.min[0] && e.diffMinDay <= this.filter.min[1]))
        .filter(e => !this.filter.lowMean || e.curr < e.mean)
      return arr.sort((x1, x2) => {
        const n1 = (x1.predMax - x1.curr) / x1.curr
        const n2 = (x2.predMax - x2.curr) / x2.curr
        return n2 - n1
      })
    }
  }
}
</script>

<style scoped>
  .input-number {
    width: 100px;
  }
</style>
