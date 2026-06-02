<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>登录</h2>
      <el-form :model="form" @submit.prevent="submit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button>
      </el-form>

      <div class="quick-section">
        <p class="quick-title">快捷登录</p>
        <div class="quick-btns">
          <el-button v-for="acc in quickAccounts" :key="acc.username" size="small" @click="quickLogin(acc)">
            {{ acc.label }}
          </el-button>
        </div>
      </div>

      <p class="tip">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { QUICK_LOGIN_ACCOUNTS } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const quickAccounts = QUICK_LOGIN_ACCOUNTS

async function doLogin(username, password) {
  loading.value = true
  try {
    const res = await authApi.login({ username, password })
    userStore.setAuth(res.token, res.user)
    router.push(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}

function submit() {
  doLogin(form.username, form.password)
}

function quickLogin(acc) {
  form.username = acc.username
  form.password = acc.password
  doLogin(acc.username, acc.password)
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-card {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  width: 420px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
}
.auth-card h2 { margin: 0 0 24px; text-align: center; }
.quick-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px dashed var(--border);
}
.quick-title {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 12px;
  text-align: center;
}
.quick-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}
.tip { text-align: center; margin-top: 16px; font-size: 14px; color: var(--text-secondary); }
.tip a { color: var(--primary); }
</style>
