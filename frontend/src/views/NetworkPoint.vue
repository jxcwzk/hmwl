<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>网点管理</span>
          <el-button type="primary" @click="handleAdd">新增网点</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="networkPointList" style="width: 100%">
        <el-table-column prop="id" label="网点ID" width="100"></el-table-column>
        <el-table-column prop="code" label="网点编码"></el-table-column>
        <el-table-column prop="name" label="网点名称"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
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

    <el-dialog v-model="dialogVisible" title="网点详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="网点编码">
          <el-input v-model="form.code"></el-input>
        </el-form-item>
        <el-form-item label="网点名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea"></el-input>
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
const networkPointList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})

const getNetworkPointList = async () => {
  loading.value = true
  try {
    const res = await request.get('/network-point/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    networkPointList.value = res.records || res.data?.records || []
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
    await ElMessageBox.confirm('确定要删除该网点吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/network-point/${id}`)
    getNetworkPointList()
    ElMessage.success('删除网点成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除网点失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/network-point', form.value)
      ElMessage.success('更新网点成功')
    } else {
      await request.post('/network-point', form.value)
      ElMessage.success('新增网点成功')
    }
    dialogVisible.value = false
    getNetworkPointList()
  } catch (error) {
    ElMessage.error('保存网点失败，请稍后重试')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getNetworkPointList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getNetworkPointList()
}

onMounted(() => {
  getNetworkPointList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>
