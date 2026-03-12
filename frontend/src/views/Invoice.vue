<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发票管理</span>
          <el-button @click="goBack">返回结算</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="客户">
          <el-select v-model="filterForm.customerId" placeholder="请选择客户" clearable style="width: 180px;">
            <el-option v-for="user in businessUserList" :key="user.id" :label="user.username" :value="user.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发票状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="待开" :value="0"></el-option>
            <el-option label="已开" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间段">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="invoiceList" style="width: 100%">
        <el-table-column prop="id" label="发票ID" width="80"></el-table-column>
        <el-table-column prop="invoiceNo" label="发票编号" width="200"></el-table-column>
        <el-table-column prop="settlementId" label="结算ID" width="100"></el-table-column>
        <el-table-column prop="orderId" label="订单ID" width="100"></el-table-column>
        <el-table-column prop="customerId" label="客户ID" width="100"></el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待开</el-tag>
            <el-tag v-else type="success">已开</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="invoiceDate" label="开票日期" width="180"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">查看</el-button>
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

    <el-dialog v-model="dialogVisible" title="发票详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="发票编号">
          <el-input v-model="form.invoiceNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="结算ID">
          <el-input v-model="form.settlementId" disabled></el-input>
        </el-form-item>
        <el-form-item label="订单ID">
          <el-input v-model="form.orderId" disabled></el-input>
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="form.customerId" disabled></el-input>
        </el-form-item>
        <el-form-item label="金额">
          <el-input v-model="form.amount" disabled></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag v-if="form.status === 0" type="warning">待开</el-tag>
          <el-tag v-else type="success">已开</el-tag>
        </el-form-item>
        <el-form-item label="开票日期">
          <el-input v-model="form.invoiceDate" disabled></el-input>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input v-model="form.createTime" disabled></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const invoiceList = ref([])
const businessUserList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})
const dateRange = ref([])

const filterForm = ref({
  customerId: null,
  status: null,
  startDate: '',
  endDate: ''
})

const getBusinessUserList = async () => {
  try {
    const res = await request.get('/business-user/list')
    businessUserList.value = res.data || res
  } catch (error) {
    console.error('获取业务用户列表失败', error)
  }
}

const getInvoiceList = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (filterForm.value.customerId) {
      params.customerId = filterForm.value.customerId
    }
    if (filterForm.value.status !== null && filterForm.value.status !== '') {
      params.status = filterForm.value.status
    }
    if (filterForm.value.startDate) {
      params.startDate = filterForm.value.startDate
    }
    if (filterForm.value.endDate) {
      params.endDate = filterForm.value.endDate
    }
    const res = await request.get('/invoice/page', { params })
    invoiceList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  currentPage.value = 1
  getInvoiceList()
}

const handleReset = () => {
  filterForm.value = {
    customerId: null,
    status: null,
    startDate: '',
    endDate: ''
  }
  dateRange.value = []
  currentPage.value = 1
  getInvoiceList()
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    filterForm.value.startDate = val[0]
    filterForm.value.endDate = val[1]
  } else {
    filterForm.value.startDate = ''
    filterForm.value.endDate = ''
  }
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该发票吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/invoice/${id}`)
    getInvoiceList()
    ElMessage.success('删除发票成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除发票失败，请稍后重试')
    }
  }
}

const goBack = () => {
  router.push('/settlement')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getInvoiceList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getInvoiceList()
}

onMounted(() => {
  getBusinessUserList()
  getInvoiceList()
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
