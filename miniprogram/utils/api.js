const app = getApp()
const config = require('./config.js')

const getBaseUrl = () => {
  const systemInfo = wx.getSystemInfoSync()
  const platform = systemInfo.platform
  
  if (platform === 'devtools') {
    return config.dev.baseUrl
  }
  return config.dev.ipUrl || config.dev.baseUrl
}

const BASE_URL = getBaseUrl()

console.log('当前API地址:', BASE_URL)

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')

    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else if (res.data.code === 401) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.showToast({
            title: '请先登录',
            icon: 'none'
          })
          reject(new Error(res.data.message || '未登录'))
        } else {
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          })
          reject(new Error(res.data.message || '请求失败'))
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

const login = (code) => {
  return request({
    url: '/user/wxLogin',
    method: 'POST',
    data: { code }
  })
}

const getUserInfo = (userId) => {
  return request({
    url: '/user/info',
    method: 'GET',
    data: { userId }
  })
}

const getOrderList = (params) => {
  const userInfo = wx.getStorageSync('userInfo')
  const data = {
    userId: userInfo?.id,
    userType: userInfo?.userType,
    businessUserId: userInfo?.businessUserId,
    ...params
  }
  return request({
    url: '/order/list',
    method: 'GET',
    data: data
  })
}

const getOrderDetail = (id) => {
  return request({
    url: `/order/${id}`,
    method: 'GET'
  })
}

const addOrder = (data) => {
  return request({
    url: '/order/add',
    method: 'POST',
    data
  })
}

const getDriverList = (params) => {
  return request({
    url: '/driver/list',
    method: 'GET',
    data: params
  })
}

const getNetworkList = (params) => {
  return request({
    url: '/network/list',
    method: 'GET',
    data: params
  })
}

const getVehicleList = (params) => {
  return request({
    url: '/vehicle/list',
    method: 'GET',
    data: params
  })
}

const getMyInfo = (userId) => {
  return request({
    url: '/miniprogram/myInfo',
    method: 'GET',
    data: { userId }
  })
}

const getSenders = (businessUserId) => {
  return request({
    url: '/miniprogram/senders',
    method: 'GET',
    data: { businessUserId }
  })
}

const getRecipients = (businessUserId) => {
  return request({
    url: '/miniprogram/recipients',
    method: 'GET',
    data: { businessUserId }
  })
}

const addSender = (data) => {
  return request({
    url: '/miniprogram/sender',
    method: 'POST',
    data
  })
}

const addRecipient = (data) => {
  return request({
    url: '/miniprogram/recipient',
    method: 'POST',
    data
  })
}

const updateContact = (data) => {
  return request({
    url: '/miniprogram/contact',
    method: 'PUT',
    data
  })
}

const deleteContact = (id) => {
  return request({
    url: `/miniprogram/contact/${id}`,
    method: 'DELETE'
  })
}

const getDriverOrderList = (driverId) => {
  return request({
    url: '/order/driver-list',
    method: 'GET',
    data: { driverId }
  })
}

const driverAcceptOrder = (orderId, driverId) => {
  return request({
    url: '/order/driver/accept',
    method: 'POST',
    data: { orderId, driverId }
  })
}

const driverRejectOrder = (orderId, driverId, reason) => {
  return request({
    url: '/order/driver/reject',
    method: 'POST',
    data: { orderId, driverId, reason }
  })
}

const driverUpdateStatus = (orderId, status, remark) => {
  return request({
    url: '/order/driver/update-status',
    method: 'POST',
    data: { orderId, status, remark }
  })
}

const uploadReceiptImage = (orderId, filePath) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.uploadFile({
      url: BASE_URL + '/order/receipt-image/upload',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      formData: {
        orderId: orderId
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data)
        } else {
          reject(new Error(data.message || '上传失败'))
        }
      },
      fail: reject
    })
  })
}

module.exports = {
  request,
  login,
  getUserInfo,
  getOrderList,
  getOrderDetail,
  addOrder,
  getDriverList,
  getNetworkList,
  getVehicleList,
  getMyInfo,
  getSenders,
  getRecipients,
  addSender,
  addRecipient,
  updateContact,
  deleteContact,
  getDriverOrderList,
  driverAcceptOrder,
  driverRejectOrder,
  driverUpdateStatus,
  uploadReceiptImage,
  BASE_URL
}
