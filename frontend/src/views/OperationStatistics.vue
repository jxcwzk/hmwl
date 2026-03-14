<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>运营报表</span>
              <el-button type="primary" @click="refreshData">刷新数据</el-button>
            </div>
          </template>

          <el-form :inline="true" class="filter-form">
            <el-form-item label="统计日期">
              <el-date-picker
                v-model="selectedDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                @change="loadDailyStats"
              ></el-date-picker>
            </el-form-item>
          </el-form>

          <el-row :gutter="20" v-loading="statsLoading">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">今日订单</div>
                <div class="stat-value">{{ dailyStats.totalOrders || 0 }}</div>
                <div class="stat-comparison">
                  <span :class="dailyStats.dayOverDayGrowth >= 0 ? 'up' : 'down'">
                    {{ dailyStats.dayOverDayGrowth >= 0 ? '↑' : '↓' }}
                    {{ Math.abs(dailyStats.dayOverDayGrowth || 0) }}%
                  </span>
                  较昨日
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">配送中</div>
                <div class="stat-value">{{ dailyStats.deliveringOrders || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">已完成</div>
                <div class="stat-value">{{ dailyStats.completedOrders || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">今日收入</div>
                <div class="stat-value">¥{{ (dailyStats.totalAmount || 0).toFixed(2) }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>司机工作量排名</span>
          </template>
          <el-radio-group v-model="driverRankingType" @change="loadDriverRanking">
            <el-radio label="order_count">订单量</el-radio>
            <el-radio label="distance">配送里程</el-radio>
            <el-radio label="amount">配送收入</el-radio>
          </el-radio-group>
          <el-table :data="driverRanking" v-loading="driverLoading" style="margin-top: 20px;">
            <el-table-column type="index" label="排名" width="60"></el-table-column>
            <el-table-column prop="driverName" label="司机"></el-table-column>
            <el-table-column prop="orderCount" label="订单量"></el-table-column>
            <el-table-column prop="totalDistance" label="配送里程(km)">
              <template #default="scope">
                {{ (scope.row.totalDistance || 0).toFixed(1) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="配送收入">
              <template #default="scope">
                ¥{{ (scope.row.totalAmount || 0).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>客户订单排名</span>
          </template>
          <el-radio-group v-model="customerRankingType" @change="loadCustomerRanking">
            <el-radio label="order_count">订单量</el-radio>
            <el-radio label="amount">订单金额</el-radio>
          </el-radio-group>
          <el-table :data="customerRanking" v-loading="customerLoading" style="margin-top: 20px;">
            <el-table-column type="index" label="排名" width="60"></el-table-column>
            <el-table-column prop="businessUserId" label="客户ID"></el-table-column>
            <el-table-column prop="orderCount" label="订单量"></el-table-column>
            <el-table-column prop="totalAmount" label="订单金额">
              <template #default="scope">
                ¥{{ (scope.row.totalAmount || 0).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>收入趋势</span>
          </template>
          <el-radio-group v-model="revenuePeriod" @change="loadRevenueTrend">
            <el-radio label="daily">最近30天</el-radio>
            <el-radio label="weekly">最近7周</el-radio>
            <el-radio label="monthly">最近30天</el-radio>
          </el-radio-group>
          <div v-loading="revenueLoading" style="margin-top: 20px; height: 300px;">
            <div v-if="revenueTrend.length === 0" style="text-align: center; padding: 50px;">
              暂无数据
            </div>
            <div v-else class="revenue-chart">
              <div class="chart-bars">
                <div v-for="(item, index) in revenueTrend" :key="index" class="chart-bar-wrapper">
                  <div class="chart-bar" :style="{ height: getBarHeight(item.totalAmount) + '%' }">
                    <span class="bar-value">¥{{ (item.totalAmount || 0).toFixed(0) }}</span>
                  </div>
                  <span class="bar-label">{{ formatDate(item.date) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const selectedDate = ref(new Date().toISOString().split('T')[0])
const statsLoading = ref(false)
const driverLoading = ref(false)
const customerLoading = ref(false)
const revenueLoading = ref(false)

const dailyStats = ref({})
const driverRanking = ref([])
const customerRanking = ref([])
const revenueTrend = ref([])

const driverRankingType = ref('order_count')
const customerRankingType = ref('order_count')
const revenuePeriod = ref('daily')

const maxRevenue = ref(0)

const loadDailyStats = async () => {
  statsLoading.value = true
  try {
    const res = await request.get('/statistics/daily', { params: { date: selectedDate.value } })
    dailyStats.value = res.statistics || {}
  } catch (error) {
    console.error('加载每日统计失败:', error)
  } finally {
    statsLoading.value = false
  }
}

const loadDriverRanking = async () => {
  driverLoading.value = true
  try {
    const res = await request.get('/statistics/driver-ranking', { params: { type: driverRankingType.value } })
    driverRanking.value = res.ranking || []
  } catch (error) {
    console.error('加载司机排名失败:', error)
  } finally {
    driverLoading.value = false
  }
}

const loadCustomerRanking = async () => {
  customerLoading.value = true
  try {
    const res = await request.get('/statistics/customer-ranking', { params: { type: customerRankingType.value } })
    customerRanking.value = res.ranking || []
  } catch (error) {
    console.error('加载客户排名失败:', error)
  } finally {
    customerLoading.value = false
  }
}

const loadRevenueTrend = async () => {
  revenueLoading.value = true
  try {
    const res = await request.get('/statistics/revenue', { params: { period: revenuePeriod.value } })
    revenueTrend.value = res.trendData || []
    maxRevenue.value = Math.max(...(revenueTrend.value.map(item => item.totalAmount || 0)), 1)
  } catch (error) {
    console.error('加载收入趋势失败:', error)
  } finally {
    revenueLoading.value = false
  }
}

const refreshData = async () => {
  try {
    await request.get('/statistics/daily/refresh', { params: { date: selectedDate.value } })
    ElMessage.success('数据已刷新')
    loadDailyStats()
    loadDriverRanking()
    loadCustomerRanking()
    loadRevenueTrend()
  } catch (error) {
    console.error('刷新数据失败:', error)
  }
}

const getBarHeight = (value) => {
  if (!value || maxRevenue.value === 0) return 0
  return (value / maxRevenue.value) * 80
}

const formatDate = (date) => {
  if (!date) return ''
  return date.substring(5)
}

onMounted(() => {
  loadDailyStats()
  loadDriverRanking()
  loadCustomerRanking()
  loadRevenueTrend()
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
.stat-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-comparison {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
.stat-comparison .up {
  color: #67c23a;
}
.stat-comparison .down {
  color: #f56c6c;
}
.revenue-chart {
  height: 280px;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding: 10px 0;
}
.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  max-width: 40px;
}
.chart-bar {
  width: 30px;
  background: linear-gradient(to top, #409eff, #66b1ff);
  border-radius: 4px 4px 0 0;
  min-height: 4px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow: hidden;
}
.bar-value {
  font-size: 10px;
  color: #fff;
  padding-top: 2px;
}
.bar-label {
  font-size: 10px;
  color: #909399;
  margin-top: 5px;
  transform: rotate(-45deg);
  white-space: nowrap;
}
</style>
