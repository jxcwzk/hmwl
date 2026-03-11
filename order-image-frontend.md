# 订单图片前端实现方案

## 1. 小程序端实现

### 1.1 图片上传功能

#### 1.1.1 选择图片

```javascript
// 选择图片
chooseImage: function(imageType) {
  const that = this;
  wx.chooseImage({
    count: 1, // 最多选择1张图片
    sizeType: ['compressed'], // 压缩图片
    sourceType: ['camera', 'album'], // 可从相册和相机选择
    success: function(res) {
      const tempFilePaths = res.tempFilePaths;
      that.uploadImage(tempFilePaths[0], imageType);
    },
    fail: function(err) {
      wx.showToast({
        title: '选择图片失败',
        icon: 'none'
      });
    }
  });
}
```

#### 1.1.2 上传图片

```javascript
// 上传图片
uploadImage: function(tempFilePath, imageType) {
  const that = this;
  const orderId = that.data.orderId;
  
  wx.showLoading({
    title: '上传中...',
    mask: true
  });
  
  // 构造FormData
  const formData = {
    orderId: orderId,
    imageType: imageType
  };
  
  wx.uploadFile({
    url: 'http://localhost:8080/order/image/upload', // 后端接口地址
    filePath: tempFilePath,
    name: 'file',
    formData: formData,
    success: function(res) {
      const result = JSON.parse(res.data);
      if (result.code === 200) {
        wx.showToast({
          title: '上传成功',
          icon: 'success'
        });
        // 刷新图片列表
        that.getImageList();
      } else {
        wx.showToast({
          title: result.message || '上传失败',
          icon: 'none'
        });
      }
    },
    fail: function(err) {
      wx.showToast({
        title: '上传失败',
        icon: 'none'
      });
    },
    complete: function() {
      wx.hideLoading();
    }
  });
}
```

#### 1.1.3 获取图片列表

```javascript
// 获取订单图片列表
getImageList: function() {
  const that = this;
  const orderId = that.data.orderId;
  
  wx.request({
    url: 'http://localhost:8080/order/image/list',
    method: 'GET',
    data: {
      orderId: orderId
    },
    success: function(res) {
      if (res.data.code === 200) {
        that.setData({
          imageList: res.data.data
        });
      } else {
        wx.showToast({
          title: res.data.message || '获取图片列表失败',
          icon: 'none'
        });
      }
    },
    fail: function(err) {
      wx.showToast({
        title: '获取图片列表失败',
        icon: 'none'
      });
    }
  });
}
```

#### 1.1.4 删除图片

```javascript
// 删除图片
deleteImage: function(imageId) {
  const that = this;
  
  wx.showModal({
    title: '提示',
    content: '确定要删除这张图片吗？',
    success: function(res) {
      if (res.confirm) {
        wx.request({
          url: `http://localhost:8080/order/image/${imageId}`,
          method: 'DELETE',
          success: function(res) {
            if (res.data.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              });
              // 刷新图片列表
              that.getImageList();
            } else {
              wx.showToast({
                title: res.data.message || '删除失败',
                icon: 'none'
              });
            }
          },
          fail: function(err) {
            wx.showToast({
              title: '删除失败',
              icon: 'none'
            });
          }
        });
      }
    }
  });
}
```

### 1.2 图片展示组件

#### 1.2.1 WXML结构

```xml
<view class="image-upload-section">
  <view class="section-title">
    <text>回单上传</text>
    <button bindtap="chooseImage" data-type="1" type="primary" size="mini">上传</button>
  </view>
  <view class="image-list">
    <block wx:for="{{imageList}}" wx:key="id">
      <view wx:if="{{item.imageType === 1}}" class="image-item">
        <image src="{{item.imageUrl}}" mode="aspectFill" bindtap="previewImage" data-url="{{item.imageUrl}}" />
        <view class="image-actions">
          <button bindtap="deleteImage" data-id="{{item.id}}" type="warn" size="mini">删除</button>
        </view>
      </view>
    </block>
  </view>
</view>

<view class="image-upload-section">
  <view class="section-title">
    <text>发货单上传</text>
    <button bindtap="chooseImage" data-type="2" type="primary" size="mini">上传</button>
  </view>
  <view class="image-list">
    <block wx:for="{{imageList}}" wx:key="id">
      <view wx:if="{{item.imageType === 2}}" class="image-item">
        <image src="{{item.imageUrl}}" mode="aspectFill" bindtap="previewImage" data-url="{{item.imageUrl}}" />
        <view class="image-actions">
          <button bindtap="deleteImage" data-id="{{item.id}}" type="warn" size="mini">删除</button>
        </view>
      </view>
    </block>
  </view>
