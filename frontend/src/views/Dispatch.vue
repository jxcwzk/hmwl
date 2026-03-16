<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>调度管理 - 报价选择</span>
          <el-button type="primary" @click="loadOrders">刷新数据</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待确认报价" name="pending">
          <el-table v-loading="loading" :data="pendingOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="senderAddress" label="发货地址" width="200"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="货物" width="100"></el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="80"></el-table-column>
            <el-table-column label="报价数量" width="100">
              <template #default="scope">
                <el-tag>{{ getQuoteCount(scope.row.id) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showQuotes(scope.row)">
                  查看报价
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已确认报价" name="confirmed">
          <el-table v-loading="loading" :data="confirmedOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="totalFee" label="确认价格" width="100">
              <template #default="scope">
                <span style="color: green; font-weight: bold;">¥{{ scope.row.totalFee }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="logisticsProgress" label="状态" width="200"></el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="quoteDialogVisible" title="网点报价列表" width="800px">
      <div v-if="currentOrder" style="margin-bottom: 20px;">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="运单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="货物">{{ currentOrder.goodsName }}</el-descriptions-item>
          <el-descriptions-item label="重量">{{ currentOrder.weight }}kg</el-descriptions-item>
          <el-descriptions-item label="体积">{{ currentOrder.volume }}m³</el-descriptions-item>
          <el-descriptions-item label="发货地址" :span="2">{{ currentOrder.senderAddress }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-table :data="currentQuotes" style="width: 100%">
        <el-table-column prop="networkName" label="网点" width="150"></el-table-column>
        <el-table-column prop="baseFee" label="成本价(底价)" width="120">
          <template #default="scope">
            <span style="color: blue;">¥{{ scope.row.baseFee }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="finalPrice" label="客户报价" width="120">
          <template #default="scope">
            <span style="color: red; font-weight: bold;">¥{{ scope.row.finalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="transitDays" label="运输天数" width="100">
          <template #default="scope">
            {{ scope.row.transitDays }}天
          </template>
        </el-table-column>
        <el-table-column prop="quoteTime" label="报价时间" width="160"></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 2" type="success">已选中</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="danger">已拒绝</el-tag>
            <el-tag v-else type="warning">待选择</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status !== 2" 
              type="primary" 
              size="small" 
              @click="selectQuote(scope.row)"
            >
              选择此报价
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 20px; text-align: center;">
        <el-alert
          title="提示：选择报价后，该报价将成为订单的最终价格，成本价将作为网点结算依据"
          type="info"
          :closable="false"
        ></el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeTab = ref('pending')
const pendingOrders = ref([])
const confirmedOrders = ref([])
const quoteDialogVisible = ref(false)
const currentOrder = ref(null)
const currentQuotes = ref([])
const quoteCountMap = ref({})

const getQuoteCount = (orderId) => {
  return quoteCountMap.value[orderId] || 0
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/list')
    const orders = res || []
    
    const pending = []
    const confirmed = []
    const countMap = {}
    
    for (const order of orders) {
      const quoteRes = await request.get('/order/dispatch/quotes', { params: { orderId: order.id } })
      const quotes = quoteRes || []
      countMap[order.id] = quotes.length
      
      const hasSelected = quotes.some(q => q.status === 2)
      
      if (hasSelected) {
        confirmed.push(order)
      } else if (quotes.length > 0) {
        pending.push(order)
      }
    }
    
    pendingOrders.value = pending
    confirmedOrders.value = confirmed
    quoteCountMap.value = countMap
  } catch (error) {
    console.error('加载订单失败', error)
  } finally {
    loading.value = false
  }
}

const showQuotes = async (order) => {
  currentOrder.value = order
  try {
    const res = await request.get('/order/dispatch/quotes', { params: { orderId: order.id } })
    currentQuotes.value = res || []
    quoteDialogVisible.value = true
  } catch (error) {
    console.error('加载报价失败', error)
    ElMessage.error('加载报价失败')
  }
}

const selectQuote = async (quote) => {
  try {
    await ElMessageBox.confirm(
      `确定选择【${quote.networkName}】的报价吗？\n成本价: ¥${quote.baseFee}\n客户报价: ¥${quote.finalPrice}`, 
      '确认选择报价', 
      { type: 'warning' }
    )
    
    const res = await request.post('/order/dispatch/select-quote', {
      quoteId: quote.id,
      orderId: currentOrder.value.id
    })
    
    if (res.code === 200) {
      ElMessage.success('选择报价成功')
      quoteDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(res.message || '选择报价失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('选择报价失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
