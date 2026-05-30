<template>
  <div class="admin-login">
    <el-card class="card">
      <h2>管理员登录</h2>
      <el-form @submit.prevent="submit">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" prefix-icon="User" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password /></el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button>
      </el-form>
      <p class="tip">默认账号 admin / admin123</p>
      <div class="quick-btns">
        <el-button size="small" @click="quickLogin">快捷登录管理员</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })

async function doLogin() {
  loading.value = true
  try {
    const res = await authApi.login(form)
    if (res.user.role !== 'admin') {
      ElMessage.error('非管理员账号')
      return
    }
    userStore.setAuth(res.token, res.user)
    router.push('/admin/dashboard')
  } finally {
    loading.value = false
  }
}

function submit() {
  doLogin()
}

function quickLogin() {
  form.username = 'admin'
  form.password = 'admin123'
  doLogin()
}
</script>

<style scoped>
.admin-login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #001529;
}
.card { width: 400px; padding: 20px; }
.card h2 { text-align: center; margin: 0 0 24px; }
.tip { text-align: center; font-size: 13px; color: var(--text-secondary); margin-top: 16px; }
.quick-btns { text-align: center; margin-top: 12px; }
</style>
