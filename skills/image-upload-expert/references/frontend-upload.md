# 前端上传组件配置文档

## 组件配置

前端上传组件使用 Element Plus 的 `el-upload` 组件，配置如下：

```vue
<el-upload
  class="upload-demo"
  :auto-upload="false"
  :on-change="handleFileChange"
  :limit="5"
  :file-list="fileList"
>
  <el-button type="primary">选择文件</el-button>
  <template #tip>
    <div class="el-upload__tip">
      只能上传jpg/png文件，且不超过10MB
    </div>
  </template>
  <template #footer>
    <el-button type="primary" @click="handleManualUpload" :disabled="fileList.length === 0">
      开始上传
    </el-button>
  </template>
</el-upload>
```

## 上传逻辑

1. **文件选择**：用户点击 "选择文件" 按钮，选择要上传的图片
2. **文件校验**：校验文件类型是否为jpg或png，文件大小是否不超过10MB
3. **手动上传**：用户点击 "开始上传" 按钮，触发上传逻辑
4. **上传请求**：使用 fetch API 发送上传请求，包含订单ID、订单编号、图片类型和文件数据
5. **上传结果**：显示上传成功或失败的提示
6. **刷新图片列表**：上传完成后，刷新已上传图片列表

## 关键代码

### 上传方法

```javascript
const handleManualUpload = async () => {
  if (!form.value.id || !form.value.orderNo) {
    ElMessage.warning('请先保存订单信息')
    return
  }
  
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  
  try {
    for (const file of fileList.value) {
      const formData = new FormData()
      formData.append('orderId', form.value.id)
      formData.append('orderNo', form.value.orderNo)
      formData.append('imageType', 1)
      formData.append('file', file.raw)
      
      const response = await fetch(uploadUrl.value, {
        method: 'POST',
        body: formData
      })
      
      if (response.ok) {
        ElMessage.success('上传成功')
      } else {
        ElMessage.error('上传失败: ' + response.statusText)
      }
    }
    
    // 刷新图片列表
    await getOrderImageList(form.value.id)
    // 上传完成后清空文件列表
    fileList.value = []
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请稍后重试')
  }
}
```

### 获取图片列表方法

```javascript
const getOrderImageList = async (orderId) => {
  if (!orderId) {
    imageList.value = []
    return
  }
  try {
    const response = await axios.get('/api/order/image/list', {
      params: { orderId }
    })
    imageList.value = response.data
  } catch (error) {
    console.error('获取图片列表失败:', error)
    ElMessage.error('获取图片列表失败，请稍后重试')
  }
}
```

## 配置项

| 配置项 | 说明 | 默认值 |
| --- | --- | --- |
| `uploadUrl` | 上传API地址 | `/api/order/image/upload/cos` |
| `fileList` | 待上传文件列表 | `[]` |
| `imageList` | 已上传图片列表 | `[]` |
| `limit` | 最大上传文件数量 | `5` |
| `auto-upload` | 是否自动上传 | `false` |

## 注意事项

1. **文件类型**：只支持jpg和png格式的图片
2. **文件大小**：图片大小不能超过10MB
3. **订单信息**：上传前必须先保存订单，获取订单ID和订单编号
4. **网络连接**：上传需要网络连接
5. **错误处理**：上传失败时会显示错误提示
