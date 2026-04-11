<template>
  <div class="home-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card created-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card processing-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOrders }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card completed-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.monthOrders }}</div>
              <div class="stat-label">本月订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card total-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.monthRevenue }}</div>
              <div class="stat-label">本月营收</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { Document, Loading, CircleCheck, Money } from '@element-plus/icons-vue'

const stats = ref({
  todayOrders: 0,
  pendingOrders: 0,
  monthOrders: 0,
  monthRevenue: '0.00'
})

const getNetworkStats = async () => {
  try {
    const networkId = localStorage.getItem('networkId')
    if (networkId) {
      const res = await request.get('/network/stats', {
        params: { networkId }
      })
      if (res) {
        stats.value = res
      }
    }
  } catch (error) {
    console.error('获取网点统计失败:', error)
  }
}

onMounted(() => {
  const userType = localStorage.getItem('userType')
  if (userType === '4') {
    getNetworkStats()
  }
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

:deep(.el-card) {
  border-radius: var(--radius-lg);
  border: none;
}
</style>
