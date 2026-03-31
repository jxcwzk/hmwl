<template>
  <div class="customer-page">
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
            <el-button type="primary" @click="handleAddOrder" style="margin-left: 10px;">
              <el-icon><Plus /></el-icon>
              新增订单
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="order-tabs">
        <el-tab-pane name="draft">
          <template #label>
            <span class="tab-label">
              草稿箱
              <el-badge :value="countMap.draft" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="4">
          <template #label>
            <span class="tab-label">
              确认报价
              <el-badge :value="countMap['4']" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="delivering">
          <template #label>
            <span class="tab-label">
              配送中
              <el-badge :value="countMap.delivering" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="13">
          <template #label>
            <span class="tab-label">
              已完成
              <el-badge :value="countMap['13']" :max="99" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <div class="table-container">
        <el-table :data="paginatedOrderList" v-loading="loading" stripe>
          <el-table-column prop="orderNo" label="订单编号" min-width="150" />
          <el-table-column prop="senderName" label="发件人" min-width="100" />
          <el-table-column prop="receiverName" label="收件人" min-width="100" />
          <el-table-column prop="goodsName" label="货物" min-width="100" />
          <el-table-column prop="weight" label="重量" width="80">
            <template #default="scope">{{ scope.row.weight }} kg</template>
          </el-table-column>
          <el-table-column prop="totalFee" label="费用" width="100">
            <template #default="scope">¥{{ scope.row.totalFee || '-' }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
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

      <el-divider content-position="left">物流时间线</el-divider>
      <div class="timeline-snake" v-if="timeline.length > 0">
        <div
          v-for="(item, index) in timeline"
          :key="index"
          class="snake-node"
          :class="getStatusClass(item.statusCode)"
        >
          <div class="snake-dot"></div>
          <div class="snake-content">
            <div class="snake-status">{{ item.statusName }}</div>
            <div class="snake-time">{{ formatTimelineTime(item.operateTime) }}</div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无物流时间线数据" />
    </el-dialog>

    <!-- 新增订单对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增订单" width="700px">
      <el-form :model="addForm" label-width="100px">
        <el-divider content-position="left">发件人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发件人">
              <el-select
                v-model="addForm.senderName"
                placeholder="请选择发件人"
                filterable
                allow-create
                default-first-option
                style="width: 100%"
                @change="handleSenderChange"
              >
                <el-option
                  v-for="s in senderList"
                  :key="s.id"
                  :label="s.customerName"
                  :value="s.customerName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发件人电话">
              <el-input v-model="addForm.senderPhone" placeholder="请选择发件人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发件人地址">
          <el-input v-model="addForm.senderAddress" placeholder="请选择发件人" disabled />
        </el-form-item>
        <el-form-item>
          <el-button v-if="addForm.senderName" type="warning" plain @click="openEditSenderForm">
            编辑发件人
          </el-button>
          <el-button v-else type="primary" plain @click="openAddSenderForm">
            + 新增发件人
          </el-button>
        </el-form-item>
        <el-card v-if="showAddSenderForm" class="add-contact-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>{{ isEditingSender ? '编辑发件人' : '新增发件人' }}</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人">
                <el-input v-model="newSender.contact" placeholder="请输入联系人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="电话">
                <el-input v-model="newSender.phone" placeholder="请输入电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="地址">
            <el-input v-model="newSender.address" placeholder="请输入地址" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSaveSender" :loading="addSenderLoading">保存</el-button>
            <el-button @click="closeSenderForm">取消</el-button>
          </el-form-item>
        </el-card>

        <el-divider content-position="left">收件人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收件人">
              <el-select
                v-model="addForm.receiverName"
                placeholder="请选择收件人"
                filterable
                allow-create
                default-first-option
                style="width: 100%"
                @change="handleReceiverChange"
              >
                <el-option
                  v-for="r in receiverList"
                  :key="r.id"
                  :label="r.customerName"
                  :value="r.customerName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收件人电话">
              <el-input v-model="addForm.receiverPhone" placeholder="请选择收件人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="收件人地址">
          <el-input v-model="addForm.receiverAddress" placeholder="请选择收件人" disabled />
        </el-form-item>
        <el-form-item>
          <el-button v-if="addForm.receiverName" type="warning" plain @click="openEditReceiverForm">
            编辑收件人
          </el-button>
          <el-button v-else type="primary" plain @click="openAddReceiverForm">
            + 新增收件人
          </el-button>
        </el-form-item>
        <el-card v-if="showAddReceiverForm" class="add-contact-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>{{ isEditingReceiver ? '编辑收件人' : '新增收件人' }}</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人">
                <el-input v-model="newReceiver.contact" placeholder="请输入联系人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="电话">
                <el-input v-model="newReceiver.phone" placeholder="请输入电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="地址">
            <el-input v-model="newReceiver.address" placeholder="请输入地址" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSaveReceiver" :loading="addReceiverLoading">保存</el-button>
            <el-button @click="closeReceiverForm">取消</el-button>
          </el-form-item>
        </el-card>

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
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddOrder" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Plus, Search } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const orderList = ref([])
const activeTab = ref('draft')
const currentPage = ref(1)
const pageSize = ref(20)
const total = computed(() => filteredOrderList.value.length)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const searchKeyword = ref('')
const addDialogVisible = ref(false)
const submitLoading = ref(false)
const senderList = ref([])
const receiverList = ref([])
const showAddSenderForm = ref(false)
const showAddReceiverForm = ref(false)
const addSenderLoading = ref(false)
const addReceiverLoading = ref(false)
const isEditingSender = ref(false)
const isEditingReceiver = ref(false)
const editingSenderId = ref(null)
const editingReceiverId = ref(null)
const newSender = ref({
  contact: '',
  phone: '',
  address: ''
})
const newReceiver = ref({
  contact: '',
  phone: '',
  address: ''
})
const timeline = ref([])
const addForm = ref({
  senderName: '',
  senderPhone: '',
  senderAddress: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  goodsName: '',
  weight: 1,
  volume: 0.01
})

