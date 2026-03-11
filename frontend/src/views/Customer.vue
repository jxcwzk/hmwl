<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
        </div>
      </template>
      <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" @select="handleMenuSelect">
        <el-menu-item index="business-user">
          <span>业务用户管理</span>
        </el-menu-item>
        <el-menu-item index="business-customer">
          <span>业务客户管理</span>
        </el-menu-item>
      </el-menu>
      <div class="router-view">
        <router-view></router-view>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  return route.path.split('/').pop() || 'business-user'
})

const handleMenuSelect = (key) => {
  router.push(`/customer/${key}`)
}

// 客户管理页面现在作为容器页面，包含业务用户和业务客户管理
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-menu-vertical-demo {
  width: 200px;
  float: left;
  margin-right: 20px;
  margin-bottom: 20px;
}

.router-view {
  margin-left: 220px;
}

.search-form {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>