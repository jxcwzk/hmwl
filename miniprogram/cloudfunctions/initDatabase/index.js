// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

// 云函数入口函数
exports.main = async (event, context) => {
  try {
    // 初始化订单集合
    await initOrderCollection()
    
    // 初始化司机集合
    await initDriverCollection()
    
    // 初始化车辆集合
    await initVehicleCollection()
    
    // 初始化网点集合
    await initNetworkPointCollection()
    
    // 初始化结算集合
    await initSettlementCollection()
    
    return {
      success: true,
      message: '云数据库初始化成功'
    }
  } catch (error) {
    console.error('云数据库初始化失败:', error)
    return {
      success: false,
      error: error.message
    }
  }
}

// 初始化订单集合
async function initOrderCollection() {
  try {
    const orderCollection = db.collection('order')
    
    // 检查集合是否存在
    const count = await orderCollection.count()
    
    if (count.total === 0) {
      // 插入测试数据
      await orderCollection.add({
        data: [
          {
            orderNo: 'HM20260307001',
            orderType: 0,
            startNetworkId: 1,
            endNetworkId: 2,
            senderName: '张三',
            senderPhone: '13800138001',
            senderAddress: '北京市朝阳区',
            receiverName: '李四',
            receiverPhone: '13900139001',
            receiverAddress: '上海市浦东新区',
            goodsName: '电子产品',
            quantity: 10,
            weight: 50,
            volume: 2,
            totalFee: 1000,
            paymentMethod: 0,
            status: 0,
            createTime: new Date(),
            updateTime: new Date()
          },
          {
            orderNo: 'HM20260307002',
            orderType: 1,
            startNetworkId: 2,
            endNetworkId: 3,
            senderName: '王五',
            senderPhone: '13700137001',
            senderAddress: '上海市浦东新区',
            receiverName: '赵六',
            receiverPhone: '13600136001',
            receiverAddress: '广州市天河区',
            goodsName: '家具',
            quantity: 5,
            weight: 200,
            volume: 10,
            totalFee: 5000,
            paymentMethod: 1,
            status: 1,
            createTime: new Date(),
            updateTime: new Date()
          }
        ]
      })
    }
  } catch (error) {
    console.error('初始化订单集合失败:', error)
    throw error
  }
}

// 初始化司机集合
async function initDriverCollection() {
  try {
    const driverCollection = db.collection('driver')
    
    const count = await driverCollection.count()
    
    if (count.total === 0) {
      await driverCollection.add({
        data: [
          {
            name: '张三',
            phone: '13800138001',
            idCard: '110101199001011234',
            vehicleId: 1,
            createTime: new Date(),
            updateTime: new Date()
          },
          {
            name: '李四',
            phone: '13900139001',
            idCard: '110101199002022345',
            vehicleId: 2,
            createTime: new Date(),
            updateTime: new Date()
          }
        ]
      })
    }
  } catch (error) {
    console.error('初始化司机集合失败:', error)
    throw error
  }
}

// 初始化车辆集合
async function initVehicleCollection() {
  try {
    const vehicleCollection = db.collection('vehicle')
    
    const count = await vehicleCollection.count()
    
    if (count.total === 0) {
      await vehicleCollection.add({
        data: [
          {
            licensePlate: '京A12345',
            vehicleType: '重型卡车',
            loadCapacity: 20,
            createTime: new Date(),
            updateTime: new Date()
          },
          {
            licensePlate: '京B67890',
            vehicleType: '中型卡车',
            loadCapacity: 10,
            createTime: new Date(),
            updateTime: new Date()
          }
        ]
      })
    }
  } catch (error) {
    console.error('初始化车辆集合失败:', error)
    throw error
  }
}

// 初始化网点集合
async function initNetworkPointCollection() {
  try {
    const networkPointCollection = db.collection('network_point')
    
    const count = await networkPointCollection.count()
    
    if (count.total === 0) {
      await networkPointCollection.add({
        data: [
          {
            code: 'HM001',
            name: '北京总部',
            contactPerson: '王经理',
            phone: '13800138000',
            address: '北京市朝阳区建国路88号',
            createTime: new Date(),
            updateTime: new Date()
          },
          {
            code: 'HM002',
            name: '上海分公司',
            contactPerson: '李经理',
            phone: '13900139000',
            address: '上海市浦东新区陆家嘴金融中心',
            createTime: new Date(),
            updateTime: new Date()
          },
          {
            code: 'HM003',
            name: '广州分公司',
            contactPerson: '张经理',
            phone: '13700137000',
            address: '广州市天河区天河路385号',
            createTime: new Date(),
            updateTime: new Date()
          }
        ]
      })
    }
  } catch (error) {
    console.error('初始化网点集合失败:', error)
    throw error
  }
}

// 初始化结算集合
async function initSettlementCollection() {
  try {
    const settlementCollection = db.collection('settlement')
    
    const count = await settlementCollection.count()
    
    if (count.total === 0) {
      await settlementCollection.add({
        data: [
          {
            settlementNo: 'JS20260307001',
            type: 0,
            orderId: 1,
            customerId: 1,
            driverId: 1,
            startNetworkId: 1,
            endNetworkId: 2,
            amount: 1000,
            status: 0,
            paymentMethod: 0,
            commission: 100,
            transferFee: 50,
            trunkFee: 850,
            createTime: new Date(),
            updateTime: new Date()
          },
          {
            settlementNo: 'JS20260307002',
            type: 1,
            orderId: 2,
            customerId: 2,
            driverId: 2,
            startNetworkId: 2,
            endNetworkId: 3,
            amount: 5000,
            status: 1,
            paymentMethod: 1,
            commission: 500,
            transferFee: 200,
            trunkFee: 4300,
            createTime: new Date(),
            updateTime: new Date()
          }
        ]
      })
    }
  } catch (error) {
    console.error('初始化结算集合失败:', error)
    throw error
  }
}
