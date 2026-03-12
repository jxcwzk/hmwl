<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>结算管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增结算</el-button>
            <el-button type="info" @click="handleReconciliation">对账管理</el-button>
            <el-button type="info" @click="handleStatistics">统计分析</el-button>
            <el-button type="success" @click="goToInvoice">发票管理</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="业务用户">
          <el-select v-model="filterForm.customerId" placeholder="请选择业务用户" clearable style="width: 180px;">
            <el-option v-for="user in businessUserList" :key="user.id" :label="user.username" :value="user.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="结算状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="未结算" :value="0"></el-option>
            <el-option label="已结算" :value="1"></el-option>
            <el-option label="已付款" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间段">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="settlementList" style="width: 100%">
        <el-table-column prop="id" label="结算ID" width="80"></el-table-column>
        <el-table-column prop="settlementNo" label="结算编号" width="150"></el-table-column>
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="customerName" label="业务用户" width="120">
          <template #default="scope">
            {{ scope.row.customerName || scope.row.customerId }}
          </template>
        </el-table-column>
        <el-table-column prop="orderAmount" label="订单原金额" width="120">
          <template #default="scope">
            ¥{{ (scope.row.orderAmount || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="recommendedPrice" label="推荐客户价" width="120">
          <template #default="scope">
            ¥{{ (scope.row.recommendedPrice || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="最终结算金额" width="120">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">¥{{ (scope.row.finalAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待确认</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已确认</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="primary">已开票</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="info">已收款</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 0" type="success" size="small" @click="handleConfirm(scope.row)">确认</el-button>
            <el-button v-if="scope.row.status === 1" type="warning" size="small" @click="handleCreateInvoice(scope.row)">开票</el-button>
            <el-button v-if="scope.row.status === 2" type="info" size="small" @click="handleMarkPaid(scope.row)">收款</el-button>
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
          <el-input v-model="form.settlementNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="订单编号">
          <el-input v-model="form.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="业务用户">
          <el-input v-model="form.customerName" disabled></el-input>
        </el-form-item>
        <el-form-item label="订单原金额">
          <el-input :value="form.orderAmount ? '¥' + form.orderAmount.toFixed(2) : '-'" disabled></el-input>
        </el-form-item>
        <el-form-item label="推荐客户价">
          <el-input :value="form.recommendedPrice ? '¥' + form.recommendedPrice.toFixed(2) : '-'" disabled>
            <template #append>
              <el-tooltip content="基于30%利润率计算: 订单金额 × 1.4286">
                <el-button>?</el-button>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="最终结算金额">
          <el-input v-model="form.finalAmount" type="number">
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="结算状态">
          <el-select v-model="form.status" disabled>
            <el-option label="待确认" :value="0"></el-option>
            <el-option label="已确认" :value="1"></el-option>
            <el-option label="已开票" :value="2"></el-option>
            <el-option label="已收款" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">保存金额</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="invoiceDialogVisible" title="确认开票" width="400px">
      <p>确定要为该结算记录开具发票吗？</p>
      <p>结算编号：<strong>{{ currentSettlement?.settlementNo }}</strong></p>
      <p>金额：<strong>¥{{ currentSettlement?.amount?.toFixed(2) }}</strong></p>
      <template #footer>
        <el-button @click="invoiceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCreateInvoice">确认开票</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const settlementList = ref([])
const businessUserList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const invoiceDialogVisible = ref(false)
const form = ref({})
const currentSettlement = ref(null)
const dateRange = ref([])

const filterForm = ref({
  customerId: null,
  status: null,
  startDate: '',
  endDate: ''
})

const getBusinessUserList = async () => {
  try {
    const res = await request.get('/business-user/list')
    businessUserList.value = res.data || res
  } catch (error) {
    console.error('获取业务用户列表失败', error)
  }
}

const getSettlementList = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (filterForm.value.customerId) {
      params.customerId = filterForm.value.customerId
    }
    if (filterForm.value.status !== null && filterForm.value.status !== '') {
      params.status = filterForm.value.status
    }
    if (filterForm.value.startDate) {
      params.startDate = filterForm.value.startDate
    }
    if (filterForm.value.endDate) {
      params.endDate = filterForm.value.endDate
    }
    const res = await request.get('/settlement/page', { params })
    settlementList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  currentPage.value = 1
  getSettlementList()
}

const handleReset = () => {
  filterForm.value = {
    customerId: null,
    status: null,
    startDate: '',
    endDate: ''
  }
  dateRange.value = []
  currentPage.value = 1
  getSettlementList()
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    filterForm.value.startDate = val[0]
    filterForm.value.endDate = val[1]
  } else {
    filterForm.value.startDate = ''
    filterForm.value.endDate = ''
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

const handleCreateInvoice = (row) => {
  currentSettlement.value = row
  invoiceDialogVisible.value = true
}

const confirmCreateInvoice = async () => {
  try {
    await request.post(`/invoice/create/${currentSettlement.value.id}`)
    ElMessage.success('开票成功')
    invoiceDialogVisible.value = false
    getSettlementList()
  } catch (error) {
    ElMessage.error('开票失败，该结算可能已开票')
  }
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确认后将进入已确认状态，是否继续？', '确认结算', {
      type: 'warning'
    })
    await request.put(`/settlement/update-status/${row.id}?status=1`)
    ElMessage.success('结算确认成功')
    getSettlementList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败，请稍后重试')
    }
  }
}

const handleMarkPaid = async (row) => {
  try {
    await ElMessageBox.confirm('确认已收到款项？', '标记收款', {
      type: 'warning'
    })
    await request.put(`/settlement/update-status/${row.id}?status=3`)
    ElMessage.success('收款标记成功')
    getSettlementList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('标记失败，请稍后重试')
    }
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

const goToInvoice = () => {
  router.push('/invoice')
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
  getBusinessUserList()
  getSettlementList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
