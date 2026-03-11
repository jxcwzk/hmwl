<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div>
            <el-button type="success" @click="handleBatchExport">批量导出</el-button>
            <el-button type="primary" @click="handleAdd">新增订单</el-button>
          </div>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="orderList" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="订单ID" width="100"></el-table-column>
        <el-table-column prop="orderNo" label="订单编号"></el-table-column>
        <el-table-column prop="orderType" label="订单类型"></el-table-column>
        <el-table-column prop="senderName" label="发件人"></el-table-column>
        <el-table-column prop="receiverName" label="收件人"></el-table-column>
        <el-table-column prop="totalFee" label="总费用"></el-table-column>
        <el-table-column prop="status" label="状态"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>

    <el-dialog v-model="dialogVisible" title="订单详情" width="1100px" class="order-detail-dialog" :show-close="true">
      <div class="order-detail-container">
        <!-- 订单基本信息区 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <div class="section-header">
              <el-icon><Document /></el-icon>
              <span>订单基本信息</span>
              <el-button type="primary" size="small" @click="handleAddBusinessUser" style="margin-left: auto;">
                + 添加业务用户
              </el-button>
            </div>
          </template>
          <el-row :gutter="16">
            <el-col :span="20">
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="订单编号">
                    <el-input v-model="form.orderNo" placeholder="自动生成"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="订单类型">
                    <el-select v-model="form.orderType" placeholder="请选择">
                      <el-option label="零担" value="0"></el-option>
                      <el-option label="整车" value="1"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="状态">
                    <el-tag :type="getStatusType(form.status)" size="large">{{ getStatusText(form.status) }}</el-tag>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="付款方式">
                    <el-select v-model="form.paymentMethod" placeholder="请选择">
                      <el-option label="现付" value="0"></el-option>
                      <el-option label="到付" value="1"></el-option>
                      <el-option label="月结" value="2"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="16">
                  <el-form-item label="业务用户">
                    <el-select v-model="form.businessUserId" placeholder="请选择业务用户" @change="handleBusinessUserChange" style="width: 100%;">
                      <el-option v-for="user in businessUserList" :key="user.id" :label="user.username" :value="user.id"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="4">
              <div class="qrcode-container">
                <canvas ref="qrcodeCanvas" class="qrcode-canvas"></canvas>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 发件人和收件人信息区 - 并排显示 -->
        <el-row :gutter="24">
          <el-col :span="12">
            <el-card class="section-card sender-card" shadow="hover">
              <template #header>
                <div class="section-header sender">
                  <el-icon><User /></el-icon>
                  <span>发件人信息</span>
                  <el-tag type="success" size="small">寄</el-tag>
                  <el-button type="primary" size="small" @click="handleAddBusinessCustomer" style="margin-left: auto;">
                    + 添加发件人
                  </el-button>
                </div>
              </template>
              <el-row :gutter="16">
                <el-col :span="24">
                  <el-form-item label="选择发件人">
                    <el-select v-model="form.senderId" placeholder="请选择发件人" @change="handleSenderChange" style="width: 100%;">
                      <el-option v-for="sender in senderList" :key="sender.id" :label="sender.customerName" :value="sender.id"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="24">
                  <el-form-item label="发件人电话">
                    <el-input v-model="form.senderPhone" placeholder="请输入发件人电话"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="24">
                  <el-form-item label="发件人地址">
                    <el-input v-model="form.senderAddress" type="textarea" :rows="2" placeholder="请输入发件人地址"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="section-card receiver-card" shadow="hover">
              <template #header>
                <div class="section-header receiver">
                  <el-icon><UserFilled /></el-icon>
                  <span>收件人信息</span>
                  <el-tag type="warning" size="small">收</el-tag>
                  <el-button type="primary" size="small" @click="handleAddBusinessCustomer" style="margin-left: auto;">
                    + 添加收件人
                  </el-button>
                </div>
              </template>
              <el-row :gutter="16">
                <el-col :span="24">
                  <el-form-item label="选择收件人">
                    <el-select v-model="form.recipientId" placeholder="请选择收件人" @change="handleRecipientChange" style="width: 100%;">
                      <el-option v-for="recipient in recipientList" :key="recipient.id" :label="recipient.customerName" :value="recipient.id"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="24">
                  <el-form-item label="收件人电话">
                    <el-input v-model="form.receiverPhone" placeholder="请输入收件人电话"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="24">
                  <el-form-item label="收件人地址">
                    <el-input v-model="form.receiverAddress" type="textarea" :rows="2" placeholder="请输入收件人地址"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
          </el-col>
        </el-row>

        <!-- 货物信息、费用信息、网点信息 - 并排显示 -->
        <el-row :gutter="16">
          <el-col :span="8">
            <!-- 货物信息区 -->
            <el-card class="section-card goods-card" shadow="hover">
              <template #header>
                <div class="section-header goods">
                  <el-icon><Box /></el-icon>
                  <span>货物信息</span>
                </div>
              </template>
              <el-form-item label="货物名称">
                <el-input v-model="form.goodsName" placeholder="请输入货物名称"></el-input>
              </el-form-item>
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="数量">
                    <el-input v-model="form.quantity" type="number" placeholder="件"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="重量(kg)">
                    <el-input v-model="form.weight" type="number" placeholder="kg"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="体积(m³)">
                    <el-input v-model="form.volume" type="number" placeholder="m³"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="距离(km)">
                    <el-input v-model="form.distance" type="number" placeholder="km"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
          </el-col>
          <el-col :span="8">
            <!-- 费用信息区 -->
            <el-card class="section-card fee-card" shadow="hover">
              <template #header>
                <div class="section-header fee">
                  <el-icon><Money /></el-icon>
                  <span>费用信息</span>
                  <span class="estimated-price" v-if="form.totalFee">¥{{ form.totalFee }}</span>
                  <el-button type="success" size="small" @click="handleCalculatePrice" style="margin-left: auto;">
                    <el-icon><PriceTag /></el-icon>估算
                  </el-button>
                </div>
              </template>
              <div v-if="priceBreakdown" class="price-breakdown">
                <div class="breakdown-item">
                  <span>起步价:</span>
                  <span>¥{{ priceBreakdown.baseFee }}</span>
                </div>
                <div class="breakdown-item">
                  <span>重量({{ form.weight || 0 }}kg):</span>
                  <span>¥{{ priceBreakdown.weightFee }}</span>
                </div>
                <div class="breakdown-item">
                  <span>体积({{ form.volume || 0 }}m³):</span>
                  <span>¥{{ priceBreakdown.volumeFee }}</span>
                </div>
                <div class="breakdown-item">
                  <span>距离({{ form.distance || 0 }}km):</span>
                  <span>¥{{ priceBreakdown.distanceFee }}</span>
                </div>
              </div>
              <div v-else class="no-estimate">
                <span>填写货物信息后点击估算</span>
              </div>
              <el-form-item label="确认费用" style="margin-top: 12px; margin-bottom: 0;">
                <el-input v-model="form.totalFee" type="number" placeholder="最终费用">
                  <template #prepend>¥</template>
                </el-input>
              </el-form-item>
              <el-form-item label="网点付款方式" style="margin-top: 12px; margin-bottom: 0;">
                <el-select v-model="form.networkPaymentMethod" placeholder="请选择网点付款方式" style="width: 100%;">
                  <el-option label="现付" :value="0"></el-option>
                  <el-option label="欠付" :value="1"></el-option>
                  <el-option label="到付" :value="2"></el-option>
                  <el-option label="回单付" :value="3"></el-option>
                </el-select>
              </el-form-item>
            </el-card>
          </el-col>
          <el-col :span="8">
            <!-- 网点信息区 -->
            <el-card class="section-card network-card" shadow="hover">
              <template #header>
                <div class="section-header network">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>网点信息</span>
                  <el-button type="primary" size="small" @click="handleAddNetworkPoint" style="margin-left: auto;">
                    + 添加网点
                  </el-button>
                </div>
              </template>
              <el-form-item label="选择网点">
                <el-select v-model="form.startNetworkId" placeholder="请选择网点" style="width: 100%;" @change="handleNetworkChange">
                  <el-option v-for="np in networkPointList" :key="np.id" :label="np.name" :value="np.id"></el-option>
                </el-select>
              </el-form-item>
              <div class="network-list">
                <el-table :data="networkPointList" size="small" max-height="120" style="width: 100%;">
                  <el-table-column prop="name" label="网点名称" show-overflow-tooltip></el-table-column>
                  <el-table-column prop="contactPerson" label="负责人" width="80"></el-table-column>
                  <el-table-column prop="phone" label="电话" width="100"></el-table-column>
                  <el-table-column label="操作" width="80" fixed="right">
                    <template #default="scope">
                      <el-button type="primary" size="small" link @click="handleEditNetworkPoint(scope.row)">编辑</el-button>
                      <el-button type="danger" size="small" link @click="handleDeleteNetworkPoint(scope.row.id)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 图片上传区 - 并排显示 -->
        <el-row :gutter="16">
          <el-col :span="12">
            <!-- 回单图片 -->
            <el-card class="section-card image-card" shadow="hover">
              <template #header>
                <div class="section-header">
                  <el-icon><Picture /></el-icon>
                  <span>回单图片</span>
                  <el-badge :value="imageList.length" class="image-badge" type="primary"></el-badge>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleManualUpload" 
                    :disabled="fileList.length === 0"
                    style="margin-left: auto;"
                  >
                    开始上传
                  </el-button>
                </div>
              </template>
              <el-form-item label="已上传图片" class="uploaded-images-form">
                <div v-if="imageList.length === 0" class="no-images">
                  暂无上传图片
                </div>
                <div v-else class="image-list">
                  <div
                    v-for="(image, index) in imageList"
                    :key="image.id"
                    class="image-item-wrapper"
                  >
                    <el-image
                      :src="image.imageUrl"
                      :preview-src-list="imageList.map(item => item.imageUrl)"
                      fit="cover"
                      class="image-item"
                    >
                      <template #error>
                        <div class="image-error">
                          <el-icon><Warning /></el-icon>
                          <span>加载失败</span>
                        </div>
                      </template>
                    </el-image>
                    <div class="image-actions">
                      <el-button type="danger" size="small" @click="handleDeleteImage(image.id)">
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="上传新图片" class="upload-form">
                <el-upload
                  class="upload-demo"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :limit="5"
                  :file-list="fileList"
                  drag
                  multiple
                >
                  <el-button type="primary" size="small">选择文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">
                      jpg/png，不超过10MB
                    </div>
                  </template>
                </el-upload>
              </el-form-item>
            </el-card>
          </el-col>
          <el-col :span="12">
            <!-- 发货单图片 -->
            <el-card class="section-card image-card" shadow="hover">
              <template #header>
                <div class="section-header">
                  <el-icon><Picture /></el-icon>
                  <span>发货单图片</span>
                  <el-badge :value="deliveryImageList.length" class="image-badge" type="primary"></el-badge>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleDeliveryUpload" 
                    :disabled="deliveryFileList.length === 0"
                    style="margin-left: auto;"
                  >
                    开始上传
                  </el-button>
                </div>
              </template>
              <el-form-item label="已上传图片" class="uploaded-images-form">
                <div v-if="deliveryImageList.length === 0" class="no-images">
                  暂无上传图片
                </div>
                <div v-else class="image-list">
                  <div
                    v-for="(image, index) in deliveryImageList"
                    :key="image.id"
                    class="image-item-wrapper"
                  >
                    <el-image
                      :src="image.imageUrl"
                      :preview-src-list="deliveryImageList.map(item => item.imageUrl)"
                      fit="cover"
                      class="image-item"
                    >
                      <template #error>
                        <div class="image-error">
                          <el-icon><Warning /></el-icon>
                          <span>加载失败</span>
                        </div>
                      </template>
                    </el-image>
                    <div class="image-actions">
                      <el-button type="danger" size="small" @click="handleDeleteDeliveryImage(image.id)">
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="上传新图片" class="upload-form">
                <el-upload
                  class="upload-demo"
                  :auto-upload="false"
                  :on-change="handleDeliveryFileChange"
                  :limit="5"
                  :file-list="deliveryFileList"
                  drag
                  multiple
                >
                  <el-button type="primary" size="small">选择文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">
                      jpg/png，不超过10MB
                    </div>
                  </template>
                </el-upload>
              </el-form-item>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="recipientDialogVisible" title="添加收件人">
      <el-form :model="recipientForm" label-width="120px">
        <el-form-item label="客户名称">
          <el-input v-model="recipientForm.customerName"></el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="recipientForm.contact"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="recipientForm.address" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="recipientForm.remark"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="recipientDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddRecipientSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="networkPointDialogVisible" :title="networkPointEditId ? '编辑网点' : '添加网点'" width="500px">
      <el-form :model="networkPointForm" label-width="100px">
        <el-form-item label="网点名称">
          <el-input v-model="networkPointForm.name" placeholder="请输入网点名称"></el-input>
        </el-form-item>
        <el-form-item label="网点编码">
          <el-input v-model="networkPointForm.code" placeholder="请输入网点编码"></el-input>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="networkPointForm.contactPerson" placeholder="请输入负责人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="networkPointForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="networkPointForm.address" type="textarea" placeholder="请输入地址"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="networkPointDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleNetworkPointSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode'
