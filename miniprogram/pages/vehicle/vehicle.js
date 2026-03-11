// vehicle.js
Page({
  data: {
    vehicleList: [],
    loading: false,
    licensePlate: ''
  },
  onLoad() {
    this.getVehicleList();
  },
  onLicensePlateInput(e) {
    this.setData({
      licensePlate: e.detail.value
    });
  },
  getVehicleList() {
    this.setData({ loading: true });
    // 调用云函数获取车辆列表
    wx.cloud.callFunction({
      name: 'getVehicleList',
      data: {
        page: 1,
        size: 10,
        licensePlate: this.data.licensePlate
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          vehicleList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('获取车辆列表失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  searchVehicle() {
    // 调用云函数搜索车辆
    this.setData({ loading: true });
    wx.cloud.callFunction({
      name: 'getVehicleList',
      data: {
        page: 1,
        size: 10,
        licensePlate: this.data.licensePlate
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          vehicleList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('搜索车辆失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  viewVehicleDetail(e) {
    const id = e.currentTarget.dataset.id;
    console.log('查看车辆详情:', id);
  },
  addVehicle() {
    console.log('新增车辆');
  }
});
