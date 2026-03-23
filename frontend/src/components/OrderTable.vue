<template>
  <div class="table-container">
    <el-table v-loading="loading" :data="orders" row-key="id">
      <el-table-column prop="orderNo" label="订单编号" min-width="150" />
      <el-table-column prop="senderName" label="发件人" min-width="100" />
      <el-table-column prop="receiverName" label="收件人" min-width="100" />
      <el-table-column prop="goodsName" label="货物" min-width="80" />
      <el-table-column prop="weight" label="重量" width="80" />
      <el-table-column prop="totalFee" label="费用" width="100">
        <template #default="scope">
          ¥{{ scope.row.totalFee || 0 }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.status === 0" type="primary" size="small" @click="$emit('action', { type: 'dispatch', order: scope.row })">
            派发比价
          </el-button>
          <el-button v-if="scope.row.pricingStatus === 1" type="warning" size="small" @click="$emit('action', { type: 'selectQuote', order: scope.row })">
            选择报价
          </el-button>
          <el-button v-if="scope.row.status === 5" type="primary" size="small" @click="$emit('action', { type: 'assignPickup', order: scope.row })">
            安排提货
          </el-button>
          <el-button v-if="scope.row.status === 9" type="primary" size="small" @click="$emit('action', { type: 'assignDelivery', order: scope.row })">
            分配配送
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
defineProps({
  orders: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['action'])

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 4: 'warning', 5: 'success', 6: 'info', 9: 'success', 11: 'primary' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待派发',
    1: '已派发',
    4: '待确认价格',
    5: '价格已确认',
    6: '待提货',
    7: '已提货',
    8: '送达网点',
    9: '待配送',
    11: '配送中',
    12: '派送中',
    13: '已签收'
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.table-container {
  padding: var(--spacing-md);
}
</style>
