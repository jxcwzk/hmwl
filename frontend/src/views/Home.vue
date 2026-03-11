<template>
  <div class="home-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card created-card" shadow="hover" @click="goToOrders(0)">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.created }}</div>
              <div class="stat-label">已创建</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card processing-card" shadow="hover" @click="goToOrders(1)">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.processing }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card completed-card" shadow="hover" @click="goToOrders(2)">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card total-card" shadow="hover" @click="goToOrders(null)">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">总订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="quick-entry-row">
      <el-col :span="8">
        <el-card class="quick-card" shadow="hover" @click="goToOrder">
          <div class="quick-content">
            <el-icon :size="32" color="#409EFF"><Plus /></el-icon>
            <span>快速下单</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="quick-card" shadow="hover" @click="goToOrders(1)">
          <div class="quick-content">
            <el-icon :size="32" color="#E6A23C"><Warning /></el-icon>
            <span>待处理订单</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="quick-card" shadow="hover" @click="goToNetworkPoint">
          <div class="quick-content">
            <el-icon :size="32" color="#67C23A"><OfficeBuilding /></el-icon>
            <span>网点管理</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="recent-row">
      <el-col :span="24">
        <el-card class="recent-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
              <el-button type="primary" link @click="goToOrders(null)">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%" max-height="250">
            <el-table-column prop="orderNo" label="订单编号" width="180"></el-table-column>
            <el-table-column prop="senderName" label="发件人" width="120"></el-table-column>
            <el-table-column prop="recipientName" label="收件人" width="120"></el-table-column>
            <el-table-column prop="goodsName" label="货物名称" width="150"></el-table-column>
            <el-table-column prop="totalFee" label="费用" width="100">
              <template #default="scope">
                ¥{{ scope.row.totalFee || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间">
              <template #default="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { Document, Loading, CircleCheck, DataAnalysis, Plus, Warning, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()

const stats = ref({
  created: 0,
  processing: 0,
  completed: 0,
  total: 0
})

const recentOrders = ref([])

const getOrderStats = async () => {
  try {
    const res = await request.get('/order/list')
    const orders = res.data || res || []
    stats.value.total = orders.length
    stats.value.created = orders.filter(o => o.status === 0).length
    stats.value.processing = orders.filter(o => o.status === 1).length
    stats.value.completed = orders.filter(o => o.status === 2).length
    
    recentOrders.value = orders.slice(0, 10)
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['已创建', '进行中', '已完成', '已取消']
  return texts[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

const goToOrders = (status) => {
  if (status !== null && status !== undefined) {
    router.push({ path: '/order', query: { status } })
  } else {
    router.push('/order')
  }
}

const goToOrder = () => {
  router.push('/order')
}

const goToNetworkPoint = () => {
  router.push('/network-point')
}

onMounted(() => {
  getOrderStats()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  margin-right: 20px;
}

.created-card .stat-icon {
  color: #909399;
}

.processing-card .stat-icon {
  color: #E6A23C;
}

.completed-card .stat-icon {
  color: #67C23A;
}

.total-card .stat-icon {
  color: #409EFF;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.quick-entry-row {
  margin-bottom: 20px;
}

.quick-card {
  cursor: pointer;
}

.quick-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  gap: 10px;
}

.quick-content span {
  font-size: 16px;
  color: #606266;
}

.recent-row .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
