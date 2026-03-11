// app.js
App({
  onLaunch() {
    // 初始化云开发
    if (!wx.cloud) {
      console.error('请使用 2.2.3 或以上的基础库以使用云能力')
    } else {
      try {
        wx.cloud.init({
          // env 参数说明：
          //   env 参数决定接下来小程序发起的云开发调用（wx.cloud.xxx）会默认请求到哪个云环境的资源
          //   此处请填入环境 ID, 环境 ID 可在云控制台获取
          //   如不填则使用默认环境（第一个创建的环境）
          env: 'default', // 使用默认环境
          traceUser: true,
        })
        console.log('云开发初始化成功')
      } catch (error) {
        console.error('云开发初始化失败:', error)
      }
    }

    this.globalData = {}
  },
  
  // 全局错误处理
  onError(error) {
    console.error('全局错误:', error)
    // 可以在这里添加错误上报逻辑
  },
  
  // 全局页面不存在处理
  onPageNotFound(res) {
    console.warn('页面不存在:', res)
    wx.redirectTo({
      url: '/pages/index/index'
    })
  }
})
