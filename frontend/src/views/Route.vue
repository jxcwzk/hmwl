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
        <el-table-column prop="id" label="线路ID" width="100"></el-table-column>
        <el-table-column prop="startPointId" label="始发网点ID"></el-table-column>
        <el-table-column prop="endPointId" label="到达网点ID"></el-table-column>
        <el-table-column prop="isTrunk" label="是否干线">
          <template #default="scope">
            {{ scope.row.isTrunk ? '是' : '否' }}
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

    <el-dialog v-model="dialogVisible" title="线路详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="始发网点ID">
          <el-input v-model="form.startPointId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="到达网点ID">
          <el-input v-model="form.endPointId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="是否干线">
          <el-switch v-model="form.isTrunk"></el-switch>
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
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})

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
