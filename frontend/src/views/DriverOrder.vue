<template>
  <div class="driver-page">
    <!-- 我的订单 -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header-custom">
          <div class="header-left">
            <el-icon class="header-icon"><Tickets /></el-icon>
            <span class="header-title">我的订单</span>
          </div>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索订单编号"
              size="default"
              clearable
              style="width: 200px; margin-right: 10px;"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="order-tabs">
        <el-tab-pane name="unpick">
          <template #label>
            <span class="tab-label">
              未提货
              <el-badge :value="countMap.unpick" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="picking">
          <template #label>
            <span class="tab-label">
              待提货
              <el-badge :value="countMap.picking" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="transit">
          <template #label>
            <span class="tab-label">
              待发往网点
              <el-badge :value="countMap.transit" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <div class="table-container">
        <el-table :data="paginatedOrderList" v-loading="loading" stripe>
          <el-table-column prop="orderNo" label="订单编号" min-width="150" />
          <el-table-column prop="senderName" label="发件人" min-width="100" />
          <el-table-column prop="senderPhone" label="发件人电话" min-width="120" />
          <el-table-column prop="senderAddress" label="发件人地址" min-width="150" show-overflow-tooltip />
          <el-table-column prop="receiverName" label="收件人" min-width="100" />
          <el-table-column prop="receiverPhone" label="收件人电话" min-width="120" />
          <el-table-column prop="receiverAddress" label="收货地址" min-width="150" show-overflow-tooltip />
          <el-table-column prop="goodsName" label="货物" min-width="100" />
          <el-table-column prop="goodsWeight" label="重量" width="80">
            <template #default="scope">{{ scope.row.goodsWeight || scope.row.weight || 0 }} kg</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="140" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="220" align="center">
            <template #default="scope">
              <template v-if="scope.row.status === 9">
                <el-button type="primary" size="small" @click="handleUpdateStatus(scope.row, 7, '接单')">接单</el-button>
              </template>
              <template v-else-if="scope.row.status === 7">
                <el-button type="success" size="small" @click="openPickupDialog(scope.row)">拍照确认提货</el-button>
              </template>
              <template v-else-if="scope.row.status === 8">
                <el-button type="warning" size="small" @click="openArrivalDialog(scope.row)">拍照确认送达网点</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 拍照确认提货对话框 -->
    <el-dialog v-model="pickupDialogVisible" title="拍照确认提货" width="500px">
      <el-form label-width="80px">
        <el-form-item label="订单号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="发件人">
          <span>{{ currentOrder?.senderName }} - {{ currentOrder?.senderPhone }}</span>
        </el-form-item>
        <el-form-item label="发件地址">
          <span>{{ currentOrder?.senderAddress }}</span>
        </el-form-item>
        <el-form-item label="提货凭证">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            list-type="picture"
            accept="image/*"
          >
            <el-button type="primary">选择照片</el-button>
            <template #tip>
              <div class="upload-tip">请上传货物照片作为提货凭证</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pickupDialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleConfirmPickup" :loading="uploadLoading">确认提货</el-button>
      </template>
    </el-dialog>

    <!-- 拍照确认送达网点对话框 -->
    <el-dialog v-model="arrivalDialogVisible" title="拍照确认送达网点" width="500px">
      <el-form label-width="80px">
        <el-form-item label="订单号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="收货人">
          <span>{{ currentOrder?.receiverName }} - {{ currentOrder?.receiverPhone }}</span>
        </el-form-item>
        <el-form-item label="收货地址">
          <span>{{ currentOrder?.receiverAddress }}</span>
        </el-form-item>
        <el-form-item label="送达凭证">
          <el-upload
            ref="arrivalUploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleArrivalFileChange"
            :on-remove="handleArrivalFileRemove"
            list-type="picture"
            accept="image/*"
          >
            <el-button type="primary">选择照片</el-button>
            <template #tip>
              <div class="upload-tip">请上传送达网点照片作为凭证</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="arrivalDialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleConfirmArrival" :loading="uploadLoading">确认送达网点</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Search } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const activeTab = ref('unpick')
