<template>
  <div class="order-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header-custom">
          <div class="header-left">
            <el-icon class="header-icon"><Tickets /></el-icon>
            <span class="header-title">网点订单管理</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待报价" name="1" />
        <el-tab-pane label="待入库" name="8" />
        <el-tab-pane label="已入库" name="9" />
      </el-tabs>

      <div class="table-container">
        <el-table v-loading="loading" :data="orderList" row-key="id">
          <el-table-column prop="orderNo" label="订单编号" min-width="150" />
          <el-table-column prop="senderName" label="发件人" min-width="100" />
          <el-table-column prop="receiverName" label="收件人" min-width="100" />
          <el-table-column prop="goodsName" label="货物" min-width="80" />
          <el-table-column prop="weight" label="重量" width="80" />
          <el-table-column prop="totalFee" label="费用" width="100">
            <template #default="scope">
              ¥{{ scope.row.totalFee || 0 }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button v-if="scope.row.pricingStatus === 1" type="primary" size="small" @click="handleQuote(scope.row)">
                报价
              </el-button>
              <el-button v-if="scope.row.status == 8" type="success" size="small" @click="handleConfirmReceive(scope.row)">
                确认收货
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="quoteDialogVisible" title="网点报价" width="400px">
      <el-form :model="quoteForm" label-width="80px">
        <el-form-item label="订单">
          <span>{{ quoteForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="底价" required>
          <el-input-number v-model="quoteForm.baseFee" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户报价" required>
          <el-input-number v-model="quoteForm.customerPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="运输天数" required>
          <el-input-number v-model="quoteForm.deliveryDays" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="quoteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitQuote">提交报价</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="receiveDialogVisible" title="确认收货" width="400px">
      <el-form :model="receiveForm" label-width="80px">
        <el-form-item label="订单">
          <span>{{ receiveForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="检查结果">
          <el-radio-group v-model="receiveForm.checkResult">
            <el-radio label="ok">正常</el-radio>
            <el-radio label="damaged">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="receiveForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="receiveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReceive">确认收货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const activeTab = ref('all')
const quoteDialogVisible = ref(false)
const quoteForm = ref({})
const receiveDialogVisible = ref(false)
const receiveForm = ref({})

const getOrderList = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/page', { params: { current: 1, size: 100 } })
    let orders = res.records || res.data?.records || []
    if (activeTab.value !== 'all') {
      orders = orders.filter(o => o.status === parseInt(activeTab.value))
    }
    orderList.value = orders
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 1: 'warning', 8: 'info', 9: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 1: '待报价', 8: '待入库', 9: '已入库' }
  return map[status] || '未知'
}

const handleTabChange = () => {
  getOrderList()
}

const handleQuote = (order) => {
  quoteForm.value = { orderId: order.id, orderNo: order.orderNo, baseFee: 100, customerPrice: 150, deliveryDays: 2 }
  quoteDialogVisible.value = true
}

const handleSubmitQuote = async () => {
  try {
    await request.post('/network-quote', {
      orderId: quoteForm.value.orderId,
      baseFee: quoteForm.value.baseFee,
      finalPrice: quoteForm.value.customerPrice,
      deliveryDays: quoteForm.value.deliveryDays
    })
    ElMessage.success('报价成功')
    quoteDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('报价失败')
  }
}

const handleConfirmReceive = (order) => {
  receiveForm.value = { orderId: order.id, orderNo: order.orderNo, checkResult: 'ok', remark: '' }
  receiveDialogVisible.value = true
}

const handleSubmitReceive = async () => {
  try {
    await request.post('/network/confirm-receive', {
      orderId: receiveForm.value.orderId,
      networkId: 1,
      checkResult: receiveForm.value.checkResult,
      remark: receiveForm.value.remark
    })
    ElMessage.success('确认收货成功')
    receiveDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('确认收货失败')
  }
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-page {
  padding: var(--spacing-lg);
}
.main-card {
  border-radius: var(--radius-lg);
}
.card-header-custom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}
.header-icon {
  font-size: 20px;
  color: var(--color-primary);
}
.header-title {
  font-size: 18px;
  font-weight: 600;
}
.table-container {
  padding: var(--spacing-md);
}
</style>
