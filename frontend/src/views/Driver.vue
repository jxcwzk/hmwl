<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>司机管理</span>
          <el-button type="primary" @click="handleAdd">新增司机</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchForm.name"
          placeholder="姓名"
          style="width: 120px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        ></el-input>
        <el-input
          v-model="searchForm.phone"
          placeholder="电话"
          style="width: 120px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="driverList" style="width: 100%; margin-top: 15px;">
        <el-table-column prop="id" label="司机ID" width="100"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="idCard" label="身份证号"></el-table-column>
        <el-table-column prop="vehicleId" label="绑定车辆ID"></el-table-column>
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

    <el-dialog v-model="dialogVisible" title="司机详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard"></el-input>
        </el-form-item>
        <el-form-item label="绑定车辆ID">
          <el-input v-model="form.vehicleId" type="number"></el-input>
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
const driverList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})
const searchForm = ref({
  name: '',
  phone: ''
})

const getDriverList = async () => {
  loading.value = true
  try {
    const res = await request.get('/driver/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        name: searchForm.value.name || null,
        phone: searchForm.value.phone || null
      }
    })
    driverList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  getDriverList()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    phone: ''
  }
  handleSearch()
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
    await ElMessageBox.confirm('确定要删除该司机吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/driver/${id}`)
    getDriverList()
    ElMessage.success('删除司机成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除司机失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/driver', form.value)
      ElMessage.success('更新司机成功')
    } else {
      await request.post('/driver', form.value)
      ElMessage.success('新增司机成功')
    }
    dialogVisible.value = false
    getDriverList()
  } catch (error) {
    ElMessage.error('保存司机失败，请稍后重试')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getDriverList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getDriverList()
}

onMounted(() => {
  getDriverList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.dialog-footer {
  text-align: right;
}
</style>
