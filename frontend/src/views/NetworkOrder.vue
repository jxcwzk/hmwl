<template>
  <div class="network-page">
    <!-- 我的订单 -->
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <el-icon><Tickets /></el-icon>
          <span>我的订单</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="order-tabs">
        <!-- 报价 tab -->
        <el-tab-pane label="报价" name="quote">
          <el-table :data="quoteOrders" v-loading="loading" stripe>
            <el-table-column prop="orderNo" label="订单编号" min-width="150" />
            <el-table-column prop="senderName" label="发件人" min-width="100" />
            <el-table-column prop="receiverName" label="收件人" min-width="100" />
            <el-table-column prop="goodsName" label="货物" min-width="100" />
            <el-table-column prop="weight" label="重量(kg)" width="90" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleQuote(scope.row)">
                  报价
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 运输 tab -->
        <el-tab-pane label="运输" name="transport">
          <el-table :data="transportOrders" v-loading="loading" stripe>
            <el-table-column prop="orderNo" label="订单编号" min-width="150" />
            <el-table-column prop="senderName" label="发件人" min-width="100" />
            <el-table-column prop="receiverName" label="收件人" min-width="100" />
            <el-table-column prop="goodsName" label="货物" min-width="100" />
            <el-table-column prop="totalFee" label="费用" width="100" align="center">
              <template #default="scope">¥{{ scope.row.totalFee || '-' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120" align="center">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 2"
                  type="success"
                  size="small"
                  @click="handleConfirmReceive(scope.row)"
                >
                  确认揽收
                </el-button>
                <el-button
                  v-if="scope.row.status === 8"
                  type="warning"
                  size="small"
                  @click="handleConfirmReceive(scope.row)"
                >
                  确认收货
                </el-button>
                <el-button
                  v-if="scope.row.status === 9"
                  type="info"
                  size="small"
                  @click="handleView(scope.row)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 完成 tab -->
        <el-tab-pane label="完成" name="completed">
          <el-table :data="completedOrders" v-loading="loading" stripe>
            <el-table-column prop="orderNo" label="订单编号" min-width="150" />
            <el-table-column prop="senderName" label="发件人" min-width="100" />
            <el-table-column prop="receiverName" label="收件人" min-width="100" />
            <el-table-column prop="goodsName" label="货物" min-width="100" />
            <el-table-column prop="totalFee" label="费用" width="100" align="center">
              <template #default="scope">¥{{ scope.row.totalFee || '-' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="scope">
                <el-button type="info" size="small" @click="handleView(scope.row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 报价对话框 -->
    <el-dialog v-model="quoteDialogVisible" title="提交报价" width="400px">
      <el-form :model="quoteForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ quoteForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="发件人">
          <span>{{ quoteForm.senderName }}</span>
        </el-form-item>
        <el-form-item label="收件人">
          <span>{{ quoteForm.receiverName }}</span>
        </el-form-item>
        <el-form-item label="货物重量">
          <span>{{ quoteForm.weight }} kg</span>
        </el-form-item>
        <el-form-item label="底价" required>
          <el-input-number v-model="quoteForm.baseFee" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户报价" required>
          <el-input-number v-model="quoteForm.finalPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="运输天数" required>
          <el-input-number v-model="quoteForm.transitDays" :min="1" style="width: 100%" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const activeTab = ref('quote')

const quoteDialogVisible = ref(false)
const quoteForm = ref({})
const receiveDialogVisible = ref(false)
const receiveForm = ref({})

const getOrderList = async () => {
  loading.value = true
  try {
    const networkPointId = localStorage.getItem('networkId') || 1
    const res = await request.get('/order/network-list', {
      params: { networkPointId }
    })
    orderList.value = res.data || res || []
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const quoteOrders = computed(() => {
  return orderList.value.filter(order =>
    order.pricingStatus === 1 && order.status !== 10
  )
})

const transportOrders = computed(() => {
  return orderList.value.filter(order =>
    order.pricingStatus === 2 &&
    order.status >= 2 &&
    order.status < 10
  )
})

const completedOrders = computed(() => {
  return orderList.value.filter(order =>
    order.status === 10 ||
    order.status === 11 ||
    order.status === 12
  )
})

const getStatusType = (status) => {
  const map = {
    1: 'warning',
    2: 'primary',
    3: 'primary',
    4: 'primary',
    5: 'primary',
    6: 'primary',
    7: 'primary',
    8: 'warning',
    9: 'success',
    10: 'info',
    11: 'info',
    12: 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待付款',
    1: '待报价',
    2: '已付款',
    3: '已报价',
    4: '已揽收',
    5: '运输中',
    6: '到达目的',
    7: '目的网点签收',
    8: '待入库',
    9: '已入库',
    10: '派送中',
    11: '已签收',
    12: '已完成'
  }
  return map[status] || '未知'
}

const handleView = (row) => {
  ElMessage.info('查看订单: ' + row.orderNo)
}

const handleQuote = (row) => {
  quoteForm.value = {
    orderId: row.id,
    orderNo: row.orderNo,
    senderName: row.senderName,
    receiverName: row.receiverName,
    weight: row.weight,
    baseFee: row.baseFee || 100,
    finalPrice: row.totalFee || 150,
    transitDays: 3
  }
  quoteDialogVisible.value = true
}

const submitQuote = async () => {
  try {
    await request.post('/network/save-quote', {
      orderId: quoteForm.value.orderId,
      networkId: localStorage.getItem('networkId') || 1,
      baseFee: quoteForm.value.baseFee,
      finalPrice: quoteForm.value.finalPrice,
      transitDays: quoteForm.value.transitDays
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
.order-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}
.order-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}
</style>
