import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('../views/Order.vue')
  },
  {
    path: '/route',
    name: 'Route',
    component: () => import('../views/Route.vue')
  },
  {
    path: '/driver',
    name: 'Driver',
    component: () => import('../views/Driver.vue')
  },
  {
    path: '/vehicle',
    name: 'Vehicle',
    component: () => import('../views/Vehicle.vue')
  },
  {
    path: '/network-point',
    name: 'NetworkPoint',
    component: () => import('../views/NetworkPoint.vue')
  },
  {
    path: '/settlement',
    name: 'Settlement',
    component: () => import('../views/Settlement.vue')
  },
  {
    path: '/invoice',
    name: 'Invoice',
    component: () => import('../views/Invoice.vue')
  },
  {
    path: '/system',
    name: 'System',
    component: () => import('../views/System.vue')
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('../views/User.vue')
  },
  {
    path: '/customer',
    name: 'Customer',
    component: () => import('../views/Customer.vue'),
    children: [
      {
        path: 'business-user',
        name: 'BusinessUser',
        component: () => import('../views/BusinessUser.vue')
      },
      {
        path: 'business-customer',
        name: 'BusinessCustomer',
        component: () => import('../views/BusinessCustomer.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
