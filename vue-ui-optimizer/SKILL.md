---
name: vue-ui-optimizer
description: VUE前端界面优化专家技能。专门用于优化Vue.js前端应用的UI性能、用户体验和代码质量。适用于以下场景：(1) 优化Vue组件渲染性能，如解决卡顿、掉帧问题；(2) 优化大型列表虚拟滚动；(3) 优化首屏加载速度和Core Web Vitals指标；(4) 优化用户交互响应速度；(5) 优化Vue应用的整体用户体验和视觉表现；(6) 解决Vue项目中常见的性能瓶颈；(7) 代码重构和最佳实践改进；(8) 响应式设计和移动端适配优化。
---

# VUE前端界面优化专家

## 核心能力

- Vue 3 性能优化（Composition API、Script Setup）
- Vue 2 迁移优化
- 组件渲染优化
- 虚拟滚动实现
- 首屏加载优化（Code Splitting、Lazy Loading）
- Core Web Vitals 优化（LCP、FID、CLS）
- 响应式设计优化
- CSS/动画性能优化

## 优化策略

### 1. 渲染性能优化

- 使用 `v-memo`、`v-once` 减少不必要的重渲染
- 合理使用 `computed` 和 `watch`
- 组件懒加载
- Key 优化策略

### 2. 首屏加载优化

- 路由级别 Code Splitting
- 组件异步加载
- 资源压缩与合并
- CDN 优化
- 预加载关键资源

### 3. 列表性能优化

- 虚拟滚动（vue-virtual-scroller、vue-virtual-scroll-list）
- 分页加载
- 骨架屏优化

### 4. 交互响应优化

- 事件防抖/节流
- Web Worker 处理复杂计算
- requestAnimationFrame 优化动画

## 常用工具

- Vue DevTools 性能分析
- Chrome DevTools Performance/Lighthouse
- @vueuse/core 组合式工具库

## 注意事项

- 优先进行性能瓶颈分析再优化
- 平衡性能与代码可维护性
- 移动端优先的响应式设计
