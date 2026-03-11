// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

// 云函数入口函数
exports.main = async (event, context) => {
  try {
    const { page = 1, size = 10, name } = event
    
    let query = db.collection('network_point')
    
    if (name) {
      query = query.where({
        name: db.RegExp({
          regexp: name,
          options: 'i'
        })
      })
    }
    
    const total = await query.count()
    const records = await query
      .skip((page - 1) * size)
      .limit(size)
      .orderBy('createTime', 'desc')
      .get()
    
    return {
      success: true,
      data: {
        records: records.data,
        total: total.total
      }
    }
  } catch (error) {
    return {
      success: false,
      error: error.message
    }
  }
}
