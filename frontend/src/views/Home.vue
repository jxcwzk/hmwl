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
    const res = await request.get('/order/list', {
      params: {
        userId: 1,
        userType: 1,
        businessUserId: 1
      }
    })
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
  padding: var(--spacing-lg);
}

.stats-row {
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  cursor: pointer;
  transition: all var(--transition-normal);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  background: var(--color-surface);
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: var(--spacing-sm);
  width: 100%;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--spacing-md);
  flex-shrink: 0;
}

.created-card .stat-icon {
  background: rgba(0, 122, 255, 0.1);
  color: var(--color-primary);
}

.processing-card .stat-icon {
  background: rgba(255, 149, 0, 0.1);
  color: var(--color-warning);
}

.completed-card .stat-icon {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

.total-card .stat-icon {
  background: rgba(90, 200, 250, 0.1);
  color: var(--color-info);
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.quick-entry-row {
  margin-bottom: var(--spacing-lg);
}

.quick-card {
  cursor: pointer;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  transition: all var(--transition-normal);
}

.quick-card:hover {
  transform: translateY(-2px);
}

.quick-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg);
  gap: var(--spacing-sm);
}

.quick-content span {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-primary);
}

.recent-row .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  border: none;
  font-weight: 600;
  font-size: 17px;
  color: var(--color-text-primary);
}

:deep(.el-card) {
  border-radius: var(--radius-lg);
  border: none;
}

:deep(.el-table) {
  border-radius: var(--radius-md);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  background: var(--color-surface-secondary) !important;
  font-weight: 600;
  color: var(--color-text-secondary);
}

:deep(.el-tag) {
  border-radius: var(--radius-full);
  border: none;
  font-weight: 500;
}

@media (max-width: 768px) {
  .home-container {
    padding: var(--spacing-md);
  }
  
  .stat-icon {
    width: 44px;
    height: 44px;
  }
  
  .stat-value {
    font-size: 22px;
  }
}
</style>
