<template>
  <component :is="orderComponent" />
</template>

<script setup>
import { computed } from 'vue'
import CustomerOrder from './CustomerOrder.vue'
import DispatcherOrder from './DispatcherOrder.vue'
import DriverOrder from './DriverOrder.vue'
import NetworkOrder from './NetworkOrder.vue'

const orderComponent = computed(() => {
  const userType = localStorage.getItem('userType')
  switch (userType) {
    case '2':
      return CustomerOrder
    case '3':
      return DriverOrder
    case '4':
      return NetworkOrder
    default:
      return DispatcherOrder
  }
})
</script>
