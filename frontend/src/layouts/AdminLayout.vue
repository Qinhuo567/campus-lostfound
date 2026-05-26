<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="brand">管理后台</div>
      <el-menu :default-active="route.path" router background-color="#001529" text-color="#fff" active-text-color="#ff6a00">
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/review">
          <el-icon><DocumentChecked /></el-icon>
          <span>待审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/posts">
          <el-icon><List /></el-icon>
          <span>帖子管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ pageTitle }}</span>
        <div>
          <el-button link @click="$router.push('/')">返回用户端</el-button>
          <el-button link type="danger" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataAnalysis, DocumentChecked, List, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const titles = {
  '/admin/dashboard': '数据概览',
  '/admin/review': '待审核',
  '/admin/posts': '帖子管理',
  '/admin/users': '用户管理'
}

const pageTitle = computed(() => titles[route.path] || '管理后台')

function logout() {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; }
.aside { background: #001529; }
.brand {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #002140;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border);
  font-weight: 600;
}
.main { background: var(--bg); }
</style>
