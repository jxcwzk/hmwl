<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>线路管理</span>
          <el-button type="primary" @click="handleAdd">新增线路</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="routeList" style="width: 100%">
        <el-table-column prop="id" label="线路ID" width="80"></el-table-column>
        <el-table-column prop="networkPointId" label="始发网点" width="150">
          <template #default="scope">
            {{ getNetworkName(scope.row.networkPointId) }}
          </template>
        </el-table-column>
        <el-table-column prop="startCity" label="始发城市" width="120"></el-table-column>
        <el-table-column prop="destinationCity" label="目的城市" width="120"></el-table-column>
        <el-table-column prop="basePrice" label="基础价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.basePrice }}
          </template>
        </el-table-column>
        <el-table-column prop="pricePerKg" label="单价/kg" width="100">
          <template #default="scope">
            ¥{{ scope.row.pricePerKg }}
          </template>
        </el-table-column>
        <el-table-column prop="transitDays" label="运输天数" width="100">
          <template #default="scope">
            {{ scope.row.transitDays }}天
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
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

    <el-dialog v-model="dialogVisible" title="线路详情" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="始发网点">
          <el-select v-model="form.networkPointId" placeholder="请选择始发网点">
            <el-option
              v-for="network in networkList"
              :key="network.id"
              :label="network.name + ' (' + network.city + ')'"
              :value="network.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="始发城市">
          <el-input v-model="form.startCity" placeholder="如：无锡市"></el-input>
        </el-form-item>
        <el-form-item label="目的城市">
          <el-input v-model="form.destinationCity" placeholder="如：北京市"></el-input>
        </el-form-item>
        <el-form-item label="基础价格">
          <el-input-number v-model="form.basePrice" :min="0" :step="10"></el-input-number>
        </el-form-item>
        <el-form-item label="单价/kg">
          <el-input-number v-model="form.pricePerKg" :min="0" :step="0.1" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="运输天数">
          <el-input-number v-model="form.transitDays" :min="1" :step="1"></el-input-number>
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
const routeList = ref([])
const networkList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})

const getNetworkName = (id) => {
  const network = networkList.value.find(n => n.id === id)
  return network ? network.name : `网点${id}`
}

const getNetworkList = async () => {
  try {
    const res = await request.get('/network-point/list')
    networkList.value = res || []
  } catch (error) {
    console.error('获取网点列表失败', error)
  }
}

const getRouteList = async () => {
  loading.value = true
  try {
    const res = await request.get('/route/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    routeList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取线路列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    networkPointId: '',
    startCity: '',
    destinationCity: '',
    basePrice: 0,
    pricePerKg: 0,
    transitDays: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该线路吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/route/${id}`)
    getRouteList()
    ElMessage.success('删除线路成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除线路失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (!form.value.networkPointId || !form.value.destinationCity) {
      ElMessage.warning('请填写完整信息')
      return
    }
    if (form.value.id) {
      await request.put('/route', form.value)
      ElMessage.success('更新线路成功')
    } else {
      await request.post('/route', form.value)
      ElMessage.success('新增线路成功')
    }
    dialogVisible.value = false
    getRouteList()
  } catch (error) {
    ElMessage.error('保存线路失败，请稍后重试')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getRouteList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getRouteList()
}

onMounted(() => {
  getNetworkList()
  getRouteList()
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