</view>

<view class="image-upload-section">
  <view class="section-title">
    <text>其他图片</text>
    <button bindtap="chooseImage" data-type="3" type="primary" size="mini">上传</button>
  </view>
  <view class="image-list">
    <block wx:for="{{imageList}}" wx:key="id">
      <view wx:if="{{item.imageType === 3}}" class="image-item">
        <image src="{{item.imageUrl}}" mode="aspectFill" bindtap="previewImage" data-url="{{item.imageUrl}}" />
        <view class="image-actions">
          <button bindtap="deleteImage" data-id="{{item.id}}" type="warn" size="mini">删除</button>
        </view>
      </view>
    </block>
  </view>
</view>
```

#### 1.2.2 WXSS样式

```css
.image-upload-section {
  margin: 20rpx 0;
  padding: 20rpx;
  background-color: #f5f5f5;
  border-radius: 10rpx;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  font-size: 32rpx;
  font-weight: bold;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  width: 200rpx;
  height: 200rpx;
  position: relative;
}

.image-item image {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}

.image-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  padding: 10rpx;
  border-bottom-left-radius: 10rpx;
  border-bottom-right-radius: 10rpx;
  display: flex;
  justify-content: flex-end;
}
```

#### 1.2.3 图片预览功能

```javascript
// 预览图片
previewImage: function(e) {
  const url = e.currentTarget.dataset.url;
  wx.previewImage({
    urls: [url],
    current: url
  });
}
```

## 2. Web端实现

### 2.1 图片上传功能

#### 2.1.1 HTML结构

```html
<div class="image-upload-section">
  <h3>回单上传</h3>
  <div class="upload-area">
    <input type="file" id="receipt-upload" accept="image/*" style="display: none;" />
    <button class="btn-upload" onclick="document.getElementById('receipt-upload').click()">上传图片</button>
  </div>
  <div class="image-list" id="receipt-list"></div>
</div>

<div class="image-upload-section">
  <h3>发货单上传</h3>
  <div class="upload-area">
    <input type="file" id="invoice-upload" accept="image/*" style="display: none;" />
    <button class="btn-upload" onclick="document.getElementById('invoice-upload').click()">上传图片</button>
  </div>
  <div class="image-list" id="invoice-list"></div>
</div>

<div class="image-upload-section">
  <h3>其他图片</h3>
  <div class="upload-area">
    <input type="file" id="other-upload" accept="image/*" style="display: none;" />
    <button class="btn-upload" onclick="document.getElementById('other-upload').click()">上传图片</button>
  </div>
  <div class="image-list" id="other-list"></div>
</div>
```

#### 2.1.2 JavaScript代码

```javascript
// 全局变量
let orderId = 123; // 订单ID，实际应该从URL或其他地方获取

// 初始化
window.onload = function() {
  // 绑定文件上传事件
  document.getElementById('receipt-upload').addEventListener('change', function(e) {
    uploadImage(e.target.files[0], 1);
  });
  
  document.getElementById('invoice-upload').addEventListener('change', function(e) {
    uploadImage(e.target.files[0], 2);
  });
  
  document.getElementById('other-upload').addEventListener('change', function(e) {
    uploadImage(e.target.files[0], 3);
  });
  
  // 获取图片列表
  getImageList();
};

// 上传图片
function uploadImage(file, imageType) {
  const formData = new FormData();
  formData.append('orderId', orderId);
  formData.append('imageType', imageType);
  formData.append('file', file);
  
  // 显示加载动画
  showLoading();
  
  fetch('http://localhost:8080/order/image/upload', {
    method: 'POST',
    body: formData
  })
  .then(response => response.json())
  .then(data => {
    hideLoading();
    if (data.code === 200) {
      showMessage('上传成功');
      getImageList();
    } else {
      showMessage(data.message || '上传失败');
    }
  })
  .catch(error => {
    hideLoading();
    showMessage('上传失败');
    console.error('Error:', error);
  });
}

// 获取图片列表
function getImageList() {
  fetch(`http://localhost:8080/order/image/list?orderId=${orderId}`)
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      renderImageList(data.data);
    } else {
      showMessage(data.message || '获取图片列表失败');
    }
  })
  .catch(error => {
    showMessage('获取图片列表失败');
    console.error('Error:', error);
  });
}

