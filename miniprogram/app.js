App({
  globalData: {
    userInfo: null,
    isLoggedIn: false
  },

  onLaunch() {
    if (!wx.cloud) {
      console.error('请使用 2.2.3 或以上的基础库以使用云能力')
    } else {
      try {
        wx.cloud.init({
          env: 'default',
          traceUser: true,
        })
        console.log('云开发初始化成功')
      } catch (error) {
        console.error('云开发初始化失败:', error)
      }
    }

    this.checkLoginStatus()
  },

  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    const token = wx.getStorageSync('token')

    if (userInfo && token) {
      this.globalData.userInfo = userInfo
      this.globalData.isLoggedIn = true
    }
  },

  login() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: res => {
          console.log('wx.login success:', res)
          if (res.code) {
            const api = require('./utils/api.js')
            api.login(res.code).then(data => {
              console.log('登录成功:', data)
              this.globalData.userInfo = data
              this.globalData.isLoggedIn = true

              wx.setStorageSync('userInfo', data)
              wx.setStorageSync('token', 'wx_' + data.id)

              resolve(data)
            }).catch(err => {
              console.error('登录失败:', err)
              reject(err)
            })
          } else {
            console.error('wx.login 没有返回 code')
            reject(new Error('获取wx.login失败'))
          }
        },
        fail: err => {
          console.error('wx.login fail:', err)
          reject(err)
        }
      })
    })
  },

  getUserInfo() {
    return this.globalData.userInfo
  },

  isLoggedIn() {
    return this.globalData.isLoggedIn
  },

  logout() {
    this.globalData.userInfo = null
    this.globalData.isLoggedIn = false
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('token')
  },

  onError(error) {
    console.error('全局错误:', error)
  },

  onPageNotFound(res) {
    console.warn('页面不存在:', res)
    wx.redirectTo({
      url: '/pages/index/index'
    })
  }
})