const countMap = computed(() => {
  const map = { draft: 0, '4': 0, '13': 0, delivering: 0 }
  orderList.value.forEach(order => {
    if (order.status === 0) map.draft++
    else if (order.status === 4) map['4']++
    else if (order.status === 13) map['13']++
    else if ([6, 7, 8, 9, 11].includes(order.status)) map.delivering++
  })
  return map
})

const filteredOrderList = computed(() => {
  let filtered = orderList.value
  if (searchKeyword.value) {
    filtered = filtered.filter(o => o.orderNo && o.orderNo.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  }
  if (activeTab.value === 'draft') {
    filtered = filtered.filter(o => o.status === 0)
  } else if (activeTab.value === 'delivering') {
    filtered = filtered.filter(o => [6, 7, 8, 9, 11].includes(o.status))
  } else {
    filtered = filtered.filter(o => o.status === parseInt(activeTab.value))
  }
  return filtered
})

const handleSearch = () => {
  currentPage.value = 1
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
    volume: 0.01
  }
  showAddSenderForm.value = false
  showAddReceiverForm.value = false
  newSender.value = { contact: '', phone: '', address: '' }
  newReceiver.value = { contact: '', phone: '', address: '' }
  addDialogVisible.value = true
  loadContacts()
}

const openAddSenderForm = () => {
  isEditingSender.value = false
  editingSenderId.value = null
  newSender.value = { contact: '', phone: '', address: '' }
  showAddSenderForm.value = true
}

const openEditSenderForm = () => {
  const sender = senderList.value.find(s => s.customerName === addForm.value.senderName)
  if (sender) {
    isEditingSender.value = true
    editingSenderId.value = sender.id
    newSender.value = {
      contact: sender.customerName || sender.contact || '',
      phone: sender.phone || '',
      address: sender.address || ''
    }
    showAddSenderForm.value = true
  }
}

const closeSenderForm = () => {
  showAddSenderForm.value = false
  isEditingSender.value = false
  editingSenderId.value = null
  newSender.value = { contact: '', phone: '', address: '' }
}

const openAddReceiverForm = () => {
  isEditingReceiver.value = false
  editingReceiverId.value = null
  newReceiver.value = { contact: '', phone: '', address: '' }
  showAddReceiverForm.value = true
}

