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
          <el-button type="primary" @click="handleAddUser">新增用户</el-button>
          <el-table :data="userList" style="width: 100%">
            <el-table-column prop="id" label="用户ID" width="100"></el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="role" label="角色"></el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" size="small">编辑</el-button>
                <el-button type="danger" size="small">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="角色管理">
          <el-button type="primary" @click="handleAddRole">新增角色</el-button>
          <el-table :data="roleList" style="width: 100%">
            <el-table-column prop="id" label="角色ID" width="100"></el-table-column>
            <el-table-column prop="name" label="角色名称"></el-table-column>
            <el-table-column prop="description" label="角色描述"></el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" size="small">编辑</el-button>
                <el-button type="danger" size="small">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="菜单管理">
          <el-button type="primary" @click="handleAddMenu">新增菜单</el-button>
          <el-table :data="menuList" style="width: 100%">
            <el-table-column prop="id" label="菜单ID" width="100"></el-table-column>
            <el-table-column prop="name" label="菜单名称"></el-table-column>
            <el-table-column prop="path" label="菜单路径"></el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" size="small">编辑</el-button>
                <el-button type="danger" size="small">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref } from 'vue'

const userList = ref([
  { id: 1, username: 'admin', role: '管理员' },
  { id: 2, username: 'user', role: '普通用户' }
])

const roleList = ref([
  { id: 1, name: '管理员', description: '系统管理员' },
  { id: 2, name: '普通用户', description: '普通用户' }
])

const menuList = ref([
  { id: 1, name: '订单管理', path: '/order' },
  { id: 2, name: '线路管理', path: '/route' },
  { id: 3, name: '司机管理', path: '/driver' },
  { id: 4, name: '车辆管理', path: '/vehicle' },
  { id: 5, name: '网点管理', path: '/network-point' },
  { id: 6, name: '财务结算', path: '/settlement' },
  { id: 7, name: '系统管理', path: '/system' }
])

const logList = ref([
  { id: 1, username: 'admin', operation: '登录系统', createTime: '2026-03-07 21:00:00' },
  { id: 2, username: 'admin', operation: '新增订单', createTime: '2026-03-07 21:10:00' }
])

const handleAddUser = () => {
  console.log('新增用户')
}

const handleAddRole = () => {
  console.log('新增角色')
}

const handleAddMenu = () => {
  console.log('新增菜单')
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
