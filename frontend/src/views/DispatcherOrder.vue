<template>
  <div class="order-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header-custom">
          <div class="header-left">
            <el-icon class="header-icon"><Tickets /></el-icon>
            <span class="header-title">调度订单管理</span>
          </div>
          <div class="header-actions">
            <el-button class="action-btn" @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all">
          <OrderTable :orders="orderList" :active-tab="activeTab" @action="handleAction" />
        </el-tab-pane>
        <el-tab-pane label="待派发比价" name="0">
          <OrderTable :orders="orderList" :active-tab="activeTab" @action="handleAction" />
        </el-tab-pane>
        <el-tab-pane label="待确认报价" name="1">
          <OrderTable :orders="orderList" :active-tab="activeTab" @action="handleAction" />
        </el-tab-pane>
        <el-tab-pane label="待安排提货" name="5">
          <OrderTable :orders="orderList" :active-tab="activeTab" @action="handleAction" />
        </el-tab-pane>
        <el-tab-pane label="待分配配送" name="9">
          <OrderTable :orders="orderList" :active-tab="activeTab" @action="handleAction" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 派发比价对话框 -->
    <el-dialog v-model="dispatchDialogVisible" title="派发比价" width="500px">
      <el-form :model="dispatchForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ dispatchForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="选择网点" required>
          <el-select v-model="dispatchForm.networkIds" multiple placeholder="请选择网点" style="width: 100%">
            <el-option v-for="n in networkList" :key="n.id" :label="n.name" :value="n.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dispatchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDispatch">确认派发</el-button>
      </template>
    </el-dialog>

    <!-- 选择报价对话框 -->
    <el-dialog v-model="quoteDialogVisible" title="选择报价" width="600px">
      <el-form :model="quoteForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ quoteForm.orderNo }}</span>
        </el-form-item>
      </el-form>
      <el-table :data="quoteList" v-loading="quoteLoading" style="margin-top: 20px;">
        <el-table-column prop="networkName" label="网点" />
        <el-table-column prop="baseFee" label="底价">
          <template #default="scope">¥{{ scope.row.baseFee }}</template>
        </el-table-column>
        <el-table-column prop="finalPrice" label="客户报价">
          <template #default="scope">¥{{ scope.row.finalPrice }}</template>
        </el-table-column>
        <el-table-column prop="deliveryDays" label="运输天数" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleSelectQuote(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 分配司机对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配司机" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="订单">
          <span>{{ assignForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="司机" required>
          <el-select v-model="assignForm.driverId" placeholder="请选择司机" style="width: 100%">
            <el-option v-for="d in driverList" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignDriver">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Refresh } from '@element-plus/icons-vue'
import request from '../utils/request'
import OrderTable from '../components/OrderTable.vue'

const loading = ref(false)
const orderList = ref([])
const activeTab = ref('all')

// 派发比价
const dispatchDialogVisible = ref(false)
const dispatchForm = ref({})
const networkList = ref([
  { id: 1, name: '北京网点' },
  { id: 2, name: '上海网点' },
  { id: 3, name: '广州网点' }
])

// 选择报价
const quoteDialogVisible = ref(false)
const quoteForm = ref({})
const quoteList = ref([])
const quoteLoading = ref(false)

// 分配司机
const assignDialogVisible = ref(false)
const assignForm = ref({})
const driverList = ref([
  { id: 1, name: '司机张三' },
  { id: 2, name: '司机李四' }
])

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

const handleTabChange = () => {
  getOrderList()
}

const handleRefresh = () => {
  getOrderList()
  ElMessage.success('刷新成功')
}

const handleAction = ({ type, order }) => {
  if (type === 'dispatch') {
    // 派发比价
    dispatchForm.value = { orderId: order.id, orderNo: order.orderNo, networkIds: [] }
    dispatchDialogVisible.value = true
  } else if (type === 'selectQuote') {
    // 选择报价
    quoteForm.value = { orderId: order.id, orderNo: order.orderNo }
    getQuoteList(order.id)
    quoteDialogVisible.value = true
  } else if (type === 'assignPickup') {
    // 安排提货
    assignForm.value = { orderId: order.id, orderNo: order.orderNo, type: 'pickup' }
    assignDialogVisible.value = true
  } else if (type === 'assignDelivery') {
    // 分配配送
    assignForm.value = { orderId: order.id, orderNo: order.orderNo, type: 'delivery' }
    assignDialogVisible.value = true
  }
}

// 派发比价
const handleDispatch = async () => {
  try {
    await request.post('/dispatch/request-quotes', {
      orderId: dispatchForm.value.orderId,
      networkPointIds: dispatchForm.value.networkIds
    })
    ElMessage.success('派发比价成功')
    dispatchDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('派发比价失败')
  }
}

// 获取报价列表
const getQuoteList = async (orderId) => {
  quoteLoading.value = true
  try {
    const res = await request.get('/dispatch/quotes', { params: { orderId } })
    quoteList.value = res.data || res || []
  } catch (error) {
    quoteList.value = []
  } finally {
    quoteLoading.value = false
  }
}

// 选择报价
const handleSelectQuote = async (quote) => {
  try {
    await request.post('/dispatch/select-quote', {
      orderId: quoteForm.value.orderId,
      quoteId: quote.id
    })
    ElMessage.success('选择报价成功')
    quoteDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('选择报价失败')
  }
}

// 分配司机
const handleAssignDriver = async () => {
  try {
    const api = assignForm.value.type === 'pickup' ? '/dispatch/assign-pickup-driver' : '/dispatch/assign-delivery-driver'
    await request.post(api, { orderId: assignForm.value.orderId, driverId: assignForm.value.driverId })
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('分配失败')
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
</style>
