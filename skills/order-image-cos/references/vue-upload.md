# Vue图片上传组件

## 1. 环境准备

### 1.1 依赖安装

在Vue项目中安装必要的依赖：

```bash
npm install axios
```

## 2. 图片上传组件

### 2.1 创建组件

创建`OrderImageUpload.vue`组件：

```vue
<template>
  <div class="order-image-upload">
    <h3>{{ title }}</h3>
    <div class="upload-area">
      <input
        type="file"
        id="file-input"
        accept="image/*"
        @change="handleFileChange"
        style="display: none;"
      />
      <button class="btn-upload" @click="chooseImage">上传图片</button>
    </div>
    <div class="image-list">
      <div v-for="image in images" :key="image.id" class="image-item">
        <img :src="image.imageUrl" alt="图片" @click="previewImage(image.imageUrl)" />
        <button class="btn-delete" @click="deleteImage(image.id)">删除</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'OrderImageUpload',
  props: {
    title: {
      type: String,
      default: '图片上传'
    },
    orderId: {
      type: Number,
      required: true
    },
    orderNo: {
      type: String,
      required: true
    },
    imageType: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      images: []
    };
  },
  mounted() {
    this.getImageList();
  },
  methods: {
    chooseImage() {
      document.getElementById('file-input').click();
    },
    handleFileChange(e) {
      const file = e.target.files[0];
      if (file) {
        this.uploadImage(file);
      }
    },
    uploadImage(file) {
      const formData = new FormData();
      formData.append('orderId', this.orderId);
      formData.append('orderNo', this.orderNo);
      formData.append('imageType', this.imageType);
      formData.append('file', file);

      axios.post('/api/order/image/upload/cos', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        if (response.data.code === 200) {
          this.getImageList();
          alert('上传成功');
        } else {
          alert('上传失败：' + response.data.message);
        }
      })
      .catch(error => {
        alert('上传失败：' + error.message);
      });
    },
    getImageList() {
      axios.get('/api/order/image/list', {
        params: {
          orderId: this.orderId
        }
      })
      .then(response => {
        if (response.data.code === 200) {
          this.images = response.data.data.filter(image => image.imageType === this.imageType);
        }
      })
      .catch(error => {
        console.error('获取图片列表失败：', error);
      });
    },
    deleteImage(imageId) {
      if (confirm('确定要删除这张图片吗？')) {
        axios.delete(`/api/order/image/${imageId}`)
        .then(response => {
          if (response.data.code === 200) {
            this.getImageList();
            alert('删除成功');
          } else {
            alert('删除失败：' + response.data.message);
          }
        })
        .catch(error => {
          alert('删除失败：' + error.message);
        });
      }
    },
    previewImage(url) {
      // 实现图片预览功能
      window.open(url, '_blank');
    }
  }
};
</script>

<style scoped>
.order-image-upload {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
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
</style>
```

## 3. 集成到订单页面

### 3.1 在订单创建页面使用

在`OrderCreate.vue`页面中集成图片上传组件：

```vue
<template>
  <div class="order-create">
    <h2>创建订单</h2>
    <!-- 订单表单 -->
    <form @submit.prevent="submitForm">
      <!-- 订单信息输入 -->
      <div class="form-group">
        <label>订单编号</label>
        <input v-model="order.orderNo" type="text" placeholder="订单编号" />
      </div>
      <!-- 其他表单字段 -->
      
      <!-- 图片上传 -->
      <OrderImageUpload 
        title="回单上传" 
        :orderId="orderId" 
        :orderNo="order.orderNo" 
        :imageType="1" 
      />
      <OrderImageUpload 
        title="发货单上传" 
        :orderId="orderId" 
        :orderNo="order.orderNo" 
        :imageType="2" 
      />
      <OrderImageUpload 
        title="其他图片" 
        :orderId="orderId" 
        :orderNo="order.orderNo" 
        :imageType="3" 
      />
      
      <button type="submit" class="btn-submit">提交订单</button>
    </form>
  </div>
</template>

<script>
import OrderImageUpload from './OrderImageUpload.vue';
import axios from 'axios';

export default {
  name: 'OrderCreate',
  components: {
    OrderImageUpload
  },
  data() {
    return {
      orderId: 0,
      order: {
        orderNo: '',
        // 其他订单字段
      }
    };
  },
  methods: {
    submitForm() {
      // 提交订单
      axios.post('/api/order', this.order)
      .then(response => {
        if (response.data.code === 200) {
          this.orderId = response.data.data.id;
          alert('订单创建成功');
        } else {
          alert('订单创建失败：' + response.data.message);
        }
      })
      .catch(error => {
        alert('订单创建失败：' + error.message);
      });
    }
  }
};
</script>

<style scoped>
.order-create {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.btn-submit {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.btn-submit:hover {
  background-color: #45a049;
}
</style>
```

