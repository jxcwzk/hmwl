Page({
  data: {
    expressNumber: '',
    hasResult: false,
    expressInfo: {
      number: '',
      status: '',
      latestStatus: '',
      latestTime: '',
      history: []
    },
    historyList: []
  },

  onLoad() {
    // 加载历史查询记录
    this.loadHistory();
  },

  bindExpressNumberInput(e) {
    this.setData({
      expressNumber: e.detail.value
    });
  },

  scanCode() {
    wx.scanCode({
      success: (res) => {
        this.setData({
          expressNumber: res.result
        });
        this.searchExpress();
      },
      fail: (err) => {
        wx.showToast({
          title: '扫码失败',
          icon: 'none'
        });
      }
    });
  },

  searchExpress() {
    if (!this.data.expressNumber) {
      wx.showToast({
        title: '请输入快递单号',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '查询中...'
    });

    // 模拟查询结果
    setTimeout(() => {
      wx.hideLoading();
      
      const expressInfo = {
        number: this.data.expressNumber,
        status: '运输中',
        latestStatus: '【深圳市】快递已到达深圳转运中心',
        latestTime: '2026-03-07 10:30:00',
        history: [
          {
            text: '【广州市】快递已离开广州转运中心',
            time: '2026-03-07 08:15:00'
          },
          {
            text: '【广州市】快递已到达广州转运中心',
            time: '2026-03-06 22:45:00'
          },
          {
            text: '【广州市】快递已揽收',
            time: '2026-03-06 18:30:00'
          }
        ]
      };

      this.setData({
        hasResult: true,
        expressInfo: expressInfo
      });

      // 保存到历史记录
      this.saveHistory(this.data.expressNumber);
    }, 1000);
  },

  saveHistory(number) {
    let history = wx.getStorageSync('expressHistory') || [];
    
    // 移除重复记录
    history = history.filter(item => item.number !== number);
    
    // 添加新记录到开头
    history.unshift({
      number: number,
      time: new Date().toLocaleString()
    });
    
    // 限制历史记录数量
    if (history.length > 10) {
      history = history.slice(0, 10);
    }
    
    wx.setStorageSync('expressHistory', history);
    this.loadHistory();
  },

  loadHistory() {
    const history = wx.getStorageSync('expressHistory') || [];
    this.setData({
      historyList: history
    });
  },

  selectHistory(e) {
    const number = e.currentTarget.dataset.number;
    this.setData({
      expressNumber: number
    });
    this.searchExpress();
  }
});