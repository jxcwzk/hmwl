const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    activeTab: 'sender',
    senders: [],
    recipients: [],
    businessUserId: null,
    showModal: false,
    editingId: null,
    form: {
      customerName: '',
      contact: '',
      address: ''
    }
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.businessUserId) {
      this.setData({
        businessUserId: userInfo.businessUserId
      })
      this.loadData()
    } else {
      wx.showToast({
        title: '您还未分配客户身份',
        icon: 'none'
      })
    }
  },

  loadData() {
    if (!this.data.businessUserId) return
    
    api.getSenders(this.data.businessUserId).then(data => {
      this.setData({
        senders: data || []
      })
    })
    
    api.getRecipients(this.data.businessUserId).then(data => {
      this.setData({
        recipients: data || []
      })
    })
  },

  switchTab(e) {
    this.setData({
      activeTab: e.currentTarget.dataset.tab
    })
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    const key = `form.${field}`
    this.setData({
      [key]: value
    })
  },

  addContact() {
    this.setData({
      showModal: true,
      editingId: null,
      form: {
        customerName: '',
        contact: '',
        address: ''
      }
    })
  },

  editContact(e) {
    const id = e.currentTarget.dataset.id
    const list = this.data.activeTab === 'sender' ? this.data.senders : this.data.recipients
    const item = list.find(i => i.id === id)
    if (item) {
      this.setData({
        showModal: true,
        editingId: id,
        form: {
          customerName: item.customerName,
          contact: item.contact,
          address: item.address
        }
      })
    }
  },

  deleteContact(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条信息吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteContact(id).then(data => {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })
            this.loadData()
          })
        }
      }
    })
  },

  closeModal() {
    this.setData({
      showModal: false
    })
  },

  submitForm() {
    const { form, editingId, businessUserId, activeTab } = this.data
    if (!form.customerName || !form.contact) {
      wx.showToast({
        title: '请填写完整信息',
        icon: 'none'
      })
      return
    }

    const data = {
      ...form,
      businessUserId: businessUserId
    }

    if (editingId) {
      data.id = editingId
      api.updateContact(data).then(() => {
        wx.showToast({
          title: '更新成功',
          icon: 'success'
        })
        this.closeModal()
        this.loadData()
      })
    } else {
      const apiCall = activeTab === 'sender' ? api.addSender : api.addRecipient
      apiCall(data).then(() => {
        wx.showToast({
          title: '添加成功',
          icon: 'success'
        })
        this.closeModal()
        this.loadData()
      })
    }
  }
})
