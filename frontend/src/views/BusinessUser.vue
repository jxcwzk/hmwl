<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>业务用户管理</span>
          <el-button type="primary" @click="handleAdd">新增业务用户</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="businessUserList" style="width: 100%">
        <el-table-column prop="id" label="业务用户ID" width="120"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="phone" label="手机号"></el-table-column>
        <el-table-column prop="wechat" label="微信号"></el-table-column>
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

    <el-dialog v-model="dialogVisible" title="业务用户详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="form.wechat"></el-input>
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
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const businessUserList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})
const searchForm = ref({})

const getBusinessUserList = async () => {
  loading.value = true
  try {
    const res = await request.get('/business-user/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        ...searchForm.value
      }
    })
    businessUserList.value = res.records || res.data?.records || []
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
    await ElMessageBox.confirm('确定要删除该业务用户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/business-user/${id}`)
    getBusinessUserList()
    ElMessage.success('删除业务用户成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除业务用户失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/business-user', form.value)
      ElMessage.success('更新业务用户成功')
    } else {
      await request.post('/business-user', form.value)
      ElMessage.success('新增业务用户成功')
    }
    dialogVisible.value = false
    getBusinessUserList()
  } catch (error) {
    ElMessage.error('保存业务用户失败，请稍后重试')
  }
}

const handleSearch = () => {
  getBusinessUserList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getBusinessUserList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getBusinessUserList()
}

onMounted(() => {
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