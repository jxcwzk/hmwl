const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    activeTab: 'all',
    orderList: [],
    loading: false,
    currentPage: 1,
    pageSize: 10,
    total: 0,
    hasMore: true,
    isLoggedIn: false,
    userInfo: null
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
    if (this.data.isLoggedIn) {
      this.loadOrderList(true)
    }
  },

  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.status === 1 && userInfo.userType > 0) {
      this.setData({
        isLoggedIn: true,
        userInfo: userInfo
      })
    } else {
      this.setData({
        isLoggedIn: false,
        userInfo: null,
        orderList: []
      })
    }
  },

  loadOrderList(refresh = false) {
    if (!this.data.isLoggedIn) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    if (refresh) {
      this.setData({
        currentPage: 1,
        orderList: [],
        hasMore: true
      })
    }

    if (!this.data.hasMore) {
      return
    }

    this.setData({ loading: true })

    api.getOrderList({
      current: this.data.currentPage,
      size: this.data.pageSize
    }).then(data => {
      const list = data.records || data || []
      this.setData({
        orderList: refresh ? list : [...this.data.orderList, ...list],
        total: data.total || 0,
        currentPage: this.data.currentPage + 1,
        hasMore: this.data.orderList.length < (data.total || 0),
        loading: false
      })
    }).catch(err => {
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    })
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab
    })
    this.loadOrderList(true)
  },

  viewOrderDetail(e) {
    const orderNo = e.currentTarget.dataset.orderno
    wx.navigateTo({
      url: `/pages/logistics/logistics?number=${orderNo}`
    })
  },

  cancelOrder(e) {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '订单已取消',
            icon: 'success'
          })
          this.loadOrderList(true)
        }
      }
    })
  },

  payOrder(e) {
    const order = e.currentTarget.dataset.order
    
    if (order.status === 0) {
      wx.showModal({
        title: '支付订单',
        content: `确定支付¥${order.totalFee}吗？`,
        success: (res) => {
          if (res.confirm) {
            wx.showToast({
              title: '支付成功',
              icon: 'success'
            })
            this.loadOrderList(true)
          }
        }
      })
    } else if (order.status === 1) {
      wx.navigateTo({
        url: `/pages/logistics/logistics?number=${order.orderNo}`
      })
    }
  },

  goToLogin() {
    wx.switchTab({
      url: '/pages/user/user'
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadOrderList()
    }
  },

  onPullDownRefresh() {
    this.loadOrderList(true).finally(() => {
      wx.stopPullDownRefresh()
    })
  }
})
