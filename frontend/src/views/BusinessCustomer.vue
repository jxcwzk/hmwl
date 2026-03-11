<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>业务客户管理</span>
          <el-button type="primary" @click="handleAdd">新增业务客户</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="businessCustomerList" style="width: 100%">
        <el-table-column prop="id" label="客户ID" width="100"></el-table-column>
        <el-table-column prop="customerName" label="客户名称"></el-table-column>
        <el-table-column prop="contact" label="联系方式"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="businessUserId" label="业务用户ID"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
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

    <el-dialog v-model="dialogVisible" title="业务客户详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="客户名称">
          <el-input v-model="form.customerName"></el-input>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.contact"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="业务用户">
          <el-select v-model="form.businessUserId" placeholder="请选择业务用户">
            <el-option v-for="user in businessUserList" :key="user.id" :label="user.username" :value="user.id"></el-option>
          </el-select>
          <el-button type="primary" @click="handleAddBusinessUser" style="margin-left: 10px">新增业务用户</el-button>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="发件人" value="0"></el-option>
            <el-option label="收件人" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
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
const businessCustomerList = ref([])
const businessUserList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})
const searchForm = ref({})

const getBusinessUserList = async () => {
  try {
    const res = await request.get('/business-user/list')
    businessUserList.value = res.data || res
  } catch (error) {
    ElMessage.error('获取业务用户列表失败，请稍后重试')
  }
}

const handleAddBusinessUser = () => {
  router.push('/customer/business-user')
}

const getBusinessCustomerList = async () => {
  loading.value = true
  try {
    const res = await request.get('/business-customer/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        ...searchForm.value
      }
    })
    businessCustomerList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该业务客户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/business-customer/${id}`)
    getBusinessCustomerList()
    ElMessage.success('删除业务客户成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除业务客户失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/business-customer', form.value)
      ElMessage.success('更新业务客户成功')
    } else {
      await request.post('/business-customer', form.value)
      ElMessage.success('新增业务客户成功')
    }
    dialogVisible.value = false
    getBusinessCustomerList()
  } catch (error) {
    ElMessage.error('保存业务客户失败，请稍后重试')
  }
}

const handleSearch = () => {
  getBusinessCustomerList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getBusinessCustomerList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getBusinessCustomerList()
}

onMounted(() => {
  getBusinessCustomerList()
  getBusinessUserList()
})
</script>

<style scoped>
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
</style>