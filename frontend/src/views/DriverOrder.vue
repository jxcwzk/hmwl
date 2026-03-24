<template>
  <div class="driver-page">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>司机信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ driverInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="工号">{{ driverInfo.code }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ driverInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ driverInfo.vehicleNo }}</el-descriptions-item>
        <el-descriptions-item label="车型" :span="2">{{ driverInfo.vehicleType }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 我的订单 -->
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <el-icon><Tickets /></el-icon>
          <span>我的订单</span>
        </div>
      </template>

      <el-table :data="orderList" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" min-width="150" />
        <el-table-column prop="senderName" label="发件人" min-width="100" />
        <el-table-column prop="receiverName" label="收件人" min-width="100" />
        <el-table-column prop="receiverAddress" label="收货地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="goodsName" label="货物" min-width="100" />
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template #default="scope">
            <template v-if="scope.row.status === 6">
              <el-button type="primary" size="small" @click="handleUpdateStatus(scope.row, 7, '已提货')">开始提货</el-button>
              <el-button type="success" size="small" @click="handleUpdateStatus(scope.row, 8, '送达网点')">送达网点</el-button>
            </template>
            <template v-else-if="scope.row.status === 9">
              <el-button type="primary" size="small" @click="handleUpdateStatus(scope.row, 11, '已接单')">接单</el-button>
            </template>
            <template v-else-if="scope.row.status === 11">
              <el-button type="warning" size="small" @click="handleUpdateStatus(scope.row, 11, '运输中')">运输中</el-button>
              <el-button type="success" size="small" @click="handleUpdateStatus(scope.row, 12, '派送中')">派送中</el-button>
              <el-button type="danger" size="small" @click="handleUpdateStatus(scope.row, 13, '已签收')">签收</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Tickets } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const driverInfo = ref({
  name: '司机张三',
  code: 'D001',
  phone: '13800138002',
  vehicleNo: '京A12345',
  vehicleType: '中型货车'
})

const getOrderList = async () => {
  loading.value = true
  try {
    const driverId = localStorage.getItem('driverId') || 1
    const res = await request.get('/order/driver-list', {
      params: { driverId }
    })
    orderList.value = res.data || res || []
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 6: 'info', 7: 'warning', 9: 'success', 11: 'primary', 12: 'warning', 13: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    6: '待提货', 7: '已提货', 8: '送达网点', 9: '待配送',
    11: '配送中', 12: '派送中', 13: '已签收'
  }
  return map[status] || '未知'
}

const handleUpdateStatus = async (row, status, remark) => {
  try {
    await request.post('/order/driver/update-status', {
      orderId: row.id,
      status,
      remark
    })
    ElMessage.success('状态更新成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.driver-page {
  padding: 20px;
}
.profile-card {
  margin-bottom: 20px;
}
.order-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}
</style>
