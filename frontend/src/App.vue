<template>
  <div id="app">
    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <h1>红美物流系统</h1>
          <div class="user-info">
            <el-dropdown @command="handleUserCommand">
              <span class="user-name">
                <el-icon><UserFilled /></el-icon>
                {{ currentUserName || '用户' }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
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
            <el-menu-item index="9" v-if="currentUserType === 1 || currentUserType === 4">
              <el-icon><i class="el-icon-s-management"></i></el-icon>
              <span>调度管理</span>
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
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const activeIndex = ref('1')
const loading = ref(false)

const currentUserName = ref('')
const currentUserType = ref(0)

onMounted(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    currentUserName.value = user.username || '用户'
    currentUserType.value = user.userType || 0
  }
})

const handleUserCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  }
}

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
    case '/dispatch':
      return '调度管理'
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
      case '9':
        router.push('/dispatch')
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
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', 'PingFang SC', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--color-text-primary);
  min-height: 100vh;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0 var(--spacing-lg);
  height: 100%;
}

.header-content h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: white;
  letter-spacing: -0.3px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  color: rgba(255, 255, 255, 0.95);
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.user-name:hover {
  background: rgba(255, 255, 255, 0.15);
  text-decoration: none;
}

.aside-container {
  background-color: var(--color-surface) !important;
  transition: all var(--transition-normal);
  border-right: 1px solid var(--color-separator);
}

.el-menu {
  height: 100%;
  border-right: none !important;
  background: transparent !important;
}

.el-main {
  padding: var(--spacing-lg);
  background-color: var(--color-background);
}

.breadcrumb {
  margin-bottom: var(--spacing-md);
  background-color: var(--color-surface);
  padding: 12px 16px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-xs);
}

.el-breadcrumb__inner {
  font-size: 14px !important;
}

.el-header {
  background: linear-gradient(135deg, #007AFF 0%, #0055BB 100%) !important;
  box-shadow: var(--shadow-md);
}

.el-menu-item {
  border-radius: var(--radius-md) !important;
  margin: 4px 12px !important;
  padding: 0 16px !important;
  height: 44px !important;
  line-height: 44px !important;
  transition: all var(--transition-fast) !important;
}

.el-menu-item.is-active {
  background-color: rgba(0, 122, 255, 0.12) !important;
  color: var(--color-primary) !important;
}

.el-menu-item.is-active .el-icon {
  color: var(--color-primary) !important;
}

.el-menu-item:hover {
  background-color: rgba(0, 0, 0, 0.04) !important;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media screen and (max-width: 768px) {
  .el-aside {
    width: 180px !important;
  }
  
  .header-content h1 {
    font-size: 16px;
  }
  
  .el-main {
    padding: var(--spacing-md);
  }
  
  .el-menu-item {
    margin: 2px 8px !important;
    padding: 0 12px !important;
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
    padding: 4px 8px;
  }
}
</style>