### 3.2 在订单详情页面使用

在`OrderDetail.vue`页面中集成图片上传组件：

```vue
<template>
  <div class="order-detail">
    <h2>订单详情</h2>
    <!-- 订单信息展示 -->
    <div class="order-info">
      <p><strong>订单编号：</strong>{{ order.orderNo }}</p>
      <!-- 其他订单信息 -->
    </div>
    
    <!-- 图片上传 -->
    <OrderImageUpload 
      title="回单上传" 
      :orderId="orderId" 
      :orderNo="order.orderNo" 
      :imageType="1" 
    />
    <OrderImageUpload 
      title="发货单上传" 
      :orderId="orderId" 
      :orderNo="order.orderNo" 
      :imageType="2" 
    />
    <OrderImageUpload 
      title="其他图片" 
      :orderId="orderId" 
      :orderNo="order.orderNo" 
      :imageType="3" 
    />
  </div>
</template>

<script>
import OrderImageUpload from './OrderImageUpload.vue';
import axios from 'axios';

export default {
  name: 'OrderDetail',
  components: {
    OrderImageUpload
  },
  data() {
    return {
      orderId: this.$route.params.id,
      order: {
        orderNo: '',
        // 其他订单字段
      }
    };
  },
  mounted() {
    this.getOrderDetail();
  },
  methods: {
    getOrderDetail() {
      axios.get(`/api/order/${this.orderId}`)
      .then(response => {
        if (response.data.code === 200) {
          this.order = response.data.data;
        } else {
          alert('获取订单详情失败：' + response.data.message);
        }
      })
      .catch(error => {
        alert('获取订单详情失败：' + error.message);
      });
    }
  }
};
</script>

<style scoped>
.order-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.order-info {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
}

.order-info p {
  margin: 10px 0;
}
</style>
```

## 4. 图片上传优化

### 4.1 图片压缩

在上传前对图片进行压缩，减少上传时间和存储空间：

```javascript
// 图片压缩函数
compressImage(file, maxWidth = 800, maxHeight = 800, quality = 0.7) {
  return new Promise((resolve) => {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();
    
    img.onload = function() {
      let width = img.width;
      let height = img.height;
      
      if (width > maxWidth) {
        height = height * (maxWidth / width);
        width = maxWidth;
      }
      
      if (height > maxHeight) {
        width = width * (maxHeight / height);
        height = maxHeight;
      }
      
      canvas.width = width;
      canvas.height = height;
      ctx.drawImage(img, 0, 0, width, height);
      
      canvas.toBlob(resolve, 'image/jpeg', quality);
    };
    
    img.src = URL.createObjectURL(file);
  });
}

// 使用压缩函数
async handleFileChange(e) {
  const file = e.target.files[0];
  if (file) {
    const compressedFile = await this.compressImage(file);
    this.uploadImage(compressedFile);
  }
}
```

### 4.2 上传进度显示

添加上传进度显示，提升用户体验：

```javascript
uploadImage(file) {
  const formData = new FormData();
  formData.append('orderId', this.orderId);
  formData.append('orderNo', this.orderNo);
  formData.append('imageType', this.imageType);
  formData.append('file', file);

  axios.post('/api/order/image/upload/cos', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      this.uploadProgress = percentCompleted;
    }
  })
  .then(response => {
    if (response.data.code === 200) {
      this.getImageList();
      this.uploadProgress = 0;
      alert('上传成功');
    } else {
      this.uploadProgress = 0;
      alert('上传失败：' + response.data.message);
    }
  })
  .catch(error => {
    this.uploadProgress = 0;
    alert('上传失败：' + error.message);
  });
}
```

## 5. 最佳实践

### 5.1 组件设计

1. **封装性**：将图片上传功能封装为独立组件，提高代码复用性
2. **可配置性**：通过props传递配置参数，提高组件的灵活性
3. **响应式**：适配不同屏幕尺寸，提供良好的移动端体验

### 5.2 用户体验

1. **反馈机制**：提供上传进度、成功/失败提示等反馈
2. **错误处理**：处理网络错误、文件类型错误等异常情况
3. **操作便捷性**：支持拖拽上传、批量上传等便捷操作

### 5.3 性能优化

1. **图片压缩**：上传前压缩图片，减少上传时间和存储空间
2. **懒加载**：图片列表使用懒加载，提高页面加载速度
3. **缓存策略**：缓存图片列表，减少重复请求

### 5.4 安全性

1. **文件验证**：验证文件类型和大小，防止恶意文件上传
2. **HTTPS**：使用HTTPS协议传输数据，确保数据安全
3. **权限控制**：确保只有授权用户才能上传和删除图片