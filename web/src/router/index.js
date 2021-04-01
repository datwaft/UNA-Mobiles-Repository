import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/user/userinformation',
    name: 'User Information',
    component: () => import('@/views/user/UserInformation.vue'),
    
  },
  {
    path: '/about/history',
    name: 'History',
    component: () => import('@/views/about/History.vue')
  },
  {
    path: '/about/contact',
    name: 'Contact Information',
    component: () => import('@/views/about/Contact.vue')
  },
  {
    path: '/about/reference',
    name: 'Institutional Reference',
    component: () => import('@/views/about/Reference.vue')
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
