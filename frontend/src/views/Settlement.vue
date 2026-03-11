<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>结算管理</span>
          <el-button type="primary" @click="handleAdd">新增结算</el-button>
          <el-button type="info" @click="handleReconciliation">对账管理</el-button>
          <el-button type="info" @click="handleStatistics">统计分析</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="settlementList" style="width: 100%">
        <el-table-column prop="id" label="结算ID" width="100"></el-table-column>
        <el-table-column prop="settlementNo" label="结算编号"></el-table-column>
        <el-table-column prop="type" label="结算类型"></el-table-column>
        <el-table-column prop="orderId" label="订单ID"></el-table-column>
        <el-table-column prop="amount" label="金额"></el-table-column>
        <el-table-column prop="status" label="状态"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>

    <el-dialog v-model="dialogVisible" title="结算详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="结算编号">
          <el-input v-model="form.settlementNo"></el-input>
        </el-form-item>
        <el-form-item label="结算类型">
          <el-select v-model="form.type">
            <el-option label="客户应收" value="0"></el-option>
            <el-option label="司机应付" value="1"></el-option>
            <el-option label="网点间结算" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单ID">
          <el-input v-model="form.orderId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="form.customerId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="司机ID">
          <el-input v-model="form.driverId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="始发网点ID">
          <el-input v-model="form.startNetworkId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="到达网点ID">
          <el-input v-model="form.endNetworkId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="金额">
          <el-input v-model="form.amount" type="number"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="未结算" value="0"></el-option>
            <el-option label="已结算" value="1"></el-option>
            <el-option label="已付款" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="付款方式">
          <el-select v-model="form.paymentMethod">
            <el-option label="现付" value="0"></el-option>
            <el-option label="到付" value="1"></el-option>
            <el-option label="月结" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="提成">
          <el-input v-model="form.commission" type="number"></el-input>
        </el-form-item>
        <el-form-item label="中转费">
          <el-input v-model="form.transferFee" type="number"></el-input>
        </el-form-item>
        <el-form-item label="干线费">
          <el-input v-model="form.trunkFee" type="number"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const settlementList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})

const getSettlementList = async () => {
  loading.value = true
  try {
    const res = await request.get('/settlement/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    settlementList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该结算吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/settlement/${id}`)
    getSettlementList()
    ElMessage.success('删除结算成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除结算失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/settlement', form.value)
      ElMessage.success('更新结算成功')
    } else {
      await request.post('/settlement', form.value)
      ElMessage.success('新增结算成功')
    }
    dialogVisible.value = false
    getSettlementList()
  } catch (error) {
    ElMessage.error('保存结算失败，请稍后重试')
  }
}

const handleReconciliation = async () => {
  try {
    await request.get('/settlement/reconciliation')
    ElMessage.success('对账管理成功')
  } catch (error) {
    ElMessage.error('对账管理失败，请稍后重试')
  }
}

const handleStatistics = async () => {
  try {
    await request.get('/settlement/statistics')
    ElMessage.success('统计分析成功')
  } catch (error) {
    ElMessage.error('统计分析失败，请稍后重试')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getSettlementList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getSettlementList()
}

onMounted(() => {
  getSettlementList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>
