# Vue 性能优化代码示例

## 1. 路由懒加载

```javascript
// router/index.js
const routes = [
  {
    path: '/about',
    component: () => import('@/views/About.vue')
  },
  {
    path: '/dashboard',
    component: () => import(/* webpackChunkName: "dashboard" */ '@/views/Dashboard.vue')
  }
]
```

## 2. 组件懒加载

```vue
<script setup>
import { defineAsyncComponent } from 'vue'

const HeavyChart = defineAsyncComponent(() =>
  import('./HeavyChart.vue')
)
</script>

<template>
  <HeavyChart v-if="showChart" />
</template>
```

## 3. v-memo 优化

```vue
<template>
  <div v-for="item in list" :key="item.id" v-memo="[item.selected]">
    <ComplexComponent :item="item" />
  </div>
</template>
```

## 4. 大型列表虚拟滚动

```vue
<script setup>
import { VirtualScroller } from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'
</script>

<template>
  <VirtualScroller :items="items" :item-size="50">
    <template #default="{ item, index, active }">
      <ListItem :item="item" :active="active" />
    </template>
  </VirtualScroller>
</template>
```

## 5. 防抖/节流

```javascript
import { useDebounceFn, useThrottleFn } from '@vueuse/core'

const debouncedSearch = useDebounceFn(() => {
  performSearch()
}, 300)

const throttledScroll = useThrottleFn(() => {
  handleScroll()
}, 100)
```

## 6. Web Worker

```javascript
// worker.js
self.onmessage = (e) => {
  const result = heavyComputation(e.data)
  self.postMessage(result)
}

// Component
import { ref } from 'vue'

const worker = new Worker(new URL('./worker.js', import.meta.url))

worker.onmessage = (e) => {
  console.log('Result:', e.data)
}

worker.postMessage(data)
```

## 7. shalowRef 优化

```javascript
import { shallowRef, triggerRef } from 'vue'

const largeData = shallowRef({ huge: 'object' })

function updateData() {
  largeData.value.huge = 'newValue'
  triggerRef(largeData)
}
```

## 8. Async Component with Suspense

```vue
<script setup>
import { defineAsyncComponent } from 'vue'

const AsyncComp = defineAsyncComponent({
  loader: () => import('./HeavyComponent.vue'),
  loadingComponent: LoadingSpinner,
  delay: 200
})
</script>

<template>
  <Suspense>
    <AsyncComp />
    <template #fallback>
      <LoadingSpinner />
    </template>
  </Suspense>
</template>
```

## 9. keep-alive 缓存

```vue
<template>
  <keep-alive :include="['UserList', 'ProductGrid']">
    <router-view />
  </keep-alive>
</template>
```

## 10. 图片懒加载

```vue
<script setup>
import { useIntersectionObserver } from '@vueuse/core'

const imgRef = ref()
const isVisible = useIntersectionObserver(imgRef, ([{ isIntersecting }]) => {
  if (isIntersecting) loadImage()
})
</script>

<template>
  <img ref="imgRef" :src="isVisible ? actualSrc : placeholder" />
</template>
```
