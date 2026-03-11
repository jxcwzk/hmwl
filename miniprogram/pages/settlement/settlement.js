// settlement.js
Page({
  data: {
    settlementList: [],
    loading: false,
    settlementNo: ''
  },
  onLoad() {
    this.getSettlementList();
  },
  onSettlementNoInput(e) {
    this.setData({
      settlementNo: e.detail.value
    });
  },
  getSettlementList() {
    this.setData({ loading: true });
    // 调用云函数获取结算列表
    wx.cloud.callFunction({
      name: 'getSettlementList',
      data: {
        page: 1,
        size: 10,
        settlementNo: this.data.settlementNo
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          settlementList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('获取结算列表失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  searchSettlement() {
    // 调用云函数搜索结算
    this.setData({ loading: true });
    wx.cloud.callFunction({
      name: 'getSettlementList',
      data: {
        page: 1,
        size: 10,
        settlementNo: this.data.settlementNo
      }
    }).then(res => {
      if (res.result.success) {
        this.setData({
          settlementList: res.result.data.records,
          loading: false
        });
      } else {
        console.error('搜索结算失败:', res.result.error);
        this.setData({ loading: false });
      }
    }).catch(error => {
      console.error('调用云函数失败:', error);
      this.setData({ loading: false });
    });
  },
  viewSettlementDetail(e) {
    const id = e.currentTarget.dataset.id;
    console.log('查看结算详情:', id);
  },
  addSettlement() {
    console.log('新增结算');
  },
  reconciliation() {
    console.log('对账管理');
  },
  statistics() {
    console.log('统计分析');
  },
  getSettlementTypeText(type) {
    const typeMap = {
      0: '客户应收',
      1: '司机应付',
      2: '网点间结算'
    };
    return typeMap[type] || '未知';
  },
  getStatusText(status) {
    const statusMap = {
      0: '未结算',
      1: '已结算',
      2: '已付款'
    };
    return statusMap[status] || '未知';
  }
});
