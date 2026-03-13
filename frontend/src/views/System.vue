<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统管理</span>
        </div>
      </template>
      <el-tabs>
        <el-tab-pane label="用户管理">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="用户名">
              <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
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
              <el-button type="success" @click="handleAddUser">新增用户</el-button>
            </el-form-item>
          </el-form>
          <el-table v-loading="userLoading" :data="userList" style="width: 100%">
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
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleEditUser(scope.row)">编辑</el-button>
                <el-button type="success" size="small" @click="handleAssignRole(scope.row)">分配角色</el-button>
                <el-button type="danger" size="small" @click="handleDeleteUser(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="userCurrentPage"
            v-model:page-size="userPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="userTotal"
            @size-change="handleUserSizeChange"
            @current-change="handleUserCurrentChange"
          ></el-pagination>
        </el-tab-pane>

        <el-tab-pane label="角色管理">
          <el-button type="primary" @click="handleAddRole">新增角色</el-button>
          <el-table v-loading="roleLoading" :data="roleList" style="width: 100%">
            <el-table-column prop="id" label="角色ID" width="100"></el-table-column>
            <el-table-column prop="name" label="角色名称" width="150"></el-table-column>
            <el-table-column prop="code" label="角色代码" width="150"></el-table-column>
            <el-table-column prop="description" label="角色描述"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
                <el-tag v-else type="danger">禁用</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170"></el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleEditRole(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteRole(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="菜单管理">
          <el-button type="primary" @click="handleAddMenu">新增菜单</el-button>
          <el-table v-loading="menuLoading" :data="menuList" style="width: 100%">
            <el-table-column prop="id" label="菜单ID" width="100"></el-table-column>
            <el-table-column prop="name" label="菜单名称" width="150"></el-table-column>
            <el-table-column prop="path" label="菜单路径"></el-table-column>
            <el-table-column prop="icon" label="菜单图标" width="120"></el-table-column>
            <el-table-column prop="sort" label="排序" width="80"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
                <el-tag v-else type="danger">禁用</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleEditMenu(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteMenu(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="日志管理">
          <el-table :data="logList" style="width: 100%">
            <el-table-column prop="id" label="日志ID" width="100"></el-table-column>
            <el-table-column prop="username" label="操作用户"></el-table-column>
            <el-table-column prop="operation" label="操作内容"></el-table-column>
            <el-table-column prop="createTime" label="操作时间"></el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 用户管理弹窗 -->
    <el-dialog v-model="userDialogVisible" :title="userDialogTitle" width="500px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username"></el-input>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="userForm.userType" style="width: 100%">
            <el-option label="未分配" :value="0"></el-option>
            <el-option label="管理员" :value="1"></el-option>
            <el-option label="客户" :value="2"></el-option>
            <el-option label="司机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="userForm.status" style="width: 100%">
            <el-option label="未激活" :value="0"></el-option>
            <el-option label="已激活" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="userForm.wechat"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="userForm.remark" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitUser">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="assignRoleDialogVisible" title="分配角色" width="400px">
      <el-form :model="assignRoleForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="assignRoleForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="assignRoleForm.userType" style="width: 100%">
            <el-option label="未分配" :value="0"></el-option>
            <el-option label="管理员" :value="1"></el-option>
            <el-option label="客户" :value="2"></el-option>
            <el-option label="司机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="assignRoleForm.status" style="width: 100%">
            <el-option label="未激活" :value="0"></el-option>
            <el-option label="已激活" :value="1"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignRoleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAssignRole">确定分配</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 角色管理弹窗 -->
    <el-dialog v-model="roleDialogVisible" :title="roleDialogTitle" width="500px">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="角色代码">
          <el-input v-model="roleForm.code" :disabled="!!roleForm.id"></el-input>
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="roleForm.description" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="roleForm.status" style="width: 100%">
            <el-option label="禁用" :value="0"></el-option>
            <el-option label="启用" :value="1"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitRole">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 菜单管理弹窗 -->
    <el-dialog v-model="menuDialogVisible" :title="menuDialogTitle" width="500px">
      <el-form :model="menuForm" label-width="100px">
        <el-form-item label="菜单名称">
          <el-input v-model="menuForm.name"></el-input>
        </el-form-item>
        <el-form-item label="菜单路径">
          <el-input v-model="menuForm.path"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-input v-model="menuForm.icon" placeholder="如: Document, Setting"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="menuForm.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="menuForm.status" style="width: 100%">
            <el-option label="禁用" :value="0"></el-option>
            <el-option label="启用" :value="1"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="menuDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitMenu">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const userLoading = ref(false)
const userList = ref([])
const userTotal = ref(0)
const userCurrentPage = ref(1)
const userPageSize = ref(10)
const userDialogVisible = ref(false)
const assignRoleDialogVisible = ref(false)
const userDialogTitle = ref('新增用户')
const userForm = ref({})
const assignRoleForm = ref({})
const searchForm = ref({})

const roleLoading = ref(false)
const roleList = ref([])
const roleDialogVisible = ref(false)
const roleDialogTitle = ref('新增角色')
const roleForm = ref({})

const menuLoading = ref(false)
const menuList = ref([])
const menuDialogVisible = ref(false)
const menuDialogTitle = ref('新增菜单')
const menuForm = ref({})

const logList = ref([
  { id: 1, username: 'admin', operation: '登录系统', createTime: '2026-03-07 21:00:00' },
  { id: 2, username: 'admin', operation: '新增订单', createTime: '2026-03-07 21:10:00' }
])

const getUserList = async () => {
  userLoading.value = true
  try {
    const res = await request.get('/user/page', {
      params: {
        current: userCurrentPage.value,
        size: userPageSize.value,
        ...searchForm.value
      }
    })
    userList.value = res.records || res.data?.records || []
    userTotal.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    userLoading.value = false
  }
}

const getRoleList = async () => {
  roleLoading.value = true
  try {
    const res = await request.get('/role/list')
    roleList.value = res || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    roleLoading.value = false
  }
}

const getMenuList = async () => {
  menuLoading.value = true
  try {
    const res = await request.get('/menu/list')
    menuList.value = res || []
  } catch (error) {
    console.error('获取菜单列表失败:', error)
  } finally {
    menuLoading.value = false
  }
}

const handleSearch = () => {
  userCurrentPage.value = 1
  getUserList()
}

const handleAddUser = () => {
  userForm.value = {}
  userDialogTitle.value = '新增用户'
  userDialogVisible.value = true
}

const handleEditUser = (row) => {
  userForm.value = { ...row }
  userDialogTitle.value = '编辑用户'
  userDialogVisible.value = true
}

const handleSubmitUser = async () => {
  try {
    if (userForm.value.id) {
      await request.put('/user', userForm.value)
      ElMessage.success('更新用户成功')
    } else {
      await request.post('/user', userForm.value)
      ElMessage.success('新增用户成功')
    }
    userDialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('保存用户失败')
  }
}

const handleDeleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/user/${id}`)
    getUserList()
    ElMessage.success('删除用户成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
    }
  }
}

const handleAssignRole = (row) => {
  assignRoleForm.value = {
    userId: row.id,
    username: row.username,
    userType: row.userType || 0,
    status: row.status || 0
  }
  assignRoleDialogVisible.value = true
}

const handleSubmitAssignRole = async () => {
  try {
    await request.post('/user/updateRole', {
      userId: assignRoleForm.value.userId,
      userType: assignRoleForm.value.userType,
      status: assignRoleForm.value.status
    })
    ElMessage.success('角色分配成功')
    assignRoleDialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('角色分配失败')
  }
}

const handleUserSizeChange = (size) => {
  userPageSize.value = size
  getUserList()
}

const handleUserCurrentChange = (current) => {
  userCurrentPage.value = current
  getUserList()
}

const handleAddRole = () => {
  roleForm.value = { status: 1 }
  roleDialogTitle.value = '新增角色'
  roleDialogVisible.value = true
}

const handleEditRole = (row) => {
  roleForm.value = { ...row }
  roleDialogTitle.value = '编辑角色'
  roleDialogVisible.value = true
}

const handleSubmitRole = async () => {
  try {
    if (roleForm.value.id) {
      await request.put('/role', roleForm.value)
      ElMessage.success('更新角色成功')
    } else {
      await request.post('/role', roleForm.value)
      ElMessage.success('新增角色成功')
    }
    roleDialogVisible.value = false
    getRoleList()
  } catch (error) {
    ElMessage.error('保存角色失败')
  }
}

const handleDeleteRole = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/role/${id}`)
    getRoleList()
    ElMessage.success('删除角色成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除角色失败')
    }
  }
}

const handleAddMenu = () => {
  menuForm.value = { status: 1, sort: 0 }
  menuDialogTitle.value = '新增菜单'
  menuDialogVisible.value = true
}

const handleEditMenu = (row) => {
  menuForm.value = { ...row }
  menuDialogTitle.value = '编辑菜单'
  menuDialogVisible.value = true
}

const handleSubmitMenu = async () => {
  try {
    if (menuForm.value.id) {
      await request.put('/menu', menuForm.value)
      ElMessage.success('更新菜单成功')
    } else {
      await request.post('/menu', menuForm.value)
      ElMessage.success('新增菜单成功')
    }
    menuDialogVisible.value = false
    getMenuList()
  } catch (error) {
    ElMessage.error('保存菜单失败')
  }
}

const handleDeleteMenu = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/menu/${id}`)
    getMenuList()
    ElMessage.success('删除菜单成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除菜单失败')
    }
  }
}

onMounted(() => {
  getUserList()
  getRoleList()
  getMenuList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 15px;
}
</style>
