const api = require('../../utils/api.js')

Page({
  data: {
    currentTab: 0,
    orderList: [],
    refreshing: false,
    rejectModalShow: false,
    rejectOrderId: null,
    rejectReason: ''
  },

  onLoad() {
    this.loadOrders()
  },

  onShow() {
    this.loadOrders()
  },

  onPullDownRefresh() {
    this.loadOrders()
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ currentTab: index })
    this.loadOrders()
  },

  async loadOrders() {
    try {
      const userInfo = wx.getStorageSync('userInfo')
      if (!userInfo || userInfo.userType !== 3) {
        wx.showToast({ title: '请使用司机账号登录', icon: 'none' })
        return
      }
      
      const driverId = userInfo.id
      let allOrders = await api.getDriverOrderList(driverId)
      
      let filteredOrders = []
      const statusMap = {
        0: { text: '待接单', class: 'pending' },
        1: { text: '已接单', class: 'accepted' },
        2: { text: '已取货', class: 'picked' },
        3: { text: '配送中', class: 'delivering' },
        4: { text: '已送达', class: 'delivered' },
        5: { text: '已完成', class: 'completed' }
      }

      if (this.data.currentTab === 0) {
        filteredOrders = allOrders.filter(o => o.status === 0 || o.status === 1)
      } else if (this.data.currentTab === 1) {
        filteredOrders = allOrders.filter(o => o.status >= 2 && o.status <= 4)
      } else {
        filteredOrders = allOrders.filter(o => o.status === 5)
      }

      const formattedOrders = filteredOrders.map(order => ({
        ...order,
        statusText: statusMap[order.status]?.text || '未知',
        statusClass: statusMap[order.status]?.class || '',
        createTime: this.formatTime(order.createTime)
      }))

      this.setData({ 
        orderList: formattedOrders,
        refreshing: false 
      })
    } catch (err) {
      console.error('加载订单失败:', err)
      this.setData({ refreshing: false })
    }
  },

  onRefresh() {
    this.setData({ refreshing: true })
    this.loadOrders()
  },

  goToDetail(e) {
    const orderId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/driver-order-detail/driver-order-detail?id=${orderId}`
    })
  },

  preventTap() {},

  showRejectModal(e) {
    const orderId = e.currentTarget.dataset.id
    this.setData({
      rejectModalShow: true,
      rejectOrderId: orderId,
      rejectReason: ''
    })
  },

  closeRejectModal() {
    this.setData({
      rejectModalShow: false,
      rejectOrderId: null,
      rejectReason: ''
    })
  },

  onRejectReasonInput(e) {
    this.setData({ rejectReason: e.detail.value })
  },

  async confirmReject() {
    const { rejectOrderId, rejectReason } = this.data
    if (!rejectReason.trim()) {
      wx.showToast({ title: '请输入拒单原因', icon: 'none' })
      return
    }

    try {
      const userInfo = wx.getStorageSync('userInfo')
      await api.driverRejectOrder(rejectOrderId, userInfo.id, rejectReason)
      wx.showToast({ title: '拒单成功', icon: 'success' })
      this.closeRejectModal()
      this.loadOrders()
    } catch (err) {
      wx.showToast({ title: err.message || '操作失败', icon: 'none' })
    }
  },

  async acceptOrder(e) {
    const orderId = e.currentTarget.dataset.id
    
    wx.showModal({
      title: '确认接单',
      content: '确定要接下这个订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const userInfo = wx.getStorageSync('userInfo')
            await api.driverAcceptOrder(orderId, userInfo.id)
            wx.showToast({ title: '接单成功', icon: 'success' })
            this.loadOrders()
          } catch (err) {
            wx.showToast({ title: err.message || '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  formatTime(timeStr) {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
  }
})
