<template>
  <div class="network-info-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <el-icon><OfficeBuilding /></el-icon>
              <span>网点基本信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border v-if="networkInfo">
            <el-descriptions-item label="网点编码">{{ networkInfo.code }}</el-descriptions-item>
            <el-descriptions-item label="网点名称">{{ networkInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="所在城市">{{ networkInfo.city }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ networkInfo.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ networkInfo.phone }}</el-descriptions-item>
            <el-descriptions-item label="网点地址">{{ networkInfo.address }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ networkInfo.createTime }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="networkInfo.status === 1 ? 'success' : 'info'">
                {{ networkInfo.status === 1 ? '营业中' : '已停用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="route-card">
          <template #header>
            <div class="card-header">
              <el-icon><Location /></el-icon>
              <span>线路信息</span>
            </div>
          </template>
          <el-table :data="routeList" v-loading="loading" stripe>
            <el-table-column prop="routeName" label="线路名称" min-width="150" />
            <el-table-column prop="startCity" label="起点城市" width="120" />
            <el-table-column prop="destinationCity" label="目的城市" width="120" />
            <el-table-column prop="basePrice" label="基础价格" width="100" align="center">
              <template #default="scope">¥{{ scope.row.basePrice }}</template>
            </el-table-column>
            <el-table-column prop="pricePerKg" label="单价(元/kg)" width="100" align="center" />
            <el-table-column prop="transitDays" label="运输天数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { OfficeBuilding, Location } from '@element-plus/icons-vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const networkInfo = ref(null)
const routeList = ref([])

const loadNetworkInfo = async () => {
  try {
    const networkId = localStorage.getItem('networkId')
    if (!networkId) {
      ElMessage.warning('请先登录网点账号')
      return
    }
    const res = await request.get(`/network/${networkId}`)
    networkInfo.value = res
  } catch (error) {
    console.error('获取网点信息失败', error)
    ElMessage.error('获取网点信息失败')
  }
}

const loadRouteList = async () => {
  loading.value = true
  try {
    const networkId = localStorage.getItem('networkId')
    if (!networkId) {
      loading.value = false
      return
    }
    const res = await request.get('/network/routes', {
      params: { networkId }
    })
    routeList.value = res || []
  } catch (error) {
    console.error('获取线路信息失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNetworkInfo()
  loadRouteList()
})
</script>

<style scoped>
.network-info-page {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 16px;
}
</style>
