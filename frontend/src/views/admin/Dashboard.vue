<template>
  <div v-loading="loading">
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in cards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
          <el-button type="primary" @click="runMatch" :loading="matching">运行智能匹配</el-button>
        </div>
      </template>
      <p>管理员可在此触发全量匹配，系统将寻物帖与招领帖进行智能配对。</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api'

const loading = ref(false)
const matching = ref(false)
const stats = ref({})

const cards = computed(() => [
  { label: '用户总数', value: stats.value.userCount ?? '-' },
  { label: '帖子总数', value: stats.value.postCount ?? '-' },
  { label: '待审核', value: stats.value.pendingCount ?? '-' },
  { label: '已确认匹配', value: stats.value.matchCount ?? '-' }
])

async function load() {
  loading.value = true
  stats.value = await adminApi.stats()
  loading.value = false
}

async function runMatch() {
  matching.value = true
  try {
    const res = await adminApi.runMatch()
    ElMessage.success(`匹配完成，共 ${res.matched} 条`)
    load()
  } finally {
    matching.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 32px; font-weight: 700; color: var(--primary); }
.stat-label { color: var(--text-secondary); margin-top: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
