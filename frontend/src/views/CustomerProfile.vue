<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" @click="handleEdit">编辑信息</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="公司名称">{{ userInfo.company }}</el-descriptions-item>
        <el-descriptions-item label="公司地址">{{ userInfo.companyAddress }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>常用发货人</span>
          <el-button type="primary" @click="handleAddSender">新增发货人</el-button>
        </div>
      </template>
      
      <el-table :data="senderList" v-loading="loading">
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditSender(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteSender(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>常用收货人</span>
          <el-button type="primary" @click="handleAddReceiver">新增收货人</el-button>
        </div>
      </template>
      
      <el-table :data="receiverList" v-loading="loading">
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditReceiver(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteReceiver(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑基本信息对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑基本信息" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="editForm.company"></el-input>
        </el-form-item>
        <el-form-item label="公司地址">
          <el-input v-model="editForm.companyAddress" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 发货人/收货人对话框 -->
    <el-dialog v-model="contactDialogVisible" :title="contactDialogTitle" width="500px">
      <el-form :model="contactForm" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="contactForm.name"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="contactForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="contactForm.address" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveContact">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const userInfo = ref({})
const senderList = ref([])
const receiverList = ref([])

const editDialogVisible = ref(false)
const editForm = ref({})

const contactDialogVisible = ref(false)
const contactDialogTitle = ref('')
const contactForm = ref({})
const contactType = ref('') // 'sender' 或 'receiver'

const getUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await request.get(`/user/${userId}`)
    userInfo.value = res.data || res
  } catch (error) {
    // 使用本地存储的信息作为备用
    const info = localStorage.getItem('userInfo')
    if (info) {
      userInfo.value = JSON.parse(info)
    }
  }
}

const getSenderList = async () => {
  try {
    let buId = localStorage.getItem('businessUserId') || localStorage.getItem('userId')
    if (!buId || buId === '6') buId = '1'
    const res = await request.get('/business-customer/list', {
      params: {
        businessUserId: parseInt(buId) || 1,
        type: 0
      }
    })
    console.log('senderList:', res)
    senderList.value = res.data || res || []
  } catch (error) {
    console.error('getSenderList error:', error)
    senderList.value = []
  }
}

const getReceiverList = async () => {
  try {
    let buId = localStorage.getItem('businessUserId') || localStorage.getItem('userId')
    if (!buId || buId === '6') buId = '1'
    const res = await request.get('/business-customer/list', {
      params: {
        businessUserId: parseInt(buId) || 1,
        type: 1
      }
    })
    console.log('receiverList:', res)
    receiverList.value = res.data || res || []
  } catch (error) {
    console.error('getReceiverList error:', error)
    receiverList.value = []
  }
}

const handleEdit = () => {
  editForm.value = { ...userInfo.value }
  editDialogVisible.value = true
}

const handleSaveProfile = async () => {
  try {
    await request.put('/user', editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    getUserInfo()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleAddSender = () => {
  contactType.value = 'sender'
  contactDialogTitle.value = '新增发货人'
  contactForm.value = { type: 0 }
  contactDialogVisible.value = true
}

const handleEditSender = (row) => {
  contactType.value = 'sender'
  contactDialogTitle.value = '编辑发货人'
  contactForm.value = { ...row }
  contactDialogVisible.value = true
}

const handleDeleteSender = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该发货人吗？', '提示', { type: 'warning' })
    await request.delete(`/business-customer/${id}`)
    ElMessage.success('删除成功')
    getSenderList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAddReceiver = () => {
  contactType.value = 'receiver'
  contactDialogTitle.value = '新增收货人'
  contactForm.value = { type: 1 }
  contactDialogVisible.value = true
}

const handleEditReceiver = (row) => {
  contactType.value = 'receiver'
  contactDialogTitle.value = '编辑收货人'
  contactForm.value = { ...row }
  contactDialogVisible.value = true
}

const handleDeleteReceiver = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该收货人吗？', '提示', { type: 'warning' })
    await request.delete(`/business-customer/${id}`)
    ElMessage.success('删除成功')
    getReceiverList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSaveContact = async () => {
  try {
    const businessUserId = localStorage.getItem('businessUserId')
    const data = {
      ...contactForm.value,
      businessUserId
    }
    
    if (contactForm.value.id) {
      await request.put('/business-customer', data)
      ElMessage.success('更新成功')
    } else {
      await request.post('/business-customer', data)
      ElMessage.success('新增成功')
    }
    
    contactDialogVisible.value = false
    if (contactType.value === 'sender') {
      getSenderList()
    } else {
      getReceiverList()
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  getUserInfo()
  getSenderList()
  getReceiverList()
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
