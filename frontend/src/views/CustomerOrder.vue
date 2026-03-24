<template>
  <div class="customer-page">
    <!-- 我的订单 -->
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><Tickets /></el-icon>
            <span>我的订单</span>
          </div>
          <div class="header-right">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入订单编号搜索"
              size="small"
              clearable
              @keyup.enter="handleSearch"
              style="width: 200px; margin-right: 10px;"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" size="small" @click="handleAddOrder">
              <el-icon><Plus /></el-icon>新增订单
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredOrderList" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" min-width="150" />
        <el-table-column prop="senderName" label="发件人" min-width="100" />
        <el-table-column prop="receiverName" label="收件人" min-width="100" />
        <el-table-column prop="goodsName" label="货物" min-width="100" />
        <el-table-column prop="totalFee" label="费用" width="100">
          <template #default="scope">¥{{ scope.row.totalFee || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
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
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="700px">
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

    <!-- 新增订单对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增订单" width="600px">
      <el-form :model="addForm" label-width="100px">
        <el-divider content-position="left">发件人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发件人">
              <el-input v-model="addForm.senderName" placeholder="请输入发件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发件人电话">
              <el-input v-model="addForm.senderPhone" placeholder="请输入发件人电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发件人地址">
          <el-input v-model="addForm.senderAddress" placeholder="请输入发件人地址" />
        </el-form-item>

        <el-divider content-position="left">收件人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收件人">
              <el-input v-model="addForm.receiverName" placeholder="请输入收件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收件人电话">
              <el-input v-model="addForm.receiverPhone" placeholder="请输入收件人电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="收件人地址">
          <el-input v-model="addForm.receiverAddress" placeholder="请输入收件人地址" />
        </el-form-item>

        <el-divider content-position="left">货物信息</el-divider>
        <el-form-item label="货物名称">
          <el-input v-model="addForm.goodsName" placeholder="请输入货物名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="重量(kg)">
              <el-input-number v-model="addForm.weight" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体积(m³)">
              <el-input-number v-model="addForm.volume" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数量">
              <el-input-number v-model="addForm.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款方式">
              <el-select v-model="addForm.paymentMethod" style="width: 100%">
                <el-option label="现结" :value="0" />
                <el-option label="月结" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddOrder" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Plus, Search } from '@element-plus/icons-vue'
import request from '../utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])
const searchKeyword = ref('')
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const addDialogVisible = ref(false)
const submitLoading = ref(false)

const addForm = ref({
  senderName: '',
  senderPhone: '',
  senderAddress: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  goodsName: '',
  weight: 1,
  volume: 0.01,
  quantity: 1,
  paymentMethod: 0
})

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
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const filteredOrderList = computed(() => {
  if (!searchKeyword.value) return orderList.value
  return orderList.value.filter(order => 
    order.orderNo && order.orderNo.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const handleSearch = () => {
  // 搜索通过computed自动完成
}

const getStatusType = (status) => {
  const map = { 0: 'info', 4: 'warning', 5: 'success', 11: 'primary', 13: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待处理', 4: '待确认价格', 5: '价格已确认', 6: '待提货',
    7: '已提货', 8: '送达网点', 9: '网点已收货', 11: '配送中', 13: '已签收'
  }
  return map[status] || '未知'
}

const handleAddOrder = () => {
  addForm.value = {
    senderName: '',
    senderPhone: '',
    senderAddress: '',
    receiverName: '',
    receiverPhone: '',
    receiverAddress: '',
    goodsName: '',
    weight: 1,
    volume: 0.01,
    quantity: 1,
    paymentMethod: 0
  }
  addDialogVisible.value = true
}

const submitAddOrder = async () => {
  if (!addForm.value.senderName || !addForm.value.receiverName) {
    ElMessage.warning('请填写发件人和收件人信息')
    return
  }
  if (!addForm.value.senderPhone || !addForm.value.receiverPhone) {
    ElMessage.warning('请填写发件人和收件人电话')
    return
  }
  if (!addForm.value.senderAddress || !addForm.value.receiverAddress) {
    ElMessage.warning('请填写发件人和收件人地址')
    return
  }
  if (!addForm.value.goodsName) {
    ElMessage.warning('请填写货物名称')
    return
  }

  submitLoading.value = true
  try {
    const businessUserId = localStorage.getItem('businessUserId') || 1
    await request.post('/order', {
      ...addForm.value,
      businessUserId: Number(businessUserId),
      status: 0,
      pricingStatus: 0
    })
    ElMessage.success('订单创建成功')
    addDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('订单创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleView = (row) => {
  currentOrder.value = row
  detailDialogVisible.value = true
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
.customer-page {
  padding: 20px;
}
.order-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}
.header-right {
  display: flex;
  align-items: center;
}
</style>
