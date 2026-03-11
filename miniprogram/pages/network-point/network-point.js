// network-point.js
Page({
  data: {
    networkList: [],
    loading: false,
    networkName: ''
  },
  onLoad() {
    this.getNetworkList();
  },
  onNetworkNameInput(e) {
    this.setData({
      networkName: e.detail.value
    });
  },
  getNetworkList() {
    this.setData({ loading: true });
    // 调用云函数获取网点列表
    wx.cloud.callFunction({
      name: 'getNetworkList',
      data: {
        page: 1,
        size: 10,
        name: this.data.networkName
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          networkList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('获取网点列表失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  searchNetwork() {
    // 调用云函数搜索网点
    this.setData({ loading: true });
    wx.cloud.callFunction({
      name: 'getNetworkList',
      data: {
        page: 1,
        size: 10,
        name: this.data.networkName
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          networkList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('搜索网点失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  viewNetworkDetail(e) {
    const id = e.currentTarget.dataset.id;
    console.log('查看网点详情:', id);
  },
  addNetwork() {
    console.log('新增网点');
  }
});
