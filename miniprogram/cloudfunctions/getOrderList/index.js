// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

// 云函数入口函数
exports.main = async (event, context) => {
  console.log('getOrderList云函数被调用:', event)
  
  try {
    // 参数验证
    const { page = 1, size = 10, orderNo } = event
    
    // 验证参数类型
    if (typeof page !== 'number' || page < 1) {
      return {
        success: false,
        error: 'page参数必须是大于0的数字'
      }
    }
    
    if (typeof size !== 'number' || size < 1 || size > 100) {
      return {
        success: false,
        error: 'size参数必须是1-100之间的数字'
      }
    }
    
    let query = db.collection('order')
    
    if (orderNo && typeof orderNo === 'string') {
      query = query.where({
        orderNo: db.RegExp({
          regexp: orderNo,
          options: 'i'
        })
      })
    }
    
    // 执行查询
    const total = await query.count()
    const records = await query
      .skip((page - 1) * size)
      .limit(size)
      .orderBy('createTime', 'desc')
      .get()
    
    console.log('查询成功，返回记录数:', records.data.length)
    
    return {
      success: true,
      data: {
        records: records.data,
        total: total.total
      }
    }
  } catch (error) {
    console.error('查询订单失败:', error)
    return {
      success: false,
      error: error.message || '查询订单失败'
    }
  }
}
