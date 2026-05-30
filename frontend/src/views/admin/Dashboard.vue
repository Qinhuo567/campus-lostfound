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

    <el-row :gutter="16" style="margin-top:20px">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待审核帖子</span>
              <el-button link type="primary" @click="$router.push('/admin/review')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingPosts" size="small">
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column label="类型" width="70">
              <template #default="{ row }">{{ row.type === 'lost' ? '寻物' : '招领' }}</template>
            </el-table-column>
            <el-table-column prop="publisherName" label="发布者" width="90" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" type="success" link @click="approve(row.id)">通过</el-button>
                <el-button size="small" type="danger" link @click="reject(row.id)">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="!pendingPosts.length" class="empty">暂无待审核</div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <el-space direction="vertical" fill style="width:100%">
            <el-button type="primary" @click="runMatch" :loading="matching" style="width:100%">
              运行智能匹配
            </el-button>
            <el-button @click="resetSeed" :loading="seeding" style="width:100%">
              重置示例数据
            </el-button>
          </el-space>
          <el-divider />
          <div class="guide">
            <p><strong>操作指引：</strong></p>
            <ol>
              <li>在「待审核」中通过新帖子</li>
              <li>运行智能匹配生成推荐</li>
              <li>用户端查看匹配并发起认领</li>
              <li>发布者在详情页审核认领</li>
            </ol>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi, seedApi } from '@/api'

const loading = ref(false)
const matching = ref(false)
const seeding = ref(false)
const stats = ref({})
const pendingPosts = ref([])

const cards = computed(() => [
  { label: '用户总数', value: stats.value.userCount ?? '-' },
  { label: '帖子总数', value: stats.value.postCount ?? '-' },
  { label: '待审核', value: stats.value.pendingCount ?? '-' },
  { label: '智能匹配', value: stats.value.suggestedMatchCount ?? stats.value.matchCount ?? '-' }
])

async function load() {
  loading.value = true
  try {
    stats.value = await adminApi.stats()
    pendingPosts.value = (await adminApi.pending()).slice(0, 5)
  } finally {
    loading.value = false
  }
}

async function approve(id) {
  await adminApi.approve(id)
  ElMessage.success('已通过')
  load()
}

async function reject(id) {
  await adminApi.reject(id)
  ElMessage.success('已拒绝')
  load()
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

async function resetSeed() {
  await ElMessageBox.confirm('将清空帖子并重新生成示例数据', '重置数据', { type: 'warning' })
  seeding.value = true
  try {
    const res = await seedApi.seed(true)
    ElMessage.success(`已重置：${res.posts} 条帖子，${res.matched} 条匹配`)
    load()
  } finally {
    seeding.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 32px; font-weight: 700; color: var(--primary); }
.stat-label { color: var(--text-secondary); margin-top: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.empty { text-align: center; color: var(--text-secondary); padding: 20px; }
.guide { font-size: 13px; color: var(--text-secondary); line-height: 1.8; }
.guide ol { padding-left: 18px; margin: 8px 0 0; }
</style>
