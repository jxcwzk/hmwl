Page({
  data: {
    userInfo: {
      name: '用户名',
      level: '普通会员'
    }
  },

  navigateToCoupons() {
    wx.showToast({
      title: '优惠券功能开发中',
      icon: 'none'
    });
  },

  navigateToPoints() {
    wx.showToast({
      title: '积分功能开发中',
      icon: 'none'
    });
  },

  navigateToAddress() {
    wx.showToast({
      title: '地址管理功能开发中',
      icon: 'none'
    });
  },

  navigateToCustomerService() {
    wx.showToast({
      title: '客服中心功能开发中',
      icon: 'none'
    });
  },

  navigateToOrder() {
    wx.navigateTo({
      url: '/pages/order/order'
    });
  },

  navigateToAccountSetting() {
    wx.showToast({
      title: '账号设置功能开发中',
      icon: 'none'
    });
  },

  navigateToPrivacySetting() {
    wx.showToast({
      title: '隐私设置功能开发中',
      icon: 'none'
    });
  },

  navigateToAbout() {
    wx.showToast({
      title: '关于我们功能开发中',
      icon: 'none'
    });
  },

  logout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
        }
      }
    });
  }
});