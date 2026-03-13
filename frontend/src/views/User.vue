<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增系统用户</el-button>
            <el-button type="success" @click="loadMiniprogramUsers">刷新小程序用户</el-button>
          </div>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="全部" clearable>
            <el-option label="管理员" :value="1"></el-option>
            <el-option label="客户" :value="2"></el-option>
            <el-option label="司机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="userList" style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.userType === 1" type="danger">管理员</el-tag>
            <el-tag v-else-if="scope.row.userType === 2" type="success">客户</el-tag>
            <el-tag v-else-if="scope.row.userType === 3" type="warning">司机</el-tag>
            <el-tag v-else type="info">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">已激活</el-tag>
            <el-tag v-else type="info">未激活</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
        <el-table-column prop="wechat" label="微信号" width="120"></el-table-column>
        <el-table-column prop="openid" label="微信OpenID" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170"></el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleAssignRole(scope.row)">分配角色</el-button>
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

    <el-dialog v-model="dialogVisible" title="用户详情">
      <el-form :model="form" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="form.userType">
            <el-option label="管理员" :value="1"></el-option>
            <el-option label="客户" :value="2"></el-option>
            <el-option label="司机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="未激活" :value="0"></el-option>
            <el-option label="已激活" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="form.wechat"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="留空不修改"></el-input>
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

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="roleForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="roleForm.userType" style="width: 100%">
            <el-option label="未分配" :value="0"></el-option>
            <el-option label="管理员" :value="1"></el-option>
            <el-option label="客户" :value="2"></el-option>
            <el-option label="司机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="roleForm.status" style="width: 100%">
            <el-option label="未激活" :value="0"></el-option>
            <el-option label="已激活" :value="1"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitRole">确定分配</el-button>
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
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const form = ref({})
const roleForm = ref({})
const searchForm = ref({})

const getUserList = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        ...searchForm.value
      }
    })
    userList.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadMiniprogramUsers = () => {
  getUserList()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleAssignRole = (row) => {
  roleForm.value = {
    userId: row.id,
    username: row.username,
    userType: row.userType || 0,
    status: row.status || 0
  }
  roleDialogVisible.value = true
}

const handleSubmitRole = async () => {
  try {
    await request.post('/user/updateRole', {
      userId: roleForm.value.userId,
      userType: roleForm.value.userType,
      status: roleForm.value.status
    })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('角色分配失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/user/${id}`)
    getUserList()
    ElMessage.success('删除用户成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败，请稍后重试')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/user', form.value)
      ElMessage.success('更新用户成功')
    } else {
      await request.post('/user', form.value)
      ElMessage.success('新增用户成功')
    }
    dialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('保存用户失败，请稍后重试')
  }
}

const handleSearch = () => {
  getUserList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getUserList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getUserList()
}

onMounted(() => {
  getUserList()
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