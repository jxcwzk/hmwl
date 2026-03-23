<template>
  <div class="order-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header-custom">
          <div class="header-left">
            <el-icon class="header-icon"><Tickets /></el-icon>
            <span class="header-title">我的订单</span>
          </div>
          <div class="header-actions">
            <el-button class="action-btn primary-btn" type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增订单
            </el-button>
          </div>
        </div>
      </template>

      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="" class="search-item">
            <el-input
              v-model="searchForm.orderNo"
              placeholder="请输入订单编号搜索"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="search-btn-item">
            <el-button type="primary" class="search-btn" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="orderList"
          class="order-table"
          row-key="id"
        >
          <el-table-column prop="orderNo" label="订单编号" min-width="160">
            <template #default="scope">
              <span class="order-no">{{ scope.row.orderNo || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="senderName" label="发件人" min-width="120"></el-table-column>
          <el-table-column prop="receiverName" label="收件人" min-width="120"></el-table-column>
          <el-table-column prop="goodsName" label="货物" min-width="100"></el-table-column>
          <el-table-column prop="totalFee" label="费用" width="100" align="right">
            <template #default="scope">
              <span class="fee-amount">¥{{ scope.row.totalFee || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="140" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleView(scope.row)">
                查看详情
              </el-button>
              <el-button
                v-if="scope.row.status === 4"
                type="success"
                size="small"
                @click="handleConfirmPrice(scope.row)"
              >
                确认价格
              </el-button>
              <el-button
                v-if="scope.row.status === 11"
                type="success"
                size="small"
                @click="handleConfirmSign(scope.row)"
              >
                确认签收
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container">
        <div class="pagination-info">
          共 <span class="total-count">{{ total }}</span> 条记录
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="sizes, prev, pager, next"
          :total="total"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="订单详情" width="800px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发件人">{{ currentOrder.senderName }}</el-descriptions-item>
        <el-descriptions-item label="发件人电话">{{ currentOrder.senderPhone }}</el-descriptions-item>
        <el-descriptions-item label="发件人地址" :span="2">{{ currentOrder.senderAddress }}</el-descriptions-item>
        <el-descriptions-item label="收件人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="收件人电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收件人地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="货物名称">{{ currentOrder.goodsName }}</el-descriptions-item>
        <el-descriptions-item label="重量">{{ currentOrder.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="体积">{{ currentOrder.volume }} m³</el-descriptions-item>
        <el-descriptions-item label="费用">¥{{ currentOrder.totalFee || 0 }}</el-descriptions-item>
        <el-descriptions-item label="物流进度" :span="2">{{ currentOrder.logisticsProgress || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Plus, Search } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = ref({})
const dialogVisible = ref(false)
const currentOrder = ref(null)

const getOrderList = async () => {
  loading.value = true
  try {
    const businessUserId = localStorage.getItem('businessUserId') || 1
    const res = await request.get('/order/list', {
      params: {
        userId: localStorage.getItem('userId'),
        userType: 2,
        businessUserId: businessUserId
      }
    })
    orderList.value = res.data || res || []
    total.value = orderList.value.length
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 0: 'info', 4: 'warning', 5: 'success', 11: 'primary', 13: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待处理',
    1: '已确认',
    4: '待确认价格',
    5: '价格已确认',
    6: '待提货',
    7: '已提货',
    8: '送达网点',
    9: '网点已收货',
    11: '配送中',
    12: '派送中',
    13: '已签收'
  }
  return map[status] || '未知'
}

const handleSearch = () => {
  currentPage.value = 1
  getOrderList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getOrderList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getOrderList()
}

const handleAdd = () => {
  window.location.href = '/order'
}

const handleView = (row) => {
  currentOrder.value = row
  dialogVisible.value = true
}

const handleConfirmPrice = async (row) => {
  try {
    await request.post('/order/confirm-price', { orderId: row.id })
    ElMessage.success('价格已确认')
    getOrderList()
  } catch (error) {
    ElMessage.error('确认失败')
  }
}

const handleConfirmSign = async (row) => {
  try {
    await request.put('/order', { id: row.id, status: 13, logisticsProgress: '客户已签收' })
    ElMessage.success('签收成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('签收失败')
  }
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-page {
  padding: var(--spacing-lg);
  min-height: calc(100vh - 120px);
}
.main-card {
  border-radius: var(--radius-lg);
  border: none;
  box-shadow: var(--shadow-md);
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
.header-actions {
  display: flex;
  gap: var(--spacing-sm);
}
.primary-btn {
  background: var(--color-primary);
  border: none;
}
.search-section {
  padding: var(--spacing-lg);
  background: var(--color-surface-secondary);
  border-bottom: 1px solid var(--color-separator);
}
.search-form {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}
.search-item {
  flex: 1;
  max-width: 400px;
}
.search-btn {
  background: var(--color-primary);
  border: none;
}
.table-container {
  padding: var(--spacing-md);
}
.order-no {
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 13px;
  font-weight: 500;
}
.fee-amount {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-danger);
}
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--color-separator);
}
.pagination-info {
  font-size: 14px;
  color: var(--color-text-secondary);
}
.total-count {
  font-weight: 600;
}
</style>
