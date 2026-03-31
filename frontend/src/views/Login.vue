<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="48" color="#007AFF"><Van /></el-icon>
        </div>
        <h1 class="title">红美物流管理系统</h1>
        <p class="subtitle">HongMei Logistics Management</p>
      </div>
      
      <el-form 
        ref="loginFormRef"
        :model="loginForm" 
        :rules="loginRules" 
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <div class="remember-forgot">
          <el-checkbox v-model="rememberMe" class="remember-me">记住密码</el-checkbox>
        </div>
        
        <el-button 
          type="primary" 
          size="large" 
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
        
        <div v-if="errorMsg" class="error-tip">
          <el-icon><WarningFilled /></el-icon>
          {{ errorMsg }}
        </div>
      </el-form>
      
      <div class="login-footer">
        <p class="copyright">© 2024 红美物流 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, WarningFilled, Van } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)
const errorMsg = ref('')

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    errorMsg.value = ''
    
    try {
      const res = await request.post('/user/login', {
        username: loginForm.username,
        password: loginForm.password
      })
      
      if (res && res.token) {
        localStorage.setItem('token', res.token)
        localStorage.setItem('userInfo', JSON.stringify(res))
        localStorage.setItem('userType', res.userType)
        localStorage.setItem('userId', res.id)
        localStorage.setItem('businessUserId', res.businessUserId || '')
        localStorage.setItem('driverId', res.driverId || '')

        ElMessage.success('登录成功')

        router.push('/home')
      } else {
        errorMsg.value = '用户名或密码错误'
      }
    } catch (error) {
      console.error('登录错误:', error)
      errorMsg.value = '网络错误，请稍后重试'
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background: linear-gradient(135deg, #007AFF 0%, #0055BB 50%, #003366 100%);
}

.login-bg::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 50%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.login-card {
  position: relative;
  width: 420px;
  background: var(--color-surface);
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 48px 40px;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.1) 0%, rgba(0, 122, 255, 0.2) 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 13px;
  color: var(--color-text-tertiary);
  margin: 0;
  letter-spacing: 1px;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 8px 16px;
  border-radius: 12px;
  box-shadow: none !important;
  border: 1px solid var(--color-border);
  background: var(--color-surface-secondary);
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--color-primary);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.15) !important;
}

.login-form :deep(.el-input__inner) {
  height: 44px;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-me {
  --el-checkbox-text-color: var(--color-text-secondary);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #007AFF 0%, #0055BB 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 122, 255, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.error-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
  padding: 10px;
  background: rgba(255, 59, 48, 0.1);
  border-radius: 8px;
  color: var(--color-danger);
  font-size: 13px;
}

.login-footer {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid var(--color-separator);
}

.copyright {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-tertiary);
}

@media (max-width: 480px) {
  .login-card {
    width: 90%;
    padding: 32px 24px;
    border-radius: 20px;
  }
  
  .title {
    font-size: 20px;
  }
  
  .logo {
    width: 64px;
    height: 64px;
  }
}
</style>