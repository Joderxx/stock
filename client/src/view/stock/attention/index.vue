<template>
  <el-row>
    <el-col>
      <el-form inline>
        <el-form-item label="股票代号">
          <stock-select v-model="queryForm.tsCode"></stock-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.stockType" multiple>
            <el-option v-for="type in stockType" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="queryForm.attention">是否关注</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button type="success" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col>
      <s-table :table-data="table">
        <el-table-column label="股票代号" prop="tsCode"></el-table-column>
        <el-table-column label="股票名称" prop="stockName"></el-table-column>
        <el-table-column label="类型" prop="stockType">
          <template slot-scope="{ row }">
            <el-tag v-for="type in row.stockType" :key="type">{{type}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否关注" prop="attention">
          <template slot-scope="{ row }">
            <el-switch :value="row.attention"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="{ row }">
            <el-link icon="el-icon-edit" type="primary" @click="handleEdit(row.tsCode)">编辑</el-link>
            <el-popconfirm title="是否删除" @onConfirm="handleDelete(row.tsCode)">
              <el-link slot="reference" icon="el-icon-delete" type="danger" style="margin-left: 10px">删除</el-link>
            </el-popconfirm>
          </template>
        </el-table-column>
      </s-table>
    </el-col>
    <el-dialog :visible.sync="dialog" width="20%">
      <el-form label-width="100px" label-suffix=":">
        <el-form-item label="股票代号">
          <stock-select v-model="submitForm.tsCode"></stock-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="submitForm.stockType" multiple>
            <el-option v-for="type in stockType" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否关注">
          <el-switch v-model="submitForm.attention"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleSave" type="primary">保存</el-button>
      </template>
    </el-dialog>
  </el-row>
</template>

<script>
import {deleteStockExtra, getStockExtra, listStockExtra, updateStockExtra} from '@/api/stock'

export default {
  data: () => ({
    queryForm: {
      tsCode: '',
      stockType: [],
      attention: true
    },
    table: [],
    submitForm: {
      tsCode: '',
      stockType: [],
      attention: true
    },
    dialog: false,
    stockType: [
      '白酒', '证券', '银行', '计算机', '互联网', '煤炭', '游戏', '零售', '保险'
    ].sort()
  }),
  mounted () {
    this.handleQuery()
  },
  methods: {
    async handleQuery () {
      const res = await listStockExtra(this.queryForm)
      if (res) {
        this.table = res
      }
    },
    handleAdd () {
      this.dialog = true
      this.submitForm = {
        tsCode: '',
        stockType: [],
        attention: true
      }
    },
    async handleEdit (tsCode) {
      const res = await getStockExtra({tsCode})
      if (res) {
        this.submitForm = res
        this.dialog = true
      }
    },
    async handleSave () {
      const res = await updateStockExtra({}, this.submitForm)
      if (res) {
        this.dialog = false
        this.$message.success('保存成功')
        await this.handleQuery()
      } else {
        this.$message.error('保存失败')
      }
    },
    async handleDelete (tsCode) {
      const res = await deleteStockExtra({tsCode})
      if (res) {
        this.$message.success('删除成功')
        await this.handleQuery()
      } else {
        this.$message.error('删除失败')
      }
    }
  },
  watch: {},
  computed: {}
}
</script>

<style scoped>

</style>
