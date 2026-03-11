Page({
  data: {
    activeTab: 'available',
    coupons: [
      {
        value: '5',
        title: '新用户专享',
        desc: '首次寄件立减5元',
        expire: '2026-06-30'
      },
      {
        value: '10',
        title: '会员优惠',
        desc: '满50元立减10元',
        expire: '2026-05-31'
      }
    ],
    activities: [
      {
        title: '春季寄件优惠',
        desc: '寄件满20元立减3元',
        time: '2026-03-01 至 2026-03-31',
        tag: '进行中'
      },
      {
        title: '会员日活动',
        desc: '每月15日寄件8折',
        time: '2026-03-15',
        tag: '即将开始'
      }
    ],
    points: 1280,
    goodsList: [
      {
        icon: '🎁',
        name: '顺丰定制笔记本',
        points: 500
      },
      {
        icon: '☕',
        name: '星巴克咖啡券',
        points: 800
      },
      {
        icon: '🧳',
        name: '顺丰定制行李牌',
        points: 300
      }
    ]
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    
    // 模拟不同状态的优惠券
    if (tab === 'available') {
      this.setData({
        coupons: [
          {
            value: '5',
            title: '新用户专享',
            desc: '首次寄件立减5元',
            expire: '2026-06-30'
          },
          {
            value: '10',
            title: '会员优惠',
            desc: '满50元立减10元',
            expire: '2026-05-31'
          }
        ]
      });
    } else if (tab === 'used') {
      this.setData({
        coupons: [
          {
            value: '3',
            title: '周末优惠',
            desc: '周末寄件立减3元',
            expire: '2026-02-28'
          }
        ]
      });
    } else {
      this.setData({
        coupons: [
          {
            value: '8',
            title: '春节优惠',
            desc: '春节期间寄件立减8元',
            expire: '2026-02-15'
          }
        ]
      });
    }
  },

  exchangeGoods(e) {
    const index = e.currentTarget.dataset.index;
    const goods = this.data.goodsList[index];
    
    if (this.data.points >= goods.points) {
      wx.showModal({
        title: '积分兑换',
        content: `确定用${goods.points}积分兑换${goods.name}吗？`,
        success: (res) => {
          if (res.confirm) {
            this.setData({
              points: this.data.points - goods.points
            });
            wx.showToast({
              title: '兑换成功',
              icon: 'success'
            });
          }
        }
      });
    } else {
      wx.showToast({
        title: '积分不足',
        icon: 'none'
      });
    }
  }
});