const currentPage = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')

// 拍照确认提货
const pickupDialogVisible = ref(false)
const uploadRef = ref(null)
const selectedFile = ref(null)

// 拍照确认送达网点
const arrivalDialogVisible = ref(false)
const arrivalUploadRef = ref(null)
const selectedArrivalFile = ref(null)

const currentOrder = ref(null)
const uploadLoading = ref(false)

const countMap = computed(() => {
  const map = { unpick: 0, picking: 0, transit: 0 }
  orderList.value.forEach(order => {
    if (order.status === 9) {
      map.unpick++
    } else if (order.status === 7) {
      map.picking++
    } else if (order.status === 8 || order.status === 4) {
      map.transit++
    }
  })
  return map
})

const filteredOrderList = computed(() => {
  let filtered = orderList.value
  if (searchKeyword.value) {
    filtered = filtered.filter(o => o.orderNo && o.orderNo.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  }
  if (activeTab.value === 'unpick') {
    filtered = filtered.filter(o => o.status === 9)
  } else if (activeTab.value === 'picking') {
    filtered = filtered.filter(o => o.status === 7)
  } else if (activeTab.value === 'transit') {
    filtered = filtered.filter(o => o.status === 8 || o.status === 4)
  }
  return filtered
})

const total = computed(() => filteredOrderList.value.length)

const paginatedOrderList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrderList.value.slice(start, end)
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

const handleSearch = () => {
  currentPage.value = 1
}

const handleTabChange = () => {
  currentPage.value = 1
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const getStatusType = (status) => {
  const map = { 9: 'info', 7: 'warning', 8: 'primary', 4: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    9: '待接单', 
    7: '待提货', 
    8: '待发往网点',
    4: '已到达网点'
  }
  return map[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

// 拍照确认提货
const openPickupDialog = (order) => {
  currentOrder.value = order
  selectedFile.value = null
  pickupDialogVisible.value = true
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleFileRemove = () => {
  selectedFile.value = null
}

const handleConfirmPickup = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先上传提货照片')
    return
  }
  
  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('orderId', currentOrder.value.id)
    formData.append('orderNo', currentOrder.value.orderNo)
    
    await request.post('/order/sender-image/upload/cos', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
    await request.post('/order/driver/update-status', {
      orderId: currentOrder.value.id,
      status: 8,
      remark: '已拍照确认提货'
    })
    
    ElMessage.success('提货确认成功')
    pickupDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('操作失败：' + (error.message || '请重试'))
  } finally {
    uploadLoading.value = false
  }
}

// 拍照确认送达网点
const openArrivalDialog = (order) => {
  currentOrder.value = order
  selectedArrivalFile.value = null
  arrivalDialogVisible.value = true
}

const handleArrivalFileChange = (file) => {
  selectedArrivalFile.value = file.raw
}

const handleArrivalFileRemove = () => {
  selectedArrivalFile.value = null
}

const handleConfirmArrival = async () => {
  if (!selectedArrivalFile.value) {
    ElMessage.warning('请先上传送达网点照片')
    return
  }
  
  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedArrivalFile.value)
    formData.append('orderId', currentOrder.value.id)
    formData.append('orderNo', currentOrder.value.orderNo)
    
    await request.post('/order/sender-image/upload/cos', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
    await request.post('/order/driver/update-status', {
      orderId: currentOrder.value.id,
      status: 4,
      remark: '已拍照确认送达网点'
    })
    
    ElMessage.success('送达网点确认成功')
    arrivalDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('操作失败：' + (error.message || '请重试'))
  } finally {
    uploadLoading.value = false
  }
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
.order-tabs {
  margin-bottom: 16px;
}
.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}
.tab-badge {
  margin-left: 4px;
}
.table-container {
  min-height: 300px;
}
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>