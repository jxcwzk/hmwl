Page({
  data: {
    expressInfo: {
      number: 'SF1234567890',
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
    }
  },

  onLoad(options) {
    // 如果有传递单号参数，更新物流信息
    if (options.number) {
      this.setData({
        'expressInfo.number': options.number
      });
    }
  },

  buyAgain() {
    wx.navigateTo({
      url: '/pages/send/send'
    });
  },

  complain() {
    wx.showToast({
      title: '投诉建议功能开发中',
      icon: 'none'
    });
  }
});