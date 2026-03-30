<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>线路管理</span>
          <el-button type="primary" @click="handleAdd">新增线路</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchForm.startCity"
          placeholder="始发城市"
          style="width: 150px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        ></el-input>
        <el-input
          v-model="searchForm.destinationCity"
          placeholder="目的城市"
          style="width: 150px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="routeList" style="width: 100%; margin-top: 15px;">
        <el-table-column prop="id" label="线路ID" width="80"></el-table-column>
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
        <el-table-column label="沿线网点" width="120">
          <template #default="scope">
            <el-button type="text" @click="handleManageNetworks(scope.row)">
              {{ scope.row.networkPoints?.length || 0 }}个网点
            </el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" title="线路详情" width="500px">
      <el-form :model="form" label-width="100px">
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

    <el-dialog v-model="networkDialogVisible" title="管理沿线网点" width="700px">
      <div style="margin-bottom: 15px;">
        <span style="color: #909399;">当前线路：{{ currentRoute.startCity }} → {{ currentRoute.destinationCity }}</span>
      </div>
      <el-transfer
        v-model="selectedNetworkIds"
        :data="networkList"
        :props="{ key: 'id', label: 'name' }"
        :titles="['可添加网点', '已关联网点']"
        filterable
        filter-placeholder="搜索网点"
      >
        <template #default="{ option }">
          {{ option.name }} ({{ option.city }})
        </template>
      </el-transfer>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="networkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveNetworks">确定</el-button>
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
const networkDialogVisible = ref(false)
const currentRoute = ref({})
const selectedNetworkIds = ref([])
const searchForm = ref({
  startCity: '',
  destinationCity: ''
})

const getNetworkName = (id) => {
  const network = networkList.value.find(n => n.id === id)
  return network ? network.name : `网点${id}`
}

const getNetworkList = async () => {
  try {
    const res = await request.get('/network/list')
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
        size: pageSize.value,
        startCity: searchForm.value.startCity || null,
        destinationCity: searchForm.value.destinationCity || null
      }
    })
    const records = res.records || res.data?.records || []
    routeList.value = records
    for (const route of routeList.value) {
      try {
        const networkRes = await request.get(`/route/${route.id}/networks`)
        route.networkPoints = networkRes || []
      } catch (e) {
        route.networkPoints = []
      }
    }
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取线路列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  getRouteList()
}

const handleReset = () => {
  searchForm.value = {
    startCity: '',
    destinationCity: ''
  }
  handleSearch()
}

const handleAdd = () => {
  form.value = {
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
    if (!form.value.startCity || !form.value.destinationCity) {
      ElMessage.warning('请填写始发城市和目的城市')
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

const handleManageNetworks = async (row) => {
  currentRoute.value = row
  selectedNetworkIds.value = row.networkPoints?.map(np => np.id) || []
  networkDialogVisible.value = true
}

const handleSaveNetworks = async () => {
  try {
    const currentIds = currentRoute.value.networkPoints?.map(np => np.id) || []
    const toAdd = selectedNetworkIds.value.filter(id => !currentIds.includes(id))
    const toRemove = currentIds.filter(id => !selectedNetworkIds.value.includes(id))
    for (const networkId of toAdd) {
      await request.post(`/route/${currentRoute.value.id}/networks/${networkId}`)
    }
    for (const networkId of toRemove) {
      await request.delete(`/route/${currentRoute.value.id}/networks/${networkId}`)
    }
    ElMessage.success('网点关联保存成功')
    networkDialogVisible.value = false
    getRouteList()
  } catch (error) {
    ElMessage.error('保存网点关联失败，请稍后重试')
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

.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.dialog-footer {
  text-align: right;
}
</style>
