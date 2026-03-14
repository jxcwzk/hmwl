const api = require('../../utils/api.js')

Page({
  data: {
    orderId: null,
    order: {},
    remark: '',
    uploading: false
  },

  onLoad(options) {
    const orderId = options.id
    if (orderId) {
      this.setData({ orderId: parseInt(orderId) })
      this.loadOrderDetail()
    }
  },

  async loadOrderDetail() {
    try {
      wx.showLoading({ title: '加载中...' })
      const order = await api.getOrderDetail(this.data.orderId)
      
      const statusMap = {
        0: { text: '待接单', class: 'pending' },
        1: { text: '已接单', class: 'accepted' },
        2: { text: '已取货', class: 'picked' },
        3: { text: '配送中', class: 'delivering' },
        4: { text: '已送达', class: 'delivered' },
        5: { text: '已完成', class: 'completed' }
      }
      
      const formattedOrder = {
        ...order,
        statusText: statusMap[order.status]?.text || '未知',
        statusClass: statusMap[order.status]?.class || '',
        createTime: this.formatTime(order.createTime)
      }
      
      this.setData({ order: formattedOrder })
      wx.hideLoading()
    } catch (err) {
      console.error('加载订单详情失败:', err)
      wx.hideLoading()
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  async submitRemark() {
    const { orderId, remark, order } = this.data
    if (!remark.trim()) {
      wx.showToast({ title: '请输入备注', icon: 'none' })
      return
    }

    try {
      await api.driverUpdateStatus(orderId, order.status, remark)
      wx.showToast({ title: '提交成功', icon: 'success' })
      this.setData({ remark: '' })
      this.loadOrderDetail()
    } catch (err) {
      wx.showToast({ title: err.message || '提交失败', icon: 'none' })
    }
  },

  async updateStatus(e) {
    const newStatus = parseInt(e.currentTarget.dataset.status)
    const { orderId, order } = this.data
    
    const statusNames = {
      2: '已取货',
      3: '配送中',
      4: '已送达'
    }

    wx.showModal({
      title: '确认状态',
      content: `确认将订单状态更新为"${statusNames[newStatus]}"吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.driverUpdateStatus(orderId, newStatus, '')
            wx.showToast({ title: '更新成功', icon: 'success' })
            this.loadOrderDetail()
          } catch (err) {
            wx.showToast({ title: err.message || '更新失败', icon: 'none' })
          }
        }
      }
    })
  },

  uploadReceipt() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const filePath = res.tempFilePaths[0]
        this.setData({ uploading: true })
        
        try {
          wx.showLoading({ title: '上传中...' })
          await api.uploadReceiptImage(this.data.orderId, filePath)
          wx.showToast({ title: '上传成功', icon: 'success' })
          this.loadOrderDetail()
        } catch (err) {
          console.error('上传回单失败:', err)
          wx.showToast({ title: err.message || '上传失败', icon: 'none' })
        } finally {
          this.setData({ uploading: false })
          wx.hideLoading()
        }
      }
    })
  },

  previewReceipt() {
    const { order } = this.data
    if (order.receiptImage) {
      wx.previewImage({
        urls: [order.receiptImage]
      })
    }
  },

  formatTime(timeStr) {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  }
})