const openEditReceiverForm = () => {
  const receiver = receiverList.value.find(r => r.customerName === addForm.value.receiverName)
  if (receiver) {
    isEditingReceiver.value = true
    editingReceiverId.value = receiver.id
    newReceiver.value = {
      contact: receiver.customerName || receiver.contact || '',
      phone: receiver.phone || '',
      address: receiver.address || ''
    }
    showAddReceiverForm.value = true
  }
}

const closeReceiverForm = () => {
  showAddReceiverForm.value = false
  isEditingReceiver.value = false
  editingReceiverId.value = null
  newReceiver.value = { contact: '', phone: '', address: '' }
}

const handleSaveSender = async () => {
  if (!newSender.value.contact || !newSender.value.phone || !newSender.value.address) {
    ElMessage.warning('请填写完整的发件人信息')
    return
  }
  addSenderLoading.value = true
  try {
    const businessUserId = localStorage.getItem('businessUserId') || 1
    if (isEditingSender.value) {
      await request.put('/miniprogram/contact', {
        id: editingSenderId.value,
        customerName: newSender.value.contact,
        contact: newSender.value.contact,
        phone: newSender.value.phone,
        address: newSender.value.address,
        businessUserId: Number(businessUserId)
      })
      ElMessage.success('发件人更新成功')
    } else {
      await request.post('/miniprogram/sender', {
        customerName: newSender.value.contact,
        contact: newSender.value.contact,
        phone: newSender.value.phone,
        address: newSender.value.address,
        businessUserId: Number(businessUserId)
      })
      ElMessage.success('发件人添加成功')
    }
    addForm.value.senderName = newSender.value.contact
    addForm.value.senderPhone = newSender.value.phone
    addForm.value.senderAddress = newSender.value.address
    closeSenderForm()
    await loadContacts()
  } catch (error) {
    ElMessage.error(isEditingSender.value ? '发件人更新失败' : '发件人添加失败')
  } finally {
    addSenderLoading.value = false
  }
}

const handleSaveReceiver = async () => {
  if (!newReceiver.value.contact || !newReceiver.value.phone || !newReceiver.value.address) {
    ElMessage.warning('请填写完整的收件人信息')
    return
  }
  addReceiverLoading.value = true
  try {
    const businessUserId = localStorage.getItem('businessUserId') || 1
    if (isEditingReceiver.value) {
      await request.put('/miniprogram/contact', {
        id: editingReceiverId.value,
        customerName: newReceiver.value.contact,
        contact: newReceiver.value.contact,
        phone: newReceiver.value.phone,
        address: newReceiver.value.address,
        businessUserId: Number(businessUserId)
      })
      ElMessage.success('收件人更新成功')
    } else {
      await request.post('/miniprogram/recipient', {
        customerName: newReceiver.value.contact,
        contact: newReceiver.value.contact,
        phone: newReceiver.value.phone,
        address: newReceiver.value.address,
        businessUserId: Number(businessUserId)
      })
      ElMessage.success('收件人添加成功')
    }
    addForm.value.receiverName = newReceiver.value.contact
    addForm.value.receiverPhone = newReceiver.value.phone
    addForm.value.receiverAddress = newReceiver.value.address
    closeReceiverForm()
    await loadContacts()
  } catch (error) {
    ElMessage.error(isEditingReceiver.value ? '收件人更新失败' : '收件人添加失败')
  } finally {
    addReceiverLoading.value = false
  }
}

const loadContacts = async () => {
  const businessUserId = localStorage.getItem('businessUserId') || 1
  try {
    const [senders, recipients] = await Promise.all([
      request.get('/miniprogram/senders', { params: { businessUserId } }),
      request.get('/miniprogram/recipients', { params: { businessUserId } })
    ])
    senderList.value = (senders.data || senders || []).filter(s => s.customerName)
    receiverList.value = (recipients.data || recipients || []).filter(r => r.customerName)
  } catch (error) {
    console.error('加载联系人失败', error)
  }
}

const handleSenderChange = (name) => {
  const sender = senderList.value.find(s => s.customerName === name)
  if (sender) {
    addForm.value.senderPhone = sender.phone || ''
    addForm.value.senderAddress = sender.address || ''
  }
}

const handleReceiverChange = (name) => {
  const receiver = receiverList.value.find(r => r.customerName === name)
  if (receiver) {
    addForm.value.receiverPhone = receiver.phone || ''
    addForm.value.receiverAddress = receiver.address || ''
  }
}

