<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>注册</h2>
      <el-form :model="form" @submit.prevent="submit">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" /></el-form-item>
        <el-form-item><el-input v-model="form.studentNo" placeholder="学号（选填）" /></el-form-item>
        <el-form-item><el-input v-model="form.nickname" placeholder="昵称" /></el-form-item>
        <el-form-item><el-input v-model="form.phone" placeholder="手机号（选填）" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" show-password /></el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">注册</el-button>
      </el-form>
      <p class="tip">已有账号？<router-link to="/login">去登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', studentNo: '', nickname: '', phone: '', password: '' })

async function submit() {
  loading.value = true
  try {
    const res = await authApi.register(form)
    userStore.setAuth(res.token, res.user)
    router.push('/')
  } finally {
    loading.value = false
  }
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
  width: 400px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
}
.auth-card h2 { margin: 0 0 24px; text-align: center; }
.tip { text-align: center; margin-top: 16px; font-size: 14px; color: var(--text-secondary); }
.tip a { color: var(--primary); }
</style>
