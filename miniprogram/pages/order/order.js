Page({
  data: {
    activeTab: 'all',
    orderList: [
      {
        number: 'SF1234567890',
        time: '2026-03-07 10:00:00',
        status: '运输中',
        service: '标准快递',
        sender: '张三 13800138000',
        receiver: '李四 13900139000',
        cost: '20',
        action: '查看物流'
      },
      {
        number: 'SF0987654321',
        time: '2026-03-06 15:30:00',
        status: '待付款',
        service: '加急',
        sender: '王五 13700137000',
        receiver: '赵六 13600136000',
        cost: '35',
        action: '立即付款'
      },
      {
        number: 'SF2468135790',
        time: '2026-03-05 09:15:00',
        status: '已完成',
        service: '标准快递',
        sender: '孙七 13500135000',
        receiver: '周八 13400134000',
        cost: '15',
        action: '再次寄件'
      }
    ]
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    
    // 模拟不同状态的订单
    if (tab === 'all') {
      this.setData({
        orderList: [
          {
            number: 'SF1234567890',
            time: '2026-03-07 10:00:00',
            status: '运输中',
            service: '标准快递',
            sender: '张三 13800138000',
            receiver: '李四 13900139000',
            cost: '20',
            action: '查看物流'
          },
          {
            number: 'SF0987654321',
            time: '2026-03-06 15:30:00',
            status: '待付款',
            service: '加急',
            sender: '王五 13700137000',
            receiver: '赵六 13600136000',
            cost: '35',
            action: '立即付款'
          },
          {
            number: 'SF2468135790',
            time: '2026-03-05 09:15:00',
            status: '已完成',
            service: '标准快递',
            sender: '孙七 13500135000',
            receiver: '周八 13400134000',
            cost: '15',
            action: '再次寄件'
          }
        ]
      });
    } else if (tab === 'pending') {
      this.setData({
        orderList: [
          {
            number: 'SF0987654321',
            time: '2026-03-06 15:30:00',
            status: '待付款',
            service: '加急',
            sender: '王五 13700137000',
            receiver: '赵六 13600136000',
            cost: '35',
            action: '立即付款'
          }
        ]
      });
    } else if (tab === 'pickup') {
      this.setData({
        orderList: []
      });
    } else if (tab === 'transport') {
      this.setData({
        orderList: [
          {
            number: 'SF1234567890',
            time: '2026-03-07 10:00:00',
            status: '运输中',
            service: '标准快递',
            sender: '张三 13800138000',
            receiver: '李四 13900139000',
            cost: '20',
            action: '查看物流'
          }
        ]
      });
    } else {
      this.setData({
        orderList: [
          {
            number: 'SF2468135790',
            time: '2026-03-05 09:15:00',
            status: '已完成',
            service: '标准快递',
            sender: '孙七 13500135000',
            receiver: '周八 13400134000',
            cost: '15',
            action: '再次寄件'
          }
        ]
      });
    }
  },

  viewOrderDetail(e) {
    const index = e.currentTarget.dataset.index;
    const order = this.data.orderList[index];
    wx.navigateTo({
      url: `/pages/logistics/logistics?number=${order.number}`
    });
  },

  cancelOrder(e) {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '订单已取消',
            icon: 'success'
          });
          // 模拟刷新订单列表
          this.switchTab({ currentTarget: { dataset: { tab: this.data.activeTab } } });
        }
      }
    });
  },

  payOrder(e) {
    const index = e.currentTarget.dataset.index;
    const order = this.data.orderList[index];
    
    if (order.status === '待付款') {
      wx.showModal({
        title: '支付订单',
        content: `确定支付¥${order.cost}吗？`,
        success: (res) => {
          if (res.confirm) {
            wx.showToast({
              title: '支付成功',
              icon: 'success'
            });
            // 模拟刷新订单列表
            this.switchTab({ currentTarget: { dataset: { tab: this.data.activeTab } } });
          }
        }
      });
    } else if (order.status === '运输中') {
      wx.navigateTo({
        url: `/pages/logistics/logistics?number=${order.number}`
      });
    } else if (order.status === '已完成') {
      wx.navigateTo({
        url: '/pages/send/send'
      });
    }
  }
});