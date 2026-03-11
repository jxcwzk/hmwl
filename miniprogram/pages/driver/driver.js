// driver.js
Page({
  data: {
    driverList: [],
    loading: false,
    driverName: ''
  },
  onLoad() {
    this.getDriverList();
  },
  onDriverNameInput(e) {
    this.setData({
      driverName: e.detail.value
    });
  },
  getDriverList() {
    this.setData({ loading: true });
    // 调用云函数获取司机列表
    wx.cloud.callFunction({
      name: 'getDriverList',
      data: {
        page: 1,
        size: 10,
        name: this.data.driverName
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          driverList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('获取司机列表失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  searchDriver() {
    // 调用云函数搜索司机
    this.setData({ loading: true });
    wx.cloud.callFunction({
      name: 'getDriverList',
      data: {
        page: 1,
        size: 10,
        name: this.data.driverName
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          driverList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('搜索司机失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  viewDriverDetail(e) {
    const id = e.currentTarget.dataset.id;
    console.log('查看司机详情:', id);
  },
  addDriver() {
    console.log('新增司机');
  }
});