const submitAddOrder = async () => {
  if (!addForm.value.senderName || !addForm.value.receiverName) {
    ElMessage.warning('请填写发件人和收件人信息')
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
  loadOrderTimeline(row.orderNo)
  detailDialogVisible.value = true
}

const loadOrderTimeline = async (orderNo) => {
  if (!orderNo) return
  try {
    const res = await request.get('/order/timeline/' + orderNo)
    const keyStatuses = ['ORDER_CREATED', 'PRICE_CONFIRMED', 'DRIVER_PICKED', 'ARRIVED_NETWORK', 'NETWORK_CONFIRMED', 'CUSTOMER_SIGNED']
    timeline.value = (res || []).filter(item => keyStatuses.includes(item.statusCode))
  } catch (error) {
    console.error('加载时间线失败', error)
    timeline.value = []
  }
}

const paginatedOrderList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrderList.value.slice(start, end)
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

const handleTabChange = () => {
  currentPage.value = 1
}

const handleRefresh = () => {
  getOrderList()
  ElMessage.success('刷新成功')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const getStatusType = (status) => {
  const map = { 0: 'info', 4: 'warning', 5: 'success', 11: 'primary', 13: 'success' }
  return map[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const formatTimelineTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const getStatusClass = (statusCode) => {
  if (statusCode === 'CUSTOMER_SIGNED') return 'status-success'
  if (statusCode === 'DRIVER_PICKED' || statusCode === 'ARRIVED_NETWORK') return 'status-active'
  return 'status-default'
}

const getOperatorTypeName = (type) => {
  const map = { CUSTOMER: '客户', DISPATCHER: '调度', NETWORK: '网点', DRIVER: '司机', SYSTEM: '系统' }
  return map[type] || type || '未知'
}

const getStatusText = (status) => {
  const map = {
    0: '待发起询价', 4: '待确认价格', 5: '价格已确认', 6: '待提货',
    7: '已提货', 8: '送达网点', 9: '网点已收货', 11: '配送中', 13: '已签收'
  }
  return map[status] || '未知'
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
.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
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
.add-contact-card {
  background-color: #f5f7fa;
  margin-bottom: 16px;
}
.card-header {
  font-weight: 600;
}
.timeline-container {
  max-height: 300px;
  overflow-y: auto;
  margin-top: 10px;
}
.timeline-item {
  padding: 4px 0;
}
.timeline-status {
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}
.timeline-operator {
  font-size: 12px;
  color: #909399;
}
.timeline-remark {
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
}
.timeline-snake {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  margin-top: 10px;
  position: relative;
}
.snake-node {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  position: relative;
}
.snake-node:nth-child(odd) {
  flex-direction: row;
  justify-content: flex-start;
}
.snake-node:nth-child(even) {
  flex-direction: row-reverse;
  justify-content: flex-start;
}
.snake-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 3px solid;
  background: #fff;
  flex-shrink: 0;
  z-index: 2;
}
.snake-node:nth-child(odd) .snake-dot { margin-right: 12px; }
.snake-node:nth-child(even) .snake-dot { margin-left: 12px; }
.status-default .snake-dot { border-color: #909399; }
.status-active .snake-dot { border-color: #409eff; }
.status-success .snake-dot { border-color: #67c23a; }
.snake-content {
  background: #f5f7fa;
  padding: 8px 14px;
  border-radius: 6px;
  flex: 1;
}
.status-default .snake-content { border-left: 3px solid #909399; }
.status-active .snake-content { border-left: 3px solid #409eff; }
.status-success .snake-content { border-left: 3px solid #67c23a; }
.snake-status {
  font-weight: 500;
  color: #303133;
  font-size: 13px;
}
.snake-time {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}
.snake-node:nth-child(odd)::after {
  content: '';
  position: absolute;
  right: -2px;
  top: 50%;
  width: 4px;
  height: 2px;
  background: #409eff;
}
.snake-node:nth-child(even)::after {
  content: '';
  position: absolute;
  left: -2px;
  top: 50%;
  width: 4px;
  height: 2px;
  background: #409eff;
}
.snake-node:nth-child(4n+3)::after,
.snake-node:nth-child(4n+4)::after {
  background: #67c23a;
}
</style>