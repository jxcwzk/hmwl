<template>
  <div class="network-page">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <el-icon><OfficeBuilding /></el-icon>
          <span>网点信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="网点名称">{{ networkInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="网点编号">{{ networkInfo.code }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ networkInfo.manager }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ networkInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ networkInfo.address }}</el-descriptions-item>
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
              v-if="scope.row.pricingStatus === 1" 
              type="success" 
              size="small" 
              @click="handleQuote(scope.row)"
            >
              报价
            </el-button>
            <el-button 
              v-if="scope.row.status === 8" 
              type="warning" 
              size="small" 
              @click="handleConfirmReceive(scope.row)"
            >
              确认收货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 报价对话框 -->
    <el-dialog v-model="quoteDialogVisible" title="提交报价" width="400px">
      <el-form :model="quoteForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ quoteForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="底价" required>
          <el-input-number v-model="quoteForm.baseFee" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户报价" required>
          <el-input-number v-model="quoteForm.customerPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="运输天数" required>
          <el-input-number v-model="quoteForm.deliveryDays" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="quoteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitQuote">提交</el-button>
      </template>
    </el-dialog>

    <!-- 确认收货对话框 -->
    <el-dialog v-model="receiveDialogVisible" title="确认收货" width="400px">
      <el-form :model="receiveForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ receiveForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="检查结果">
          <el-radio-group v-model="receiveForm.checkResult">
            <el-radio label="ok">正常</el-radio>
            <el-radio label="damaged">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="receiveForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="receiveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReceive">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, Tickets } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const networkInfo = ref({
  name: '北京网点',
  code: 'NP001',
  manager: '张三',
  phone: '13800138001',
  address: '北京市朝阳区建国路88号'
})

const quoteDialogVisible = ref(false)
const quoteForm = ref({})
const receiveDialogVisible = ref(false)
const receiveForm = ref({})

const getOrderList = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/list', {
      params: {
        networkId: localStorage.getItem('networkId') || 1
      }
    })
    orderList.value = res.data || res || []
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 1: 'warning', 8: 'info', 9: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 1: '待报价', 8: '待入库', 9: '已入库' }
  return map[status] || '未知'
}

const handleView = (row) => {
  ElMessage.info('查看订单: ' + row.orderNo)
}

const handleQuote = (row) => {
  quoteForm.value = {
    orderId: row.id,
    orderNo: row.orderNo,
    baseFee: 100,
    customerPrice: 150,
    deliveryDays: 2
  }
  quoteDialogVisible.value = true
}

const submitQuote = async () => {
  try {
    await request.post('/network-quote', {
      orderId: quoteForm.value.orderId,
      baseFee: quoteForm.value.baseFee,
      finalPrice: quoteForm.value.customerPrice,
      deliveryDays: quoteForm.value.deliveryDays
    })
    ElMessage.success('报价成功')
    quoteDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('报价失败')
  }
}

const handleConfirmReceive = (row) => {
  receiveForm.value = {
    orderId: row.id,
    orderNo: row.orderNo,
    checkResult: 'ok',
    remark: ''
  }
  receiveDialogVisible.value = true
}

const submitReceive = async () => {
  try {
    await request.post('/network/confirm-receive', {
      orderId: receiveForm.value.orderId,
      networkId: 1,
      checkResult: receiveForm.value.checkResult,
      remark: receiveForm.value.remark
    })
    ElMessage.success('确认收货成功')
    receiveDialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('确认收货失败')
  }
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.network-page {
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
