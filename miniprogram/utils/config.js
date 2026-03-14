module.exports = {
  dev: {
    baseUrl: 'http://localhost:8081/api',
    ipUrl: 'http://192.168.31.230:8081/api'
  },
  prod: {
    baseUrl: 'https://your-production-domain.com/api'
  },
  getApiUrl: function() {
    try {
      const systemInfo = wx.getSystemInfoSync()
      const platform = systemInfo.platform
      if (platform === 'devtools') {
        return this.dev.baseUrl
      }
      return this.dev.ipUrl || this.dev.baseUrl
    } catch (e) {
      return this.dev.baseUrl
    }
  },
  getDevUrl: function() {
    return 'http://localhost:8081/api'
  }
}
