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
    userInfo: null,
    driverList: [],
    networkList: []
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
    if (this.data.isLoggedIn) {
      this.loadOrderList(true)
      if (this.data.userInfo.userType === 1) {
        this.loadDriverList()
        this.loadNetworkList()
      }
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
      const updatedOrderList = refresh ? list : [...this.data.orderList, ...list]
      this.setData({
        orderList: updatedOrderList,
        total: data.total || 0,
        currentPage: this.data.currentPage + 1,
        hasMore: updatedOrderList.length < (data.total || 0),
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

  loadDriverList() {
    api.getDriverList({ page: 1, size: 100 }).then(data => {
      this.setData({ driverList: data.records || [] })
    })
  },

  loadNetworkList() {
    api.getNetworkList({ page: 1, size: 100 }).then(data => {
      this.setData({ networkList: data.records || [] })
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

  assignDriver(e) {
    const order = e.currentTarget.dataset.order
    const driverList = this.data.driverList
    
    const driverOptions = driverList.map(driver => ({
      id: driver.id,
      name: driver.name
    }))
    
    wx.showActionSheet({
      itemList: driverOptions.map(d => d.name),
      success: (res) => {
        const selectedDriver = driverOptions[res.tapIndex]
        
        api.request({
          url: '/order/assign-driver',
          method: 'POST',
          data: {
            orderId: order.id,
            driverId: selectedDriver.id
          }
        }).then(() => {
          wx.showToast({
            title: '司机指派成功',
            icon: 'success'
          })
          this.loadOrderList(true)
        })
      }
    })
  },

  assignNetwork(e) {
    const order = e.currentTarget.dataset.order
    const networkList = this.data.networkList
    
    const networkOptions = networkList.map(network => ({
      id: network.id,
      name: network.name
    }))
    
    wx.showActionSheet({
      itemList: networkOptions.map(n => n.name),
      success: (res) => {
        const selectedNetwork = networkOptions[res.tapIndex]
        
        api.request({
          url: '/order/assign-network',
          method: 'POST',
          data: {
            orderId: order.id,
            networkPointId: selectedNetwork.id
          }
        }).then(() => {
          wx.showToast({
            title: '网点指派成功',
            icon: 'success'
          })
          this.loadOrderList(true)
        })
      }
    })
  },

  updateLogistics(e) {
    const order = e.currentTarget.dataset.order
    
    wx.showModal({
      title: '更新物流进度',
      content: '请输入物流进度',
      editable: true,
      placeholderText: '例如：已发货，正在运输中',
      success: (res) => {
        if (res.confirm && res.content) {
          api.request({
            url: '/order/update-logistics',
            method: 'POST',
            data: {
              orderId: order.id,
              logisticsProgress: res.content
            }
          }).then(() => {
            wx.showToast({
              title: '物流进度更新成功',
              icon: 'success'
            })
            this.loadOrderList(true)
          })
        }
      }
    })
  },

  providePrice(e) {
    const order = e.currentTarget.dataset.order
    
    wx.showModal({
      title: '提供报价',
      content: '请输入基础费用',
      editable: true,
      placeholderText: '例如：100',
      success: (res) => {
        if (res.confirm && res.content) {
          const baseFee = parseFloat(res.content)
          if (!isNaN(baseFee)) {
            api.request({
              url: '/order/provide-price',
              method: 'POST',
              data: {
                orderId: order.id,
                baseFee: baseFee
              }
            }).then(() => {
              wx.showToast({
                title: '报价提供成功',
                icon: 'success'
              })
              this.loadOrderList(true)
            })
          } else {
            wx.showToast({
              title: '请输入有效的价格',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  updatePrice(e) {
    const order = e.currentTarget.dataset.order
    
    wx.showModal({
      title: '修改客户报价',
      content: '请输入客户报价',
      editable: true,
      placeholderText: `当前报价：${order.totalFee || 0}`,
      success: (res) => {
        if (res.confirm && res.content) {
          const totalFee = parseFloat(res.content)
          if (!isNaN(totalFee)) {
            api.request({
              url: '/order/update-price',
              method: 'POST',
              data: {
                orderId: order.id,
                totalFee: totalFee
              }
            }).then(() => {
              wx.showToast({
                title: '价格修改成功',
                icon: 'success'
              })
              this.loadOrderList(true)
            })
          } else {
            wx.showToast({
              title: '请输入有效的价格',
              icon: 'none'
            })
          }
        }
      }
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
