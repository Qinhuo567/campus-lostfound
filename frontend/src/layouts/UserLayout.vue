<template>
  <div class="user-layout">
    <header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">🔍</span>
          <span class="logo-text">校园失物招领</span>
        </router-link>

        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索物品、地点..."
            clearable
            @keyup.enter="doSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="doSearch" />
            </template>
          </el-input>
        </div>

        <nav class="nav-actions">
          <router-link to="/matches" class="nav-link">智能匹配</router-link>
          <el-dropdown v-if="userStore.isLoggedIn" trigger="click">
            <el-button type="primary" class="publish-btn">
              发布 <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/publish/lost')">发布寻物</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/publish/found')">发布招领</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button v-else type="primary" @click="$router.push('/login')">登录发布</el-button>

          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="user-avatar">
                <el-avatar :size="36">{{ userStore.user?.nickname?.[0] || 'U' }}</el-avatar>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/mine/posts')">我的发布</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/mine/claims')">我的认领</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/mine/favorites')">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" divided @click="$router.push('/admin')">管理后台</el-dropdown-item>
                  <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <el-button v-else link @click="$router.push('/login')">登录</el-button>
        </nav>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <p>校园失物招领与寻物系统 · 小组开发实践</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const keyword = ref(route.query.keyword || '')

watch(() => route.query.keyword, (v) => { keyword.value = v || '' })

function doSearch() {
  router.push({ name: 'home', query: { ...route.query, keyword: keyword.value || undefined } })
}

function logout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.logo-icon { font-size: 24px; }

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary);
}

.search-box {
  flex: 1;
  max-width: 480px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-link {
  color: var(--text-secondary);
  font-size: 14px;
}

.nav-link:hover { color: var(--primary); }

.publish-btn {
  background: var(--primary);
  border-color: var(--primary);
}

.user-avatar { cursor: pointer; }

.main { flex: 1; }

.footer {
  text-align: center;
  padding: 24px;
  color: var(--text-secondary);
  font-size: 13px;
  border-top: 1px solid var(--border);
  background: #fff;
}
</style>
