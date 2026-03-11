<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆管理</span>
          <el-button type="primary" @click="handleAdd">新增车辆</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="vehicleList" style="width: 100%">
        <el-table-column prop="id" label="车辆ID" width="100"></el-table-column>
        <el-table-column prop="licensePlate" label="车牌号"></el-table-column>
        <el-table-column prop="vehicleType" label="车型"></el-table-column>
        <el-table-column prop="loadCapacity" label="载重"></el-table-column>
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

    <el-dialog v-model="dialogVisible" title="车辆详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="车牌号">
          <el-input v-model="form.licensePlate"></el-input>
        </el-form-item>
        <el-form-item label="车型">
          <el-input v-model="form.vehicleType"></el-input>
        </el-form-item>
        <el-form-item label="载重">
          <el-input v-model="form.loadCapacity" type="number"></el-input>
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
const vehicleList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})

const getVehicleList = async () => {
  loading.value = true
  try {
    const res = await request.get('/vehicle/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    vehicleList.value = res.records || res.data?.records || []
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
    await ElMessageBox.confirm('确定要删除该车辆吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/vehicle/${id}`)
    getVehicleList()
    ElMessage.success('删除车辆成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除车辆失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/vehicle', form.value)
      ElMessage.success('更新车辆成功')
    } else {
      await request.post('/vehicle', form.value)
      ElMessage.success('新增车辆成功')
    }
    dialogVisible.value = false
    getVehicleList()
  } catch (error) {
    ElMessage.error('保存车辆失败，请稍后重试')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getVehicleList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getVehicleList()
}

onMounted(() => {
  getVehicleList()
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