import * as XLSX from 'xlsx'
import { Warning, Document, User, UserFilled, Box, Money, Picture, PriceTag, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)

// 订单列表相关
const orderList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 表单相关
const dialogVisible = ref(false)
const form = ref({})
const searchForm = ref({})
const qrcodeCanvas = ref(null)
const selectedOrders = ref([])

// 业务用户和客户相关
const businessUserList = ref([])
const businessCustomerList = ref([])
const senderList = ref([])
const recipientList = ref([])

// 收件人对话框相关
const recipientDialogVisible = ref(false)
const recipientForm = ref({})

// 图片上传相关
const uploadUrl = ref('/api/order/image/upload')
const fileList = ref([])
const imageList = ref([])
const deliveryFileList = ref([])
const deliveryImageList = ref([])
const priceBreakdown = ref(null)

// 网点相关
const networkPointList = ref([])
const networkPointDialogVisible = ref(false)
const networkPointForm = ref({})
const networkPointEditId = ref(null)

// 定价配置
const pricingConfig = {
  basePrice: 10,
  pricePerKg: 1.5,
  pricePerVolume: 80,
  pricePerKm: 0.5,
  minPrice: 20,
  weightThresholds: { light: 5, medium: 20, heavy: 100 },
  volumeThresholds: { small: 0.1, medium: 1, large: 5 },
  distanceThresholds: { short: 100, medium: 500, long: 1000 }
}

