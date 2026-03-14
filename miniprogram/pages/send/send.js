const app = getApp();
const api = require('../../utils/api.js');
const config = require('../../utils/config.js');

Page({
  data: {
    senderIndex: -1,
    senderList: [],
    senderId: null,
    sender: {
      name: '',
      phone: '',
      address: ''
    },
    receiverIndex: -1,
    receiverList: [],
    receiverId: null,
    receiver: {
      name: '',
      phone: '',
      address: ''
    },
    goods: {
      description: '',
      weight: '',
      volume: '',
      remark: ''
    },
    goodsPhotos: [],
    maxPhotoCount: 5,
    businessUserId: null,
    showModal: false,
    modalType: 'add',
    modalTitle: '',
    modalContactType: 0,
    editingId: null,
    modalForm: {
      customerName: '',
      phone: '',
      address: ''
    }
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo && userInfo.businessUserId) {
      this.setData({
        businessUserId: userInfo.businessUserId
      });
      this.loadContacts();
    } else {
      wx.showToast({
        title: '您还未分配客户身份',
        icon: 'none'
      });
    }
  },

  loadContacts() {
    const businessUserId = this.data.businessUserId;
    if (!businessUserId) {
      return;
    }

    api.getSenders(businessUserId).then(data => {
      this.setData({
        senderList: data || []
      });
    }).catch(err => {
      console.error('获取发件人列表失败', err);
    });

    api.getRecipients(businessUserId).then(data => {
      this.setData({
        receiverList: data || []
      });
    }).catch(err => {
      console.error('获取收件人列表失败', err);
    });
  },

  bindSenderChange(e) {
    const index = e.detail.value;
    const sender = this.data.senderList[index];
    if (!sender) {
      return;
    }
    const phone = sender.phone || sender.contact || '';
    this.setData({
      senderIndex: index,
      senderId: sender.id || null,
      sender: {
        name: sender.customerName || '',
        phone: phone,
        address: sender.address || ''
      }
    });
  },

  bindReceiverChange(e) {
    const index = e.detail.value;
    const receiver = this.data.receiverList[index];
    if (!receiver) {
      return;
    }
    const phone = receiver.phone || receiver.contact || '';
    this.setData({
      receiverIndex: index,
      receiverId: receiver.id || null,
      receiver: {
        name: receiver.customerName || '',
        phone: phone,
        address: receiver.address || ''
      }
    });
  },

  bindSenderInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`sender.${field}`]: value
    });
  },

  bindReceiverInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`receiver.${field}`]: value
    });
  },

  bindGoodsInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`goods.${field}`]: value
    });
  },

  addSender() {
    this.setData({
      showModal: true,
      modalType: 'add',
      modalTitle: '发件人',
      modalContactType: 0,
      editingId: null,
      modalForm: {
        customerName: '',
        phone: '',
        address: ''
      }
    });
  },

  addReceiver() {
    this.setData({
      showModal: true,
      modalType: 'add',
      modalTitle: '收件人',
      modalContactType: 1,
      editingId: null,
      modalForm: {
        customerName: '',
        phone: '',
        address: ''
      }
    });
  },

  onModalInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`modalForm.${field}`]: value
    });
  },

  closeModal() {
    this.setData({
      showModal: false
    });
  },

  submitModal() {
    const { modalForm, modalContactType, businessUserId, editingId } = this.data;
    
    if (!modalForm.customerName || !modalForm.phone) {
      wx.showToast({
        title: '请填写完整信息',
        icon: 'none'
      });
      return;
    }

    const data = {
      ...modalForm,
      businessUserId: businessUserId,
      type: modalContactType
    };

    if (editingId) {
      data.id = editingId;
      api.updateContact(data).then(() => {
        wx.showToast({
          title: '更新成功',
          icon: 'success'
        });
        this.closeModal();
        this.loadContacts();
      }).catch(err => {
        wx.showToast({
          title: '更新失败',
          icon: 'none'
        });
        console.error('更新联系人失败', err);
      });
    } else {
      const apiCall = modalContactType === 0 ? api.addSender : api.addRecipient;
      apiCall(data).then(() => {
        wx.showToast({
          title: '添加成功',
          icon: 'success'
        });
        this.closeModal();
        this.loadContacts();
      }).catch(err => {
        wx.showToast({
          title: '添加失败',
          icon: 'none'
        });
        console.error('添加联系人失败', err);
      });
    }
  },

  chooseImage() {
    if (this.data.goodsPhotos.length >= this.data.maxPhotoCount) {
      wx.showToast({
        title: '最多上传5张照片',
        icon: 'none'
      });
      return;
    }

    wx.chooseMedia({
      count: this.data.maxPhotoCount - this.data.goodsPhotos.length,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newPhotos = res.tempFiles.map(file => file.tempFilePath);
        this.setData({
          goodsPhotos: this.data.goodsPhotos.concat(newPhotos)
        });
      }
    });
  },

  deletePhoto(e) {
    const index = e.currentTarget.dataset.index;
    const photos = this.data.goodsPhotos;
    photos.splice(index, 1);
    this.setData({
      goodsPhotos: photos
    });
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.goodsPhotos
    });
  },

  submitOrder() {
    if (this.data.senderIndex < 0) {
      wx.showToast({
        title: '请选择发件人',
        icon: 'none'
      });
      return;
    }

    if (this.data.receiverIndex < 0) {
      wx.showToast({
        title: '请选择收件人',
        icon: 'none'
      });
      return;
    }

    if (!this.data.goods.description || !this.data.goods.weight) {
      wx.showToast({
        title: '请填写货物描述和重量',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '提交中...'
    });

    const token = wx.getStorageSync('token');
    const businessUserId = this.data.businessUserId;

    wx.request({
      url: `${config.getApiUrl()}/order`,
      method: 'POST',
      header: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      data: {
        businessUserId: businessUserId,
        senderId: this.data.senderId,
        senderName: this.data.senderList[this.data.senderIndex].customerName,
        senderPhone: this.data.senderList[this.data.senderIndex].phone || this.data.senderList[this.data.senderIndex].contact,
        senderAddress: this.data.senderList[this.data.senderIndex].address,
        recipientId: this.data.receiverId,
        receiverName: this.data.receiverList[this.data.receiverIndex].customerName,
        receiverPhone: this.data.receiverList[this.data.receiverIndex].phone || this.data.receiverList[this.data.receiverIndex].contact,
        receiverAddress: this.data.receiverList[this.data.receiverIndex].address,
        goodsName: this.data.goods.description,
        weight: parseFloat(this.data.goods.weight) || 0,
        volume: parseFloat(this.data.goods.volume) || 0,
        orderType: 0,
        status: 0
      },
      success: (res) => {
        if (res.data && res.data.id) {
          const orderId = res.data.id;
          const orderNo = res.data.orderNo;
          this.uploadGoodsImages(orderId, orderNo);
        } else {
          wx.hideLoading();
          wx.showToast({
            title: '提交失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '提交失败',
          icon: 'none'
        });
        console.error('提交订单失败', err);
      }
    });
  },

  uploadGoodsImages(orderId, orderNo) {
    if (this.data.goodsPhotos.length === 0) {
      wx.hideLoading();
      wx.showToast({
        title: '寄件成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
      return;
    }

    const token = wx.getStorageSync('token');
    let uploadedCount = 0;

    for (let i = 0; i < this.data.goodsPhotos.length; i++) {
      wx.uploadFile({
        url: `${config.getApiUrl()}/order/goods-image/upload/cos`,
        filePath: this.data.goodsPhotos[i],
        name: 'file',
        header: {
          'Authorization': `Bearer ${token}`
        },
        formData: {
          orderId: orderId,
          orderNo: orderNo
        },
        success: (res) => {
          uploadedCount++;
          if (uploadedCount === this.data.goodsPhotos.length) {
            wx.hideLoading();
            wx.showToast({
              title: '寄件成功',
              icon: 'success'
            });
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          }
        },
        fail: (err) => {
          uploadedCount++;
          console.error('上传图片失败', err);
          if (uploadedCount === this.data.goodsPhotos.length) {
            wx.hideLoading();
            wx.showToast({
              title: '部分图片上传失败',
              icon: 'none'
            });
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          }
        }
      });
    }
  }
});