// 渲染图片列表
function renderImageList(images) {
  // 清空列表
  document.getElementById('receipt-list').innerHTML = '';
  document.getElementById('invoice-list').innerHTML = '';
  document.getElementById('other-list').innerHTML = '';
  
  // 分类渲染
  images.forEach(image => {
    const imageItem = createImageItem(image);
    if (image.imageType === 1) {
      document.getElementById('receipt-list').appendChild(imageItem);
    } else if (image.imageType === 2) {
      document.getElementById('invoice-list').appendChild(imageItem);
    } else if (image.imageType === 3) {
      document.getElementById('other-list').appendChild(imageItem);
    }
  });
}

// 创建图片项
function createImageItem(image) {
  const div = document.createElement('div');
  div.className = 'image-item';
  
  const img = document.createElement('img');
  img.src = image.imageUrl;
  img.alt = '图片';
  img.onclick = function() {
    previewImage(image.imageUrl);
  };
  
  const deleteBtn = document.createElement('button');
  deleteBtn.className = 'btn-delete';
  deleteBtn.textContent = '删除';
  deleteBtn.onclick = function() {
    deleteImage(image.id);
  };
  
  div.appendChild(img);
  div.appendChild(deleteBtn);
  
  return div;
}

// 删除图片
function deleteImage(imageId) {
  if (confirm('确定要删除这张图片吗？')) {
    showLoading();
    
    fetch(`http://localhost:8080/order/image/${imageId}`, {
      method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
      hideLoading();
      if (data.code === 200) {
        showMessage('删除成功');
        getImageList();
      } else {
        showMessage(data.message || '删除失败');
      }
    })
    .catch(error => {
      hideLoading();
      showMessage('删除失败');
      console.error('Error:', error);
    });
  }
}

// 预览图片
function previewImage(url) {
  // 创建预览窗口
  const preview = document.createElement('div');
  preview.className = 'image-preview';
  
  const img = document.createElement('img');
  img.src = url;
  
  const closeBtn = document.createElement('button');
  closeBtn.className = 'btn-close';
  closeBtn.textContent = '关闭';
  closeBtn.onclick = function() {
    document.body.removeChild(preview);
  };
  
  preview.appendChild(img);
  preview.appendChild(closeBtn);
  document.body.appendChild(preview);
}

// 显示加载动画
function showLoading() {
  // 实现加载动画
  const loading = document.createElement('div');
  loading.id = 'loading';
  loading.className = 'loading';
  loading.textContent = '加载中...';
  document.body.appendChild(loading);
}

// 隐藏加载动画
function hideLoading() {
  const loading = document.getElementById('loading');
  if (loading) {
    document.body.removeChild(loading);
  }
}

// 显示消息
function showMessage(message) {
  // 实现消息提示
  const msg = document.createElement('div');
  msg.className = 'message';
  msg.textContent = message;
  document.body.appendChild(msg);
  
  setTimeout(() => {
    document.body.removeChild(msg);
  }, 3000);
}
```

#### 2.1.3 CSS样式

```css
.image-upload-section {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
}

.image-upload-section h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: bold;
}

.upload-area {
  margin-bottom: 20px;
}

.btn-upload {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-upload:hover {
  background-color: #45a049;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.image-item {
  width: 150px;
  height: 150px;
  position: relative;
}

.image-item img {
  width: 100%;
  height: 100%;
  border-radius: 5px;
  cursor: pointer;
}

.btn-delete {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(255, 0, 0, 0.7);
  color: white;
  border: none;
  padding: 5px;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  cursor: pointer;
}

.btn-delete:hover {
  background-color: rgba(255, 0, 0, 0.9);
}

.image-preview {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.image-preview img {
  max-width: 90%;
  max-height: 80%;
}

.btn-close {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-close:hover {
  background-color: #da190b;
}

.loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 20px;
  border-radius: 5px;
  z-index: 1000;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: #4CAF50;
  color: white;
  padding: 15px;
  border-radius: 5px;
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
```

## 3. 前端最佳实践

### 3.1 性能优化

1. **图片压缩**：在上传前对图片进行压缩，减少文件大小
2. **批量上传**：支持多图批量上传，提高用户体验
3. **上传进度**：显示上传进度，让用户了解上传状态
4. **缓存策略**：对图片列表进行缓存，减少重复请求

### 3.2 用户体验

1. **拖拽上传**：支持拖拽文件到上传区域
2. **图片预览**：上传前预览图片，确认无误后再上传
3. **批量操作**：支持批量删除图片
4. **错误处理**：友好的错误提示，引导用户正确操作

### 3.3 安全性

1. **文件类型验证**：只允许上传图片文件
2. **文件大小限制**：限制上传文件的大小
3. **防重复上传**：避免重复上传相同的图片
4. **权限控制**：确保只有有权限的用户才能上传和删除图片