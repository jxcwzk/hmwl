Page({
  data: {
    selectedMode: 'pickup',
    sender: {
      name: '',
      phone: '',
      address: ''
    },
    receiver: {
      name: '',
      phone: '',
      address: ''
    },
    goodsTypes: ['文件', '电子产品', '服装', '食品', '其他'],
    goodsTypeIndex: 0,
    goods: {
      weight: '',
      quantity: ''
    },
    selectedService: 'standard',
    estimatedCost: 0
  },

  selectMode(e) {
    this.setData({
      selectedMode: e.currentTarget.dataset.mode
    });
  },

  bindSenderInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`sender.${field}`]: value
    });
    this.calculateCost();
  },

  bindReceiverInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`receiver.${field}`]: value
    });
  },

  bindGoodsTypeChange(e) {
    this.setData({
      goodsTypeIndex: e.detail.value
    });
    this.calculateCost();
  },

  bindGoodsInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`goods.${field}`]: value
    });
    this.calculateCost();
  },

  selectService(e) {
    this.setData({
      selectedService: e.currentTarget.dataset.service
    });
    this.calculateCost();
  },

  calculateCost() {
    const weight = parseFloat(this.data.goods.weight) || 0;
    let baseCost = 12;
    
    if (weight > 1) {
      baseCost += (weight - 1) * 8;
    }
    
    if (this.data.selectedService === 'express') {
      baseCost += 10;
    } else if (this.data.selectedService === 'cold') {
      baseCost += 15;
    }
    
    this.setData({
      estimatedCost: baseCost
    });
  },

  submitOrder() {
    // 表单验证
    if (!this.data.sender.name || !this.data.sender.phone || !this.data.sender.address) {
      wx.showToast({
        title: '请填写完整的寄件人信息',
        icon: 'none'
      });
      return;
    }
    
    if (!this.data.receiver.name || !this.data.receiver.phone || !this.data.receiver.address) {
      wx.showToast({
        title: '请填写完整的收件人信息',
        icon: 'none'
      });
      return;
    }
    
    if (!this.data.goods.weight || !this.data.goods.quantity) {
      wx.showToast({
        title: '请填写物品信息',
        icon: 'none'
      });
      return;
    }
    
    // 模拟提交订单
    wx.showLoading({
      title: '提交中...'
    });
    
    setTimeout(() => {
      wx.hideLoading();
      wx.showToast({
        title: '寄件成功',
        icon: 'success'
      });
      
      // 跳转到订单页面
      wx.navigateTo({
        url: '/pages/order/order'
      });
    }, 1000);
  }
});