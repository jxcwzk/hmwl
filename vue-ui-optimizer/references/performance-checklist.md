# Vue 性能优化检查清单

## 渲染性能

- [ ] 使用 `v-memo` 缓存静态子树的渲染结果
- [ ] 避免在模板中使用函数调用
- [ ] 使用 `v-once` 处理静态内容
- [ ] 合理设置组件 `key`
- [ ] 使用 `shallowRef` 处理大型对象
- [ ] 避免响应式对象结构破坏

## 首屏加载

- [ ] 路由级别 Code Splitting
- [ ] 第三方库按需加载
- [ ] 关键 CSS 内联
- [ ] 预加载关键资源 `preload`
- [ ] 预连接第三方域名 `preconnect`
- [ ] 图片懒加载
- [ ] 使用 `webpack-bundle-analyzer` 分析包体积

## 列表性能

- [ ] 虚拟滚动（超过 100 项）
- [ ] 列表分页
- [ ] 骨架屏
- [ ] 图片虚拟化

## 交互响应

- [ ] 事件防抖/节流
- [ ] 长任务使用 Web Worker
- [ ] 使用 `requestAnimationFrame`
- [ ] 避免同步 DOM 操作

## Core Web Vitals

### LCP (Largest Contentful Paint)

- 优化服务器响应时间
- 减少 CSS 阻塞
- 优化图片加载
- 预加载 Hero 图片

### FID (First Input Delay) / INP (Interaction to Next Paint)

- 减少 JavaScript 执行时间
- 拆分长任务
- 减少主线程阻塞

### CLS (Cumulative Layout Shift)

- 设置图片/iframe 尺寸
- 避免动态注入内容
- 使用字体加载优化
- 预留广告位空间

## Vue 特定优化

- 使用 `defineAsyncComponent` 异步组件
- 使用 `Suspense` 处理异步依赖
- 合理使用 `provide/inject`
- 避免 `v-for` 和 `v-if` 一起使用
- 使用 `keep-alive` 缓存组件
- 启用生产模式
