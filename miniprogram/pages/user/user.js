const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    userTypeText: '未分配身份',
    statusText: '未激活',
    canUseApp: false
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
  },

  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({
        userInfo: userInfo,
        isLoggedIn: true,
        userTypeText: this.getUserTypeText(userInfo.userType),
        statusText: userInfo.status === 1 ? '已激活' : '未激活',
        canUseApp: userInfo.status === 1 && userInfo.userType > 0
      })
    } else {
      this.setData({
        userInfo: null,
        isLoggedIn: false,
        userTypeText: '未分配身份',
        statusText: '未激活',
        canUseApp: false
      })
    }
  },

  getUserTypeText(userType) {
    const types = {
      0: '未分配身份',
      1: '管理员',
      2: '客户',
      3: '司机'
    }
    return types[userType] || '未分配身份'
  },

  handleLogin() {
    wx.showLoading({ title: '登录中...' })

    app.login().then(data => {
      wx.hideLoading()
      this.setData({
        userInfo: data,
        isLoggedIn: true,
        userTypeText: this.getUserTypeText(data.userType),
        statusText: data.status === 1 ? '已激活' : '未激活',
        canUseApp: data.status === 1 && data.userType > 0
      })

      if (data.status !== 1 || !data.userType) {
        wx.showModal({
          title: '提示',
          content: '登录成功！请等待管理员为您分配身份后使用完整功能。',
          showCancel: false
        })
      }
    }).catch(err => {
      wx.hideLoading()
      wx.showToast({
        title: '登录失败: ' + (err.message || '请重试'),
        icon: 'none'
      })
    })
  },

  handleLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
          this.setData({
            userInfo: null,
            isLoggedIn: false,
            userTypeText: '未分配身份',
            statusText: '未激活',
            canUseApp: false
          })
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  },

  navigateToCoupons() {
    if (!this.data.canUseApp) {
      wx.showToast({
        title: '请等待管理员分配身份',
        icon: 'none'
      })
      return
    }
    wx.showToast({
      title: '优惠券功能开发中',
      icon: 'none'
    })
  },

  navigateToPoints() {
    if (!this.data.canUseApp) {
      wx.showToast({
        title: '请等待管理员分配身份',
        icon: 'none'
      })
      return
    }
    wx.showToast({
      title: '积分功能开发中',
      icon: 'none'
    })
  },

  navigateToAddress() {
    if (!this.data.canUseApp) {
      wx.showToast({
        title: '请等待管理员分配身份',
        icon: 'none'
      })
      return
    }
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo || !userInfo.businessUserId) {
      wx.showToast({
        title: '您还不是客户身份',
        icon: 'none'
      })
      return
    }
    wx.navigateTo({
      url: '/pages/address/address'
    })
  },

  navigateToCustomerService() {
    wx.showToast({
      title: '客服中心功能开发中',
      icon: 'none'
    })
  },

  navigateToOrder() {
    if (!this.data.canUseApp) {
      wx.showToast({
        title: '请等待管理员分配身份',
        icon: 'none'
      })
      return
    }
    wx.navigateTo({
      url: '/pages/order/order'
    })
  },

  navigateToAccountSetting() {
    wx.showToast({
      title: '账号设置功能开发中',
      icon: 'none'
    })
  },

  navigateToPrivacySetting() {
    wx.showToast({
      title: '隐私设置功能开发中',
      icon: 'none'
    })
  },

  navigateToAbout() {
    wx.showToast({
      title: '关于我们功能开发中',
      icon: 'none'
    })
  },

  logout() {
    this.handleLogout()
  }
})