// 上传数据
const uploadData = {
  get orderId() {
    return form.value.id
  },
  get orderNo() {
    return form.value.orderNo
  },
  imageType: 1
}

const getNetworkPointList = async () => {
  try {
    const res = await request.get('/network-point/list')
    networkPointList.value = res.data || res || []
  } catch (error) {
    ElMessage.error('获取网点列表失败')
  }
}

const handleAddNetworkPoint = () => {
  networkPointForm.value = {}
  networkPointEditId.value = null
  networkPointDialogVisible.value = true
}

const handleEditNetworkPoint = (row) => {
  networkPointForm.value = { ...row }
  networkPointEditId.value = row.id
  networkPointDialogVisible.value = true
}

const handleDeleteNetworkPoint = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该网点吗？', '提示', { type: 'warning' })
    await request.delete(`/network-point/${id}`)
    getNetworkPointList()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleNetworkPointSubmit = async () => {
  try {
    if (networkPointEditId.value) {
      await request.put('/network-point', networkPointForm.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/network-point', networkPointForm.value)
      ElMessage.success('新增成功')
    }
    networkPointDialogVisible.value = false
    getNetworkPointList()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleNetworkChange = (networkId) => {
  if (networkId && form.value.senderId) {
    form.value.status = 1
  }
}

const getOrderList = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        ...searchForm.value
      }
    })
    orderList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const getBusinessUserList = async () => {
  try {
    const res = await request.get('/business-user/list')
    businessUserList.value = res.data || res
  } catch (error) {
    ElMessage.error('获取业务用户列表失败，请稍后重试')
  }
}

