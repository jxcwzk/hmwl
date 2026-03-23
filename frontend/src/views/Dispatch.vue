<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>调度管理 - V4流程</span>
          <el-button type="primary" @click="loadData">刷新数据</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="派发比价" name="pricing">
          <el-table v-loading="loading" :data="pricingOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="senderAddress" label="发货地址" width="200"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="货物" width="100"></el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="80"></el-table-column>
            <el-table-column prop="pricingStatus" label="比价状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.pricingStatus === 0" type="warning">待报价</el-tag>
                <el-tag v-else-if="scope.row.pricingStatus === 1" type="success">已报价</el-tag>
                <el-tag v-else-if="scope.row.pricingStatus === 2" type="info">已选择</el-tag>
                <el-tag v-else type="info">未开始</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showNetworks(scope.row)">
                  选择网点派发
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

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

        <el-tab-pane label="安排提货" name="pickup">
          <el-table v-loading="loading" :data="pendingPickupOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="senderAddress" label="发货地址" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="货物" width="100"></el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="80"></el-table-column>
            <el-table-column prop="pickupDriverId" label="提货司机" width="120">
              <template #default="scope">
                <span v-if="scope.row.pickupDriverId">司机{{ scope.row.pickupDriverId }}</span>
                <el-tag v-else type="warning">待分配</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 6" type="info">待提货</el-tag>
                <el-tag v-else-if="scope.row.status === 7" type="success">已提货</el-tag>
                <el-tag v-else type="info">其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button v-if="!scope.row.pickupDriverId" type="primary" size="small" @click="showDrivers(scope.row, 'pickup')">
                  分配提货司机
                </el-button>
                <el-button v-else type="success" size="small" @click="updatePickupStatus(scope.row)">
                  更新状态
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="分配配送" name="delivery">
          <el-table v-loading="loading" :data="pendingDeliveryOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="货物" width="100"></el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="80"></el-table-column>
            <el-table-column prop="warehouseStatus" label="入库状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.warehouseStatus === 1" type="success">已入库</el-tag>
                <el-tag v-else type="warning">待入库</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deliveryDriverId" label="配送司机" width="120">
              <template #default="scope">
                <span v-if="scope.row.deliveryDriverId">司机{{ scope.row.deliveryDriverId }}</span>
                <el-tag v-else type="warning">待分配</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button v-if="scope.row.warehouseStatus === 1 && !scope.row.deliveryDriverId" type="primary" size="small" @click="showDrivers(scope.row, 'delivery')">
                  分配配送司机
                </el-button>
                <el-button v-else-if="scope.row.deliveryDriverId" type="success" size="small" @click="updateDeliveryStatus(scope.row)">
                  更新状态
                </el-button>
                <span v-else style="color: #999;">等待入库</span>
              </template>
            </el-table-column>
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
          title="提示：选择报价后，该报价将成为订单的最终价格"
          type="info"
          :closable="false"
        ></el-alert>
      </div>
    </el-dialog>

    <el-dialog v-model="networkDialogVisible" title="选择网点派发比价" width="600px">
      <div v-if="currentOrder" style="margin-bottom: 20px;">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="运单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="货物信息">{{ currentOrder.goodsName }} - {{ currentOrder.weight }}kg - {{ currentOrder.volume }}m³</el-descriptions-item>
          <el-descriptions-item label="发货地址">{{ currentOrder.senderAddress }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <el-checkbox-group v-model="selectedNetworks">
        <el-checkbox v-for="network in networks" :key="network.id" :label="network.id" style="margin: 10px 0;">
          {{ network.networkName }} - {{ network.address }}
        </el-checkbox>
      </el-checkbox-group>

      <template #footer>
        <el-button @click="networkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="requestQuotes">确认派发</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="driverDialogVisible" :title="driverType === 'pickup' ? '分配提货司机' : '分配配送司机'" width="600px">
      <div v-if="currentOrder" style="margin-bottom: 20px;">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="运单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ driverType === 'pickup' ? currentOrder.senderAddress : currentOrder.receiverAddress }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <el-radio-group v-model="selectedDriver">
        <div v-for="driver in availableDrivers" :key="driver.id" style="margin: 10px 0;">
          <el-radio :label="driver.id">
            {{ driver.name }} - {{ driver.phone }} - 评分: {{ driver.rating || 5.0 }}
          </el-radio>
        </div>
      </el-radio-group>

      <template #footer>
        <el-button @click="driverDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="assignDriver">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeTab = ref('pricing')
const pendingOrders = ref([])
const confirmedOrders = ref([])
const pricingOrders = ref([])
const pendingPickupOrders = ref([])
const pendingDeliveryOrders = ref([])
const quoteDialogVisible = ref(false)
const networkDialogVisible = ref(false)
const driverDialogVisible = ref(false)
const currentOrder = ref(null)
const currentQuotes = ref([])
const quoteCountMap = ref({})
const networks = ref([])
const selectedNetworks = ref([])
const availableDrivers = ref([])
const selectedDriver = ref(null)
const driverType = ref('')

const getQuoteCount = (orderId) => {
  return quoteCountMap.value[orderId] || 0
}

const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadPricingOrders(),
      loadOrders(),
      loadPendingPickupOrders(),
      loadPendingDeliveryOrders(),
      loadNetworks()
    ])
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const loadPricingOrders = async () => {
  try {
    const res = await request.get('/dispatch/orders/pricing')
    pricingOrders.value = res || []
  } catch (error) {
    console.error('加载比价订单失败', error)
  }
}

const loadOrders = async () => {
  try {
    const res = await request.get('/order/list')
    const orders = res || []

    const pending = []
    const confirmed = []
    const countMap = {}

    for (const order of orders) {
      try {
        const quoteRes = await request.get('/dispatch/quotes', { params: { orderId: order.id } })
        const quotes = quoteRes || []
        countMap[order.id] = quotes.length

        const hasSelected = quotes.some(q => q.status === 2)

        if (hasSelected) {
          confirmed.push(order)
        } else if (quotes.length > 0) {
          pending.push(order)
        }
      } catch (e) {
        console.error('加载报价失败', e)
      }
    }

    pendingOrders.value = pending
    confirmedOrders.value = confirmed
    quoteCountMap.value = countMap
  } catch (error) {
    console.error('加载订单失败', error)
  }
}

const loadPendingPickupOrders = async () => {
  try {
    const res = await request.get('/dispatch/orders/pending-pickup')
    pendingPickupOrders.value = res || []
  } catch (error) {
    console.error('加载待提货订单失败', error)
  }
}

const loadPendingDeliveryOrders = async () => {
  try {
    const res = await request.get('/dispatch/orders/pending-delivery')
    pendingDeliveryOrders.value = res || []
  } catch (error) {
    console.error('加载待配送订单失败', error)
  }
}

const loadNetworks = async () => {
  try {
    const res = await request.get('/network/list')
    networks.value = res || []
  } catch (error) {
    console.error('加载网点失败', error)
  }
}

const loadDrivers = async () => {
  try {
    const res = await request.get('/driver/list')
    availableDrivers.value = res || []
  } catch (error) {
    console.error('加载司机失败', error)
  }
}

const showNetworks = async (order) => {
  currentOrder.value = order
  selectedNetworks.value = []
  networkDialogVisible.value = true
}

const showDrivers = async (order, type) => {
  currentOrder.value = order
  driverType.value = type
  selectedDriver.value = null
  await loadDrivers()
  driverDialogVisible.value = true
}

const requestQuotes = async () => {
  if (selectedNetworks.value.length === 0) {
    ElMessage.warning('请选择至少一个网点')
    return
  }

  try {
    const res = await request.post('/dispatch/request-quotes', {
      orderId: currentOrder.value.id,
      networkIds: selectedNetworks.value
    })

    if (res.code === 200) {
      ElMessage.success('比价请求已派发')
      networkDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '派发失败')
    }
  } catch (error) {
    ElMessage.error('派发比价请求失败')
  }
}

const showQuotes = async (order) => {
  currentOrder.value = order
  try {
    const res = await request.get('/dispatch/quotes', { params: { orderId: order.id } })
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

    const res = await request.post('/dispatch/select-quote', {
      quoteId: quote.id,
      orderId: currentOrder.value.id
    })

    if (res.code === 200) {
      ElMessage.success('选择报价成功')
      quoteDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '选择报价失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('选择报价失败')
    }
  }
}

const assignDriver = async () => {
  if (!selectedDriver.value) {
    ElMessage.warning('请选择司机')
    return
  }

  try {
    const api = driverType.value === 'pickup' ? '/dispatch/assign-pickup-driver' : '/dispatch/assign-delivery-driver'
    const res = await request.post(api, {
      orderId: currentOrder.value.id,
      driverId: selectedDriver.value
    })

    if (res.code === 200) {
      ElMessage.success(driverType.value === 'pickup' ? '提货司机分配成功' : '配送司机分配成功')
      driverDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '分配失败')
    }
  } catch (error) {
    ElMessage.error('分配司机失败')
  }
}

const updatePickupStatus = async (order) => {
  try {
    await ElMessageBox.confirm('确认货物已提货?', '更新状态', { type: 'warning' })

    const res = await request.post('/order/driver/update-status', {
      orderId: order.id,
      status: 7,
      remark: '已提货'
    })

    if (res.code === 200) {
      ElMessage.success('状态已更新')
      loadData()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('更新状态失败')
    }
  }
}

const updateDeliveryStatus = async (order) => {
  try {
    await ElMessageBox.confirm('确认货物已送达?', '更新状态', { type: 'warning' })

    const res = await request.post('/order/driver/update-status', {
      orderId: order.id,
      status: 13,
      remark: '已签收'
    })

    if (res.code === 200) {
      ElMessage.success('状态已更新')
      loadData()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('更新状态失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>