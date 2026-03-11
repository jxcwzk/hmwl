<template>
  <div id="app">
    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <h1>红美物流系统</h1>
          <div class="user-info">
            <el-dropdown>
              <span class="user-name">管理员</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" class="aside-container">
          <el-menu
            :default-active="activeIndex"
            class="el-menu-vertical-demo"
            @select="handleSelect"
            background-color="#f5f7fa"
            text-color="#303133"
            active-text-color="#409EFF"
          >
            <el-menu-item index="0">
              <el-icon><i class="el-icon-s-home"></i></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="1">
              <el-icon><i class="el-icon-s-order"></i></el-icon>
              <span>订单管理</span>
            </el-menu-item>
            <el-menu-item index="2">
              <el-icon><i class="el-icon-s-grid"></i></el-icon>
              <span>线路管理</span>
            </el-menu-item>
            <el-menu-item index="3">
              <el-icon><i class="el-icon-user"></i></el-icon>
              <span>司机管理</span>
            </el-menu-item>
            <el-menu-item index="4">
              <el-icon><i class="el-icon-truck"></i></el-icon>
              <span>车辆管理</span>
            </el-menu-item>
            <el-menu-item index="5">
              <el-icon><i class="el-icon-location"></i></el-icon>
              <span>网点管理</span>
            </el-menu-item>
            <el-menu-item index="6">
              <el-icon><i class="el-icon-s-finance"></i></el-icon>
              <span>财务结算</span>
            </el-menu-item>
            <el-menu-item index="7">
              <el-icon><i class="el-icon-setting"></i></el-icon>
              <span>系统管理</span>
            </el-menu-item>
            <el-menu-item index="8">
              <el-icon><i class="el-icon-user"></i></el-icon>
              <span>客户管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
          </el-breadcrumb>
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
    <el-loading v-if="loading" fullscreen text="加载中..." />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeIndex = ref('1')
const loading = ref(false)

// 监听路由变化，更新活动菜单和页面标题
watch(() => route.path, (newPath) => {
  updateActiveIndex(newPath)
})

// 根据当前路由更新活动菜单索引
const updateActiveIndex = (path) => {
  switch (path) {
    case '/home':
      activeIndex.value = '0'
      break
    case '/order':
      activeIndex.value = '1'
      break
    case '/route':
      activeIndex.value = '2'
      break
    case '/driver':
      activeIndex.value = '3'
      break
    case '/vehicle':
      activeIndex.value = '4'
      break
    case '/network-point':
      activeIndex.value = '5'
      break
    case '/settlement':
      activeIndex.value = '6'
      break
    case '/system':
      activeIndex.value = '7'
      break
    case '/customer':
    case '/customer/business-user':
    case '/customer/business-customer':
      activeIndex.value = '8'
      break
    default:
      activeIndex.value = '1'
  }
}

// 获取当前页面名称
const currentPageName = computed(() => {
  const path = route.path
  switch (path) {
    case '/home':
      return '首页'
    case '/order':
      return '订单管理'
    case '/route':
      return '线路管理'
    case '/driver':
      return '司机管理'
    case '/vehicle':
      return '车辆管理'
    case '/network-point':
      return '网点管理'
    case '/settlement':
      return '财务结算'
    case '/system':
      return '系统管理'
    case '/customer':
      return '客户管理'
    case '/customer/business-user':
      return '业务用户管理'
    case '/customer/business-customer':
      return '业务客户管理'
    default:
      return '首页'
  }
})

// 处理菜单选择
const handleSelect = (key) => {
  activeIndex.value = key
  loading.value = true
  
  setTimeout(() => {
    switch (key) {
      case '0':
        router.push('/home')
        break
      case '1':
        router.push('/order')
        break
      case '2':
        router.push('/route')
        break
      case '3':
        router.push('/driver')
        break
      case '4':
        router.push('/vehicle')
        break
      case '5':
        router.push('/network-point')
        break
      case '6':
        router.push('/settlement')
        break
      case '7':
        router.push('/system')
        break
      case '8':
        router.push('/customer')
        break
    }
    loading.value = false
  }, 300)
}

// 初始化时更新活动菜单
updateActiveIndex(route.path)
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-name:hover {
  text-decoration: underline;
}

.aside-container {
  background-color: #f5f7fa;
  transition: all 0.3s;
}

.el-menu {
  height: 100%;
  border-right: none;
}

.el-main {
  padding: 20px;
  background-color: #f0f2f5;
}

.breadcrumb {
  margin-bottom: 20px;
  background-color: white;
  padding: 10px 15px;
  border-radius: 4px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .el-aside {
    width: 180px !important;
  }
  
  .header-content h1 {
    font-size: 16px;
  }
  
  .el-main {
    padding: 10px;
  }
}

@media screen and (max-width: 480px) {
  .el-aside {
    width: 150px !important;
  }
  
  .header-content h1 {
    font-size: 14px;
  }
  
  .user-name {
    font-size: 12px;
  }
}
</style>