const getSenderList = async (businessUserId) => {
  if (!businessUserId) {
    senderList.value = []
    return
  }
  try {
    const res = await request.get(`/business-customer/listByBusinessUserIdAndType/${businessUserId}/0`)
    senderList.value = res.data || res
  } catch (error) {
    ElMessage.error('获取发件人列表失败，请稍后重试')
  }
}

const getRecipientList = async (businessUserId) => {
  if (!businessUserId) {
    recipientList.value = []
    return
  }
  try {
    const res = await request.get(`/business-customer/listByBusinessUserIdAndType/${businessUserId}/1`)
    recipientList.value = res.data || res
  } catch (error) {
    ElMessage.error('获取收件人列表失败，请稍后重试')
  }
}

const getBusinessCustomerList = async (businessUserId) => {
  if (!businessUserId) {
    businessCustomerList.value = []
    return
  }
  try {
    const res = await request.get(`/business-customer/listByBusinessUserId/${businessUserId}`)
    businessCustomerList.value = res.data || res
  } catch (error) {
    ElMessage.error('获取业务客户列表失败，请稍后重试')
  }
}

/**
 * 处理业务用户变更
 * @param businessUserId 业务用户ID
 * @returns {Promise<void>}
 */
const handleBusinessUserChange = async (businessUserId) => {
  await getSenderList(businessUserId)
  await getRecipientList(businessUserId)
  await getBusinessCustomerList(businessUserId)
  const businessUser = businessUserList.value.find(user => user.id === businessUserId)
  if (businessUser) {
    form.value.senderName = businessUser.username
    form.value.senderPhone = businessUser.phone
    form.value.senderAddress = businessUser.address
  }
}

