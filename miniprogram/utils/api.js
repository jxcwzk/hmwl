const app = getApp()
const config = require('./config.js')

const BASE_URL = config.dev.baseUrl

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
  return request({
    url: '/order/list',
    method: 'GET',
    data: params
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
  BASE_URL
}
