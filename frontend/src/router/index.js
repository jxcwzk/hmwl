import { createRouter, createWebHistory } from 'vue-router'

const getOrderPageByUserType = () => {
  const userType = localStorage.getItem('userType')
  switch (userType) {
    case '2':
      return () => import('../views/CustomerOrder.vue')
    case '3':
      return () => import('../views/DriverOrder.vue')
    case '4':
      return () => import('../views/NetworkOrder.vue')
    default:
      return () => import('../views/DispatcherOrder.vue')
  }
}

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: getOrderPageByUserType(),
    meta: { requiresAuth: true }
  },
  {
    path: '/route',
    name: 'Route',
    component: () => import('../views/Route.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/driver',
    name: 'Driver',
    component: () => import('../views/Driver.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/vehicle',
    name: 'Vehicle',
    component: () => import('../views/Vehicle.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/network-point',
    name: 'NetworkPoint',
    component: () => import('../views/NetworkPoint.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/settlement',
    name: 'Settlement',
    component: () => import('../views/Settlement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/invoice',
    name: 'Invoice',
    component: () => import('../views/Invoice.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/system',
    name: 'System',
    component: () => import('../views/System.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('../views/User.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/customer',
    name: 'Customer',
    component: () => import('../views/Customer.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'business-user',
        name: 'BusinessUser',
        component: () => import('../views/BusinessUser.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'business-customer',
        name: 'BusinessCustomer',
        component: () => import('../views/BusinessCustomer.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'CustomerProfile',
        component: () => import('../views/CustomerProfile.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/operation-statistics',
    name: 'OperationStatistics',
    component: () => import('../views/OperationStatistics.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order-assign',
    name: 'OrderAssign',
    component: () => import('../views/OrderAssign.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/dispatch',
    name: 'Dispatch',
    component: () => import('../views/Dispatch.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/network-confirm',
    name: 'NetworkConfirm',
    component: () => import('../views/NetworkConfirm.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/home')
  } else {
    next()
  }
})

export default router