/**
 * 处理发件人变更
 * @param senderId 发件人ID
 */
const handleSenderChange = (senderId) => {
  const sender = senderList.value.find(sender => sender.id === senderId)
  if (sender) {
    form.value.senderName = sender.contact
    form.value.senderPhone = sender.contact
    form.value.senderAddress = sender.address
  }
  if (form.value.startNetworkId) {
    form.value.status = 1
  }
}

/**
 * 处理收件人变更
 * @param recipientId 收件人ID
 */
const handleRecipientChange = (recipientId) => {
  const recipient = recipientList.value.find(recipient => recipient.id === recipientId)
  if (recipient) {
    form.value.receiverName = recipient.contact
    form.value.receiverPhone = recipient.contact
    form.value.receiverAddress = recipient.address
  }
}

const handleAddBusinessUser = () => {
  dialogVisible.value = false
  router.push('/business-user')
}

const handleAddBusinessCustomer = () => {
  if (!form.value.businessUserId) {
    ElMessage.warning('请先选择业务用户')
    return
  }
  dialogVisible.value = false
  router.push('/business-customer')
}

/**
 * 打开添加收件人对话框
 */
const handleAddRecipient = () => {
  if (!form.value.businessUserId) {
    ElMessage.warning('请先选择业务用户')
    return
  }
  recipientForm.value = {
    businessUserId: form.value.businessUserId,
    type: 1 // 设置为收件人类型
  }
  recipientDialogVisible.value = true
}

/**
 * 处理业务客户变更
 * @param businessCustomerId 业务客户ID
 */
const handleBusinessCustomerChange = (businessCustomerId) => {
  const businessCustomer = businessCustomerList.value.find(customer => customer.id === businessCustomerId)
  if (businessCustomer) {
    form.value.receiverName = businessCustomer.contact
    form.value.receiverPhone = businessCustomer.phone
    form.value.receiverAddress = businessCustomer.address
  }
}

const handleAddRecipientSubmit = async () => {
  try {
    await request.post('/business-customer', recipientForm.value)
    recipientDialogVisible.value = false
    await getRecipientList(form.value.businessUserId)
    ElMessage.success('添加收件人成功')
  } catch (error) {
    ElMessage.error('添加收件人失败，请稍后重试')
  }
}

/**
 * 打开新增订单对话框
 */
const handleAdd = () => {
  form.value = { status: 0 }
  recipientList.value = []
  businessCustomerList.value = []
  priceBreakdown.value = null
  fileList.value = []
  imageList.value = []
  deliveryFileList.value = []
  deliveryImageList.value = []
  dialogVisible.value = true
  nextTick(() => {
    generateQrCode()
  })
}

const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

const handleBatchExport = () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请先选择要导出的订单')
    return
  }
  
  const exportData = selectedOrders.value.map(order => ({
    '订单编号': order.orderNo || '',
    '订单类型': order.orderType === '0' ? '零担' : order.orderType === '1' ? '整车' : '',
    '状态': getStatusText(order.status),
    '付款方式': order.paymentMethod === '0' ? '现付' : order.paymentMethod === '1' ? '到付' : order.paymentMethod === '2' ? '月结' : '',
    '网点付款方式': order.networkPaymentMethod === 0 ? '现付' : order.networkPaymentMethod === 1 ? '欠付' : order.networkPaymentMethod === 2 ? '到付' : order.networkPaymentMethod === 3 ? '回单付' : '',
    '发件人': order.senderName || '',
    '发件人电话': order.senderPhone || '',
    '发件人地址': order.senderAddress || '',
    '收件人': order.recipientName || '',
    '收件人电话': order.recipientPhone || '',
    '收件人地址': order.recipientAddress || '',
    '货物名称': order.goodsName || '',
    '数量': order.quantity || '',
    '重量(kg)': order.weight || '',
    '体积(m³)': order.volume || '',
    '距离(km)': order.distance || '',
    '运费': order.freight || '',
    '确认费用': order.totalFee || '',
    '创建时间': order.createTime ? new Date(order.createTime).toLocaleString() : '',
    '更新时间': order.updateTime ? new Date(order.updateTime).toLocaleString() : ''
  }))
  
  const ws = XLSX.utils.json_to_sheet(exportData)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '订单数据')
  XLSX.writeFile(wb, `订单导出_${new Date().toLocaleDateString().replace(/\//g, '-')}.xlsx`)
  ElMessage.success(`成功导出 ${exportData.length} 条订单数据`)
}

