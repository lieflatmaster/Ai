import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/views/Chat.vue'),
    meta: { title: 'AI对话' }
  },
  {
    path: '/tickets',
    name: 'TicketList',
    component: () => import('@/views/TicketList.vue'),
    meta: { title: '工单列表' }
  },
  {
    path: '/tickets/create',
    name: 'TicketCreate',
    component: () => import('@/views/TicketCreate.vue'),
    meta: { title: '创建工单' }
  },
  {
    path: '/tickets/:id',
    name: 'TicketDetail',
    component: () => import('@/views/TicketDetail.vue'),
    meta: { title: '工单详情' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || '首页'} - 智能客服工单系统`
  next()
})

export default router
