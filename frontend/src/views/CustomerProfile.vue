<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户档案管理</span>
          <el-button type="primary" @click="handleAdd">新增档案</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="公司名称">
          <el-input v-model="filterForm.companyName" placeholder="请输入公司名称" clearable style="width: 180px;"></el-input>
        </el-form-item>
        <el-form-item label="信用评级">
          <el-select v-model="filterForm.creditRating" placeholder="请选择评级" clearable style="width: 150px;">
            <el-option label="1星" :value="1"></el-option>
            <el-option label="2星" :value="2"></el-option>
            <el-option label="3星" :value="3"></el-option>
            <el-option label="4星" :value="4"></el-option>
            <el-option label="5星" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="customerProfileList" style="width: 100%">
        <el-table-column prop="id" label="档案ID" width="80"></el-table-column>
        <el-table-column prop="companyName" label="公司名称" width="180"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100"></el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="address" label="地址" width="200"></el-table-column>
        <el-table-column prop="cooperationDuration" label="合作时长" width="100">
          <template #default="scope">
            {{ scope.row.cooperationDuration ? scope.row.cooperationDuration + '个月' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalOrders" label="历史订单" width="100"></el-table-column>
        <el-table-column prop="totalAmount" label="历史金额" width="120">
          <template #default="scope">
            ¥{{ (scope.row.totalAmount || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="creditRating" label="信用评级" width="100">
          <template #default="scope">
            <el-rate v-model="scope.row.creditRating" disabled :max="5"></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">有效</el-tag>
            <el-tag v-else type="info">无效</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="primary" @click="handleHistory(scope.row)">历史</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="业务用户" prop="businessUserId">
          <el-select v-model="form.businessUserId" placeholder="请选择业务用户" style="width: 100%;">
            <el-option v-for="user in businessUserList" :key="user.id" :label="user.username" :value="user.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入公司名称"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="合作时长">
          <el-input-number v-model="form.cooperationDuration" :min="0" placeholder="月"></el-input-number>
        </el-form-item>
        <el-form-item label="信用评级">
          <el-rate v-model="form.creditRating" :max="5"></el-rate>
        </el-form-item>
        <el-form-item label="收货习惯">
          <el-input v-model="form.receivingHabits" type="textarea" placeholder="请输入收货习惯"></el-input>
        </el-form-item>
        <el-form-item label="特殊要求">
          <el-input v-model="form.specialRequirements" type="textarea" placeholder="请输入特殊要求"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="historyDialogVisible" title="客户历史订单" width="800px">
      <el-table :data="orderHistoryList" v-loading="historyLoading">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="senderName" label="发货人" width="100"></el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100"></el-table-column>
        <el-table-column prop="goodsName" label="货物" width="120"></el-table-column>
        <el-table-column prop="totalFee" label="金额" width="100">
          <template #default="scope">
            ¥{{ (scope.row.totalFee || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="info">待处理</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="primary">已指派</el-tag>
            <el-tag v-else-if="scope.row.status === 5" type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const dialogVisible = ref(false)
const historyDialogVisible = ref(false)
const historyLoading = ref(false)
const dialogTitle = ref('新增档案')
const customerProfileList = ref([])
const businessUserList = ref([])
const orderHistoryList = ref([])
const formRef = ref(null)

const filterForm = ref({
  companyName: '',
  creditRating: null
})

const form = ref({
  id: null,
  businessUserId: null,
  companyName: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  cooperationDuration: 0,
  creditRating: 3,
  receivingHabits: '',
  specialRequirements: '',
  status: 1
})

const formRules = {
  businessUserId: [{ required: true, message: '请选择业务用户', trigger: 'change' }],
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/customer-profile/list')
    customerProfileList.value = res.data || res || []
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const loadBusinessUsers = async () => {
  try {
    const res = await request.get('/business-user/list')
    businessUserList.value = res.data || res || []
  } catch (error) {
    console.error('加载业务用户失败:', error)
  }
}

const handleFilter = () => {
  loadData()
}

const handleReset = () => {
  filterForm.value = { companyName: '', creditRating: null }
  loadData()
}

const handleAdd = () => {
  form.value = {
    id: null,
    businessUserId: null,
    companyName: '',
    contactPerson: '',
    contactPhone: '',
    address: '',
    cooperationDuration: 0,
    creditRating: 3,
    receivingHabits: '',
    specialRequirements: '',
    status: 1
  }
  dialogTitle.value = '新增档案'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogTitle.value = '编辑档案'
  dialogVisible.value = true
}

const handleView = (row) => {
  handleEdit(row)
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/customer-profile', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/customer-profile', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleHistory = async (row) => {
  historyDialogVisible.value = true
  historyLoading.value = true
  try {
    const res = await request.get(`/customer-profile/${row.id}/history`)
    orderHistoryList.value = res.orders || []
  } catch (error) {
    console.error('加载历史订单失败:', error)
  } finally {
    historyLoading.value = false
  }
}

onMounted(() => {
  loadData()
  loadBusinessUsers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-form {
  margin-bottom: 20px;
}
</style>
