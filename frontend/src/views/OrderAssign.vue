<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单列表</span>
              <div>
                <el-button type="primary" :disabled="!selectedOrders.length" @click="handleBatchAssign">
                  批量分配 ({{ selectedOrders.length }})
                </el-button>
              </div>
            </div>
          </template>

          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="订单状态">
              <el-select v-model="filterForm.status" placeholder="全部" clearable style="width: 120px;">
                <el-option label="全部" :value="null"></el-option>
                <el-option label="待处理" :value="0"></el-option>
                <el-option label="已指派" :value="1"></el-option>
                <el-option label="配送中" :value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadOrders">筛选</el-button>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="orderLoading"
            :data="orderList"
            @selection-change="handleSelectionChange"
            style="width: 100%"
          >
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column prop="orderNo" label="订单号" width="150"></el-table-column>
            <el-table-column prop="senderName" label="发货人" width="100"></el-table-column>
            <el-table-column prop="senderPhone" label="电话" width="120"></el-table-column>
            <el-table-column prop="receiverName" label="收货人" width="100"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" min-width="150"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="info">待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="primary">已指派</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="warning">配送中</el-tag>
                <el-tag v-else-if="scope.row.status === 5" type="success">已完成</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button
                  v-if="!scope.row.driverId"
                  link
                  type="primary"
                  @click="handleSingleAssign(scope.row)"
                >
                  指派
                </el-button>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>推荐司机列表</span>
          </template>
          <div v-loading="driverLoading">
            <div v-for="driver in recommendedDrivers" :key="driver.driverId" class="driver-card">
              <div class="driver-info">
                <div class="driver-name">{{ driver.driverName }}</div>
                <div class="driver-phone">{{ driver.driverPhone }}</div>
              </div>
              <div class="driver-stats">
                <div class="stat-item">
                  <span class="stat-label">当前工作量</span>
                  <span class="stat-value">{{ driver.currentWorkload }}单</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">推荐指数</span>
                  <span class="stat-value score">{{ driver.score.toFixed(0) }}</span>
                </div>
              </div>
              <el-tag v-if="driver.status === 1" type="success" size="small">在线</el-tag>
            </div>
            <div v-if="recommendedDrivers.length === 0" style="text-align: center; padding: 20px; color: #909399;">
              暂无推荐司机
            </div>
          </div>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <span>分配历史</span>
          </template>
          <div v-loading="historyLoading">
            <div v-for="record in assignHistory" :key="record.id" class="history-item">
              <div class="history-time">{{ formatTime(record.createTime) }}</div>
              <div class="history-content">
                订单 <span class="order-no">{{ record.orderNo }}</span> 
                指派给 <span class="driver-name">{{ record.driverName }}</span>
              </div>
            </div>
            <div v-if="assignHistory.length === 0" style="text-align: center; padding: 20px; color: #909399;">
              暂无分配记录
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="assignDialogVisible" title="分配订单" width="500px">
      <div v-if="selectedDriver">
        <p>将 <strong>{{ selectedOrders.length }}</strong> 个订单分配给：</p>
        <div class="assign-driver-info">
          <div class="driver-name">{{ selectedDriver.driverName }}</div>
          <div class="driver-phone">{{ selectedDriver.driverPhone }}</div>
          <div class="driver-workload">当前工作量：{{ selectedDriver.currentWorkload }}单</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign" :loading="assignLoading">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const orderLoading = ref(false)
const driverLoading = ref(false)
const historyLoading = ref(false)
const assignLoading = ref(false)

const orderList = ref([])
const recommendedDrivers = ref([])
const assignHistory = ref([])
const selectedOrders = ref([])
const selectedDriver = ref(null)

const assignDialogVisible = ref(false)

const filterForm = ref({
  status: 0
})

const loadOrders = async () => {
  orderLoading.value = true
  try {
    const res = await request.get('/order/list', {
      params: { userId: 1, userType: 1 }
    })
    let orders = res.data || res || []
    if (filterForm.value.status !== null && filterForm.value.status !== undefined) {
      orders = orders.filter(o => o.status === filterForm.value.status || (filterForm.value.status === 0 && !o.driverId))
    }
    orderList.value = orders.filter(o => !o.driverId || o.status === 0 || o.status === 1)
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    orderLoading.value = false
  }
}

const loadRecommendedDrivers = async () => {
  driverLoading.value = true
  try {
    const res = await request.get('/order/recommend-drivers')
    recommendedDrivers.value = res.data || res || []
  } catch (error) {
    console.error('加载推荐司机失败:', error)
  } finally {
    driverLoading.value = false
  }
}

const loadAssignHistory = async () => {
  historyLoading.value = true
  try {
    const res = await request.get('/order/assign-history', { params: { limit: 10 } })
    assignHistory.value = res.data || res || []
  } catch (error) {
    console.error('加载分配历史失败:', error)
  } finally {
    historyLoading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

const handleSingleAssign = (order) => {
  selectedOrders.value = [order]
  loadRecommendedDrivers()
  if (recommendedDrivers.value.length > 0) {
    selectedDriver.value = recommendedDrivers.value[0]
    assignDialogVisible.value = true
  }
}

const handleBatchAssign = () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请选择要分配的订单')
    return
  }
  loadRecommendedDrivers()
  if (recommendedDrivers.value.length > 0) {
    selectedDriver.value = recommendedDrivers.value[0]
    assignDialogVisible.value = true
  }
}

const confirmAssign = async () => {
  if (!selectedDriver.value) {
    ElMessage.warning('请选择司机')
    return
  }
  
  assignLoading.value = true
  try {
    const orderIds = selectedOrders.value.map(o => o.id)
    await request.post('/order/batch-assign', {
      orderIds: orderIds,
      driverId: selectedDriver.value.driverId,
      operatorId: 1,
      operatorName: '管理员'
    })
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    selectedOrders.value = []
    loadOrders()
    loadAssignHistory()
    loadRecommendedDrivers()
  } catch (error) {
    console.error('分配失败:', error)
  } finally {
    assignLoading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

onMounted(() => {
  loadOrders()
  loadRecommendedDrivers()
  loadAssignHistory()
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
.driver-card {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.driver-info {
  flex: 1;
}
.driver-name {
  font-weight: bold;
  color: #303133;
}
.driver-phone {
  font-size: 12px;
  color: #909399;
}
.driver-stats {
  display: flex;
  gap: 15px;
}
.stat-item {
  text-align: center;
}
.stat-label {
  font-size: 12px;
  color: #909399;
  display: block;
}
.stat-value {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}
.stat-value.score {
  color: #67c23a;
}
.history-item {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}
.history-time {
  font-size: 12px;
  color: #909399;
}
.history-content {
  font-size: 13px;
  margin-top: 5px;
}
.order-no {
  color: #409eff;
}
.assign-driver-info {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  margin-top: 15px;
}
.assign-driver-info .driver-name {
  font-size: 18px;
  margin-bottom: 5px;
}
.assign-driver-info .driver-phone {
  font-size: 14px;
  margin-bottom: 10px;
}
.assign-driver-info .driver-workload {
  font-size: 13px;
  color: #909399;
}
</style>
