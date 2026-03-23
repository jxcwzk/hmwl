<template>
  <div class="order-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header-custom">
          <div class="header-left">
            <el-icon class="header-icon"><Tickets /></el-icon>
            <span class="header-title">司机订单管理</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待提货" name="6" />
        <el-tab-pane label="待配送" name="9" />
        <el-tab-pane label="配送中" name="11" />
      </el-tabs>

      <div class="table-container">
        <el-table v-loading="loading" :data="orderList" row-key="id">
          <el-table-column prop="orderNo" label="订单编号" min-width="150" />
          <el-table-column prop="senderName" label="发件人" min-width="100" />
          <el-table-column prop="receiverName" label="收件人" min-width="100" />
          <el-table-column prop="goodsName" label="货物" min-width="80" />
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
          <el-table-column label="操作" width="280" align="center" fixed="right">
            <template #default="scope">
              <template v-if="scope.row.status == 6">
                <el-button type="primary" size="small" @click="handleStartPickup(scope.row)">开始提货</el-button>
                <el-button type="success" size="small" @click="handleArrivedNetwork(scope.row)">送达网点</el-button>
              </template>
              <template v-else-if="scope.row.status == 9">
                <el-button type="primary" size="small" @click="handleAccept(scope.row)">接单</el-button>
              </template>
              <template v-else-if="scope.row.status == 11">
                <el-button type="warning" size="small" @click="handleTransit(scope.row)">运输中</el-button>
                <el-button type="success" size="small" @click="handleDelivering(scope.row)">派送中</el-button>
                <el-button type="danger" size="small" @click="handleSign(scope.row)">签收</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
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

const getOrderList = async () => {
  loading.value = true
  try {
    const driverId = localStorage.getItem('driverId') || 1
    const res = await request.get('/order/driver-list', { params: { driverId } })
    let orders = res.data || res || []
    if (activeTab.value !== 'all') {
      orders = orders.filter(o => o.status === parseInt(activeTab.value))
    }
    orderList.value = orders
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 6: 'info', 7: 'warning', 9: 'success', 11: 'primary', 12: 'warning', 13: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 6: '待提货', 7: '已提货', 8: '送达网点', 9: '待配送', 11: '运输中', 12: '派送中', 13: '已签收' }
  return map[status] || '未知'
}

const handleTabChange = () => {
  getOrderList()
}

const handleUpdateStatus = async (order, status, remark) => {
  try {
    await request.post('/order/driver/update-status', {
      orderId: order.id,
      status,
      remark
    })
    ElMessage.success('更新成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleStartPickup = (order) => handleUpdateStatus(order, 7, '已提货')
const handleArrivedNetwork = (order) => handleUpdateStatus(order, 8, '送达网点')
const handleAccept = (order) => handleUpdateStatus(order, 11, '已接单')
const handleTransit = (order) => handleUpdateStatus(order, 11, '运输中')
const handleDelivering = (order) => handleUpdateStatus(order, 12, '派送中')
const handleSign = (order) => handleUpdateStatus(order, 13, '已签收')

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
