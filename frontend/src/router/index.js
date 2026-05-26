import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/user/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/user/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/user/Home.vue') },
      { path: 'posts/:id', name: 'postDetail', component: () => import('@/views/user/PostDetail.vue') },
      { path: 'publish/lost', name: 'publishLost', component: () => import('@/views/user/Publish.vue'), props: { postType: 'lost' } },
      { path: 'publish/found', name: 'publishFound', component: () => import('@/views/user/Publish.vue'), props: { postType: 'found' } },
      { path: 'mine/posts', name: 'myPosts', component: () => import('@/views/user/MyPosts.vue') },
      { path: 'mine/claims', name: 'myClaims', component: () => import('@/views/user/MyClaims.vue') },
      { path: 'mine/favorites', name: 'myFavorites', component: () => import('@/views/user/MyFavorites.vue') },
      { path: 'matches', name: 'matches', component: () => import('@/views/user/Matches.vue') }
    ]
  },
  {
    path: '/admin/login',
    name: 'adminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { admin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'adminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'review', name: 'adminReview', component: () => import('@/views/admin/Review.vue') },
      { path: 'posts', name: 'adminPosts', component: () => import('@/views/admin/Posts.vue') },
      { path: 'users', name: 'adminUsers', component: () => import('@/views/admin/Users.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const store = useUserStore()
  if (to.meta.public) return true

  if (to.meta.admin) {
    if (!store.isLoggedIn) return { name: 'adminLogin' }
    if (!store.isAdmin) return { name: 'home' }
    return true
  }

  const needAuth = ['publishLost', 'publishFound', 'myPosts', 'myClaims', 'myFavorites', 'matches'].includes(to.name)
  if (needAuth && !store.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
