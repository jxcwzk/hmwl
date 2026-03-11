Page({
  data: {
    logisticsList: [],
    activityList: [],
    loading: true,
    error: false
  },

  onLoad() {
    this.loadData();
  },

  async loadData() {
    try {
      this.setData({ loading: true, error: false });
      
      // 尝试从缓存获取数据
      const cachedData = wx.getStorageSync('homeData');
      if (cachedData && Date.now() - cachedData.timestamp < 5 * 60 * 1000) { // 5分钟缓存
        this.setData({
          logisticsList: cachedData.logisticsList,
          activityList: cachedData.activityList,
          loading: false
        });
        return;
      }
      
      // 模拟请求数据
      await this.fetchData();
      
      // 缓存数据
      wx.setStorageSync('homeData', {
        logisticsList: this.data.logisticsList,
        activityList: this.data.activityList,
        timestamp: Date.now()
      });
    } catch (error) {
      console.error('加载数据失败:', error);
      this.setData({ error: true, loading: false });
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    }
  },

  async fetchData() {
    // 模拟网络请求延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    this.setData({
      logisticsList: [
        {
          number: 'SF1234567890',
          status: '运输中',
          detail: '【深圳市】快递已到达深圳转运中心'
        },
        {
          number: 'SF0987654321',
          status: '已揽收',
          detail: '【广州市】快递已揽收'
        }
      ],
      activityList: [
        {
          title: '新用户专享',
          desc: '首次寄件立减5元',
          tag: '限时'
        },
        {
          title: '会员积分',
          desc: '寄件积分翻倍',
          tag: '热卖'
        }
      ],
      loading: false
    });
  },

  navigateToSend() {
    wx.navigateTo({
      url: '/pages/send/send'
    });
  },

  navigateToQuery() {
    wx.navigateTo({
      url: '/pages/query/query'
    });
  },

  navigateToOrder() {
    wx.navigateTo({
      url: '/pages/order/order'
    });
  },

  navigateToNetwork() {
    wx.navigateTo({
      url: '/pages/network-point/network-point'
    });
  },

  navigateToActivity() {
    wx.navigateTo({
      url: '/pages/activity/activity'
    });
  },

  scanCode() {
    wx.scanCode({
      success: (res) => {
        wx.navigateTo({
          url: `/pages/query/query?number=${res.result}`
        });
      },
      fail: (err) => {
        console.error('扫码失败:', err);
        wx.showToast({
          title: '扫码失败，请重试',
          icon: 'none'
        });
      }
    });
  },

  // 重试加载数据
  retryLoad() {
    this.loadData();
  }
});