<template>
  <div class="page-container">
    <h2 class="section-title">智能匹配</h2>
    <p class="subtitle">系统根据类别、地点、描述和时间自动匹配寻物与招领信息</p>

    <div v-loading="loading">
      <div v-for="m in matches" :key="m.id" class="match-card">
        <div class="match-header">
          <span class="score-badge">{{ m.score }} 分</span>
          <el-tag size="small">{{ m.reason || '智能推荐' }}</el-tag>
          <el-tag :type="statusType(m.status)" size="small">{{ statusLabel(m.status) }}</el-tag>
        </div>
        <div class="match-body">
          <div class="side lost">
            <div class="side-label">寻物</div>
            <h4>{{ m.lostPost?.title }}</h4>
            <p>{{ m.lostPost?.location }}</p>
            <router-link :to="`/posts/${m.lostPost?.id}`">查看详情</router-link>
          </div>
          <div class="arrow">↔</div>
          <div class="side found">
            <div class="side-label">招领</div>
            <h4>{{ m.foundPost?.title }}</h4>
            <p>{{ m.foundPost?.location }}</p>
            <router-link :to="`/posts/${m.foundPost?.id}`">查看详情</router-link>
          </div>
        </div>
        <div class="match-actions" v-if="m.status === 'suggested'">
          <el-button size="small" type="primary" @click="accept(m.id)">确认匹配</el-button>
          <el-button size="small" @click="dismiss(m.id)">忽略</el-button>
        </div>
      </div>
      <div v-if="!matches.length && !loading" class="empty-state">暂无匹配推荐，发布更多信息后将自动匹配</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { matchApi } from '@/api'

const loading = ref(false)
const matches = ref([])

function statusLabel(s) {
  return { suggested: '待确认', accepted: '已确认', dismissed: '已忽略' }[s] || s
}
function statusType(s) {
  return { suggested: 'warning', accepted: 'success', dismissed: 'info' }[s] || 'info'
}

async function load() {
  loading.value = true
  matches.value = await matchApi.my()
  loading.value = false
}

async function accept(id) {
  await matchApi.accept(id)
  ElMessage.success('已确认匹配')
  load()
}

async function dismiss(id) {
  await matchApi.dismiss(id)
  load()
}

onMounted(load)
</script>

<style scoped>
.subtitle { color: var(--text-secondary); margin: -8px 0 20px; }
.match-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid var(--border);
}
.match-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.score-badge {
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 700;
  font-size: 20px;
  padding: 4px 12px;
  border-radius: 8px;
}
.match-body { display: flex; align-items: stretch; gap: 16px; }
.side { flex: 1; padding: 16px; border-radius: 8px; }
.side.lost { background: #ecf5ff; }
.side.found { background: #fdf6ec; }
.side-label { font-size: 12px; color: var(--text-secondary); margin-bottom: 8px; }
.side h4 { margin: 0 0 8px; }
.side p { margin: 0 0 8px; font-size: 13px; color: var(--text-secondary); }
.arrow { display: flex; align-items: center; font-size: 24px; color: var(--primary); }
.match-actions { margin-top: 16px; }
</style>