const generateQrCode = async () => {
  if (!qrcodeCanvas.value) return
  
  const orderNo = form.value.orderNo
  if (!orderNo) {
    const canvas = qrcodeCanvas.value
    const ctx = canvas.getContext('2d')
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    return
  }
  
  try {
    await QRCode.toCanvas(qrcodeCanvas.value, orderNo, {
      width: 80,
      margin: 1
    })
  } catch (err) {
    console.error('生成二维码失败:', err)
  }
}

const handleDownloadQrCode = () => {
  if (!qrcodeCanvas.value) return
  
  const link = document.createElement('a')
  link.download = `订单二维码_${form.value.orderNo}.png`
  link.href = qrcodeCanvas.value.toDataURL('image/png')
  link.click()
}

const getStatusType = (status) => {
  const statusMap = {
    0: 'info',
    1: 'warning',
    2: 'success'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '已创建',
    1: '网点已确认',
    2: '已完成'
  }
  return textMap[status] !== undefined ? textMap[status] : '已创建'
}

const clearFee = () => {
  priceBreakdown.value = null
}

const handleCalculatePrice = () => {
  const { weight, volume, distance, orderType } = form.value
  if ((!weight || weight === 0) && (!volume || volume === 0)) {
    ElMessage.warning('请至少填写货物重量或体积')
    return
  }

  const config = pricingConfig
  const w = parseFloat(weight) || 0
  const v = parseFloat(volume) || 0
  const d = parseFloat(distance) || 0
  const type = orderType === '1' || orderType === 1 ? 1 : 0

  let baseFee = config.basePrice

  let weightFee = 0
  if (w > 0) {
    if (w <= config.weightThresholds.light) {
      weightFee = w * config.pricePerKg * 0.8
    } else if (w <= config.weightThresholds.medium) {
      weightFee = w * config.pricePerKg
    } else if (w <= config.weightThresholds.heavy) {
      weightFee = w * config.pricePerKg * 1.2
    } else {
      weightFee = w * config.pricePerKg * 1.5
    }
  }

  let volumeFee = 0
  if (v > 0) {
    if (v <= config.volumeThresholds.small) {
      volumeFee = v * config.pricePerVolume * 0.8
    } else if (v <= config.volumeThresholds.medium) {
      volumeFee = v * config.pricePerVolume
    } else if (v <= config.volumeThresholds.large) {
      volumeFee = v * config.pricePerVolume * 1.2
    } else {
      volumeFee = v * config.pricePerVolume * 1.5
    }
  }

  let distanceFee = 0
  if (d > 0) {
    if (d <= config.distanceThresholds.short) {
      distanceFee = d * config.pricePerKm * 1.2
    } else if (d <= config.distanceThresholds.medium) {
      distanceFee = d * config.pricePerKm
    } else if (d <= config.distanceThresholds.long) {
      distanceFee = d * config.pricePerKm * 0.8
    } else {
      distanceFee = d * config.pricePerKm * 0.6
    }
  }

  let totalFee = baseFee + weightFee + volumeFee + distanceFee

  if (type === 1) {
    totalFee = totalFee * 1.5
  }

  if (totalFee < config.minPrice) {
    totalFee = config.minPrice
  }

  totalFee = Math.round(totalFee * 100) / 100

  priceBreakdown.value = {
    baseFee: Math.round(baseFee * 100) / 100,
    weightFee: Math.round(weightFee * 100) / 100,
    volumeFee: Math.round(volumeFee * 100) / 100,
    distanceFee: Math.round(distanceFee * 100) / 100
  }

  form.value.totalFee = totalFee
  ElMessage.success('运费估算成功')
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/order/${id}`)
    getOrderList()
    ElMessage.success('删除订单成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除订单失败，请稍后重试')
    }
  }
}

/**
 * 处理查询
 */
const handleSearch = () => {
  getOrderList()
}

/**
 * 处理分页大小变更
 * @param size 每页大小
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  getOrderList()
}

/**
 * 处理页码变更
 * @param current 当前页码
 */
const handleCurrentChange = (current) => {
  currentPage.value = current
  getOrderList()
}

/**
 * 处理图片上传成功
 * @param response 上传响应
 * @param uploadFile 上传的文件
 * @param fileList 文件列表
 */
const handleUploadSuccess = async (response, uploadFile, fileList) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    await getOrderImageList(form.value.id)
  } else {
    ElMessage.error('上传失败: ' + response.message)
  }
}

/**
 * 处理图片上传失败
 * @param error 错误信息
 */
const handleUploadError = (error) => {
  ElMessage.error('上传失败，请稍后重试')
}

/**
 * 处理文件选择
 * @param file 文件对象
 * @param fileList 文件列表
 */
const handleFileChange = (file, files) => {
  // 校验文件类型和大小
  const isJpgOrPng = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG或PNG图片')
    return
  }
  const isLt10M = file.raw.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return
  }
  // 更新文件列表
  fileList.value = files
  // 提示用户需要先保存订单
  if (!form.value.id) {
    ElMessage.warning('请先保存订单信息，然后再上传图片')
  }
}

/**
 * 手动上传文件
 */
const handleManualUpload = async () => {
  if (!form.value.id) {
    ElMessage.warning('请先保存订单信息')
    return
  }

  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  try {
    for (const file of fileList.value) {
      const formData = new FormData()
      formData.append('orderId', form.value.id)
      formData.append('orderNo', form.value.orderNo)
      formData.append('imageType', 1)
      formData.append('file', file.raw)

      await request.post('/order/image/upload/cos', formData)
    }

    await getOrderImageList(form.value.id)
    fileList.value = []
    form.value.status = 2
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败，请稍后重试')
  }
}

const handleDeliveryFileChange = (file, files) => {
  const isJpgOrPng = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG或PNG图片')
    return
  }
  const isLt10M = file.raw.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return
  }
  deliveryFileList.value = files
  if (!form.value.id) {
    ElMessage.warning('请先保存订单信息，然后再上传图片')
  }
}

const handleDeliveryUpload = async () => {
  if (!form.value.id) {
    ElMessage.warning('请先保存订单信息')
    return
  }

  if (deliveryFileList.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  try {
    for (const file of deliveryFileList.value) {
      const formData = new FormData()
      formData.append('orderId', form.value.id)
      formData.append('orderNo', form.value.orderNo)
      formData.append('imageType', 2)
      formData.append('file', file.raw)

      await request.post('/order/image/upload/cos', formData)
    }

    await getDeliveryImageList(form.value.id)
    deliveryFileList.value = []
    if (imageList.value.length > 0 || deliveryImageList.value.length > 0) {
      form.value.status = 2
    }
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败，请稍后重试')
  }
}

const getDeliveryImageList = async (orderId) => {
  if (!orderId) {
    deliveryImageList.value = []
    return
  }
  try {
    const res = await request.get('/order/image/list', {
      params: { orderId, imageType: 2 }
    })
    deliveryImageList.value = res.data || res || []
  } catch (error) {
    deliveryImageList.value = []
  }
}

const handleDeleteDeliveryImage = async (imageId) => {
  try {
    await ElMessageBox.confirm('确定要删除该图片吗？', '提示', { type: 'warning' })
    await request.delete(`/order/image/${imageId}`)
    await getDeliveryImageList(form.value.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 获取订单图片列表
 * @param orderId 订单ID
 * @returns {Promise<void>}
 */
const getOrderImageList = async (orderId) => {
  if (!orderId) {
    imageList.value = []
    return
  }
  try {
    const res = await request.get('/order/image/list', {
      params: { orderId }
    })
    imageList.value = res.data?.data || res.data || []
  } catch (error) {
    ElMessage.error('获取图片列表失败，请稍后重试')
  }
}

/**
 * 删除图片
 * @param id 图片ID
 * @returns {Promise<void>}
 */
const handleDeleteImage = async (id) => {
  try {
    await request.delete(`/order/image/${id}`)
    await getOrderImageList(form.value.id)
    ElMessage.success('删除图片成功')
  } catch (error) {
    ElMessage.error('删除图片失败，请稍后重试')
  }
}

/**
 * 打开编辑订单对话框
 * @param row 订单数据
 */
const handleEdit = (row) => {
  form.value = { ...row }
  if (row.businessUserId) {
    getSenderList(row.businessUserId)
    getRecipientList(row.businessUserId)
    getBusinessCustomerList(row.businessUserId)
  }
  getOrderImageList(row.id)
  getDeliveryImageList(row.id)
  dialogVisible.value = true
  nextTick(() => {
    generateQrCode()
  })
}

/**
 * 提交订单表单
 * @returns {Promise<void>}
 */
const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/order', form.value)
      ElMessage.success('更新订单成功')
    } else {
      const res = await request.post('/order', form.value)
      ElMessage.success('新增订单成功')
      form.value = res.data || res
      getOrderImageList(form.value.id)
      dialogVisible.value = true
    }
    const updatedOrder = await request.get(`/order/${form.value.id}`)
    form.value = updatedOrder
    nextTick(() => {
      generateQrCode()
    })
    dialogVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('保存订单失败，请稍后重试')
  }
}

/**
 * 组件挂载时初始化数据
 */
onMounted(() => {
  getOrderList()
  getBusinessUserList()
  getNetworkPointList()
})
</script>

<style scoped>
.order-detail-dialog :deep(.el-dialog__header) {
  padding: 4px 4px;
  margin: 0;
  background-color: #f5f7fa;
}

.order-detail-dialog :deep(.el-dialog__title) {
  font-size: 13px;
  font-weight: 600;
}

.order-detail-dialog :deep(.el-dialog__body) {
  padding: 8px 12px;
  max-height: 94vh;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.upload-demo {
  margin-bottom: 20px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.image-item-wrapper {
  position: relative;
  width: 120px;
}

.image-item {
  width: 120px;
  height: 120px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.image-actions {
  margin-top: 4px;
  text-align: center;
}

.no-images {
  color: #909399;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}

.image-footer {
  padding: 4px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  text-align: center;
}

.order-detail-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 85vh;
  overflow-y: auto;
  padding: 8px 8px 0 0;
}

.section-card {
  border-radius: 8px;
  min-height: 180px;
}

.section-card :deep(.el-card__header) {
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.section-card :deep(.el-card__body) {
  padding: 16px;
  min-height: 120px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.section-header .el-icon {
  font-size: 18px;
  color: #409eff;
}

.section-header.sender .el-icon {
  color: #67c23a;
}

.section-header.receiver .el-icon {
  color: #e6a23c;
}

.section-header.goods .el-icon {
  color: #909399;
}

.section-header.fee .el-icon {
  color: #f56c6c;
}

.sender-card {
  border-left: 4px solid #67c23a;
  min-height: 180px;
}

.receiver-card {
  border-left: 4px solid #e6a23c;
  min-height: 180px;
}

.goods-card {
  border-left: 4px solid #909399;
  min-height: 140px;
}

.fee-card {
  border-left: 4px solid #f56c6c;
  min-height: 140px;
}

.image-card {
  border-left: 4px solid #409eff;
  min-height: 140px;
}

.image-card :deep(.el-card__body) {
  padding: 10px;
}

.uploaded-images-form {
  flex: 1;
  min-height: 100px;
}

.uploaded-images-form :deep(.el-form-item__content) {
  max-height: 120px;
  overflow-y: auto;
}

.upload-form {
  margin-bottom: 0;
}

.upload-form :deep(.el-upload-dragger) {
  padding: 10px;
}

.network-card {
  border-left: 4px solid #909399;
  min-height: 140px;
}

.network-card :deep(.el-card__body) {
  padding: 12px;
}

.network-list {
  margin-top: 8px;
}

.fee-summary {
  display: flex;
  justify-content: center;
  padding: 20px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
  border-radius: 8px;
}

.fee-item.total-fee {
  text-align: center;
}

.fee-label {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 4px;
}

.fee-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
}

.image-badge {
  margin-left: 8px;
}

.estimated-price {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
  background-color: #fef0f0;
  padding: 4px 12px;
  border-radius: 4px;
}

.no-estimate {
  padding: 20px;
  text-align: center;
  color: #909399;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.price-breakdown {
  margin-top: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.price-breakdown .el-divider {
  margin: 8px 0;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 14px;
  color: #606266;
}

.breakdown-item span:last-child {
  font-weight: 500;
  color: #303133;
}

.upload-demo :deep(.el-upload-dragger) {
  padding: 20px;
}

.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px;
  background-color: #f5f7fa;
  border-radius: 4px;
  height: 100%;
}

.qrcode-canvas {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  width: 80px;
  height: 80px;
}
</style>