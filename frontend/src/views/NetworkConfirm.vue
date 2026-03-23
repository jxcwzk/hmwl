<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>网点确认收货</span>
          <el-button type="primary" @click="loadData">刷新数据</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="待确认收货" name="pending">
          <el-table v-loading="loading" :data="pendingOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="senderAddress" label="发货地址" width="200"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="货物" width="100"></el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="80"></el-table-column>
            <el-table-column prop="pickupDriverId" label="提货司机" width="120">
              <template #default="scope">
                <span v-if="scope.row.pickupDriverId">司机{{ scope.row.pickupDriverId }}</span>
                <el-tag v-else type="warning">待分配</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="logisticsProgress" label="当前状态" width="200"></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showConfirmDialog(scope.row)">
                  确认收货
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="已入库" name="warehoused">
          <el-table v-loading="loading" :data="warehousedOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
            <el-table-column prop="orderNo" label="运单号" width="180"></el-table-column>
            <el-table-column prop="receiverAddress" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="货物" width="100"></el-table-column>
            <el-table-column prop="warehouseConfirmTime" label="入库时间" width="180"></el-table-column>
            <el-table-column prop="logisticsProgress" label="状态" width="200"></el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="confirmDialogVisible" title="确认收货" width="600px">
      <div v-if="currentOrder" style="margin-bottom: 20px;">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="运单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="货物信息">{{ currentOrder.goodsName }} - {{ currentOrder.weight }}kg</el-descriptions-item>
          <el-descriptions-item label="发货地址">{{ currentOrder.senderAddress }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="提货司机">司机{{ currentOrder.pickupDriverId }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <el-form :model="confirmForm" label-width="100px">
        <el-form-item label="检查结果">
          <el-radio-group v-model="confirmForm.checkResult">
            <el-radio label="ok">货物完好</el-radio>
            <el-radio label="exception">有异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="confirmForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReceive">确认收货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const activeTab = ref('pending')
const pendingOrders = ref([])
const warehousedOrders = ref([])
const confirmDialogVisible = ref(false)
const currentOrder = ref(null)
const confirmForm = ref({
  checkResult: 'ok',
  remark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadPendingOrders(),
      loadWarehousedOrders()
    ])
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const loadPendingOrders = async () => {
  try {
    const res = await request.get('/network/orders', {
      params: {
        networkId: 1,
        status: 8
      }
    })
    pendingOrders.value = res || []
  } catch (error) {
    console.error('加载待确认订单失败', error)
  }
}

const loadWarehousedOrders = async () => {
  try {
    const res = await request.get('/network/orders', {
      params: {
        networkId: 1,
        status: 9
      }
    })
    warehousedOrders.value = res || []
  } catch (error) {
    console.error('加载已入库订单失败', error)
  }
}

const showConfirmDialog = (order) => {
  currentOrder.value = order
  confirmForm.value = {
    checkResult: 'ok',
    remark: ''
  }
  confirmDialogVisible.value = true
}

const confirmReceive = async () => {
  if (!currentOrder.value) {
    ElMessage.error('请选择订单')
    return
  }

  try {
    const res = await request.post('/network/confirm-receive', {
      orderId: currentOrder.value.id,
      networkId: currentOrder.value.selectedNetworkId || 1,
      checkResult: confirmForm.value.checkResult,
      remark: confirmForm.value.remark
    })

    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      confirmDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '确认收货失败')
    }
  } catch (error) {
    ElMessage.error('确认收货失败')
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