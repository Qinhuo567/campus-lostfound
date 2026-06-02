<template>
  <div>
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-text">
          <h1>校园失物招领平台</h1>
          <p>寻物、招领、智能匹配，让遗失物品更快回家</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="$router.push('/publish/found')">发布招领</el-button>
            <el-button size="large" @click="$router.push('/publish/lost')">发布寻物</el-button>
            <el-button size="large" plain @click="$router.push('/matches')">查看智能匹配</el-button>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item" v-for="s in statItems" :key="s.label">
            <div class="stat-num">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <div class="page-container">
      <div class="category-bar">
        <span
          v-for="c in categoryOptions"
          :key="c.value"
          class="category-chip"
          :class="{ active: filters.category === c.value }"
          @click="selectCategory(c.value)"
        >{{ c.label }}</span>
      </div>

      <div class="filter-bar">
        <el-radio-group v-model="filters.type" @change="load">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="lost">寻物</el-radio-button>
          <el-radio-button label="found">招领</el-radio-button>
        </el-radio-group>
        <el-select v-model="filters.postStatus" placeholder="状态" clearable style="width:120px" @change="load">
          <el-option label="进行中" value="open" />
          <el-option label="认领中" value="pending_claim" />
          <el-option label="已关闭" value="closed" />
        </el-select>
        <div class="filter-right">
          <span class="result-count">共 {{ posts.length }} 条</span>
          <el-button v-if="userStore.isAdmin" size="small" @click="seedData(false)">导入示例数据</el-button>
          <el-button v-if="userStore.isAdmin" size="small" type="warning" @click="seedData(true)">重置数据</el-button>
        </div>
      </div>

      <div v-loading="loading">
        <div v-if="posts.length" class="card-grid">
          <PostCard v-for="p in posts" :key="p.id" :post="p" />
        </div>
        <div v-else class="empty-state">
          <p>暂无符合条件的帖子</p>
          <el-button type="primary" @click="$router.push('/publish/found')">去发布</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import { postApi, seedApi, statsApi } from '@/api'
import { CATEGORIES } from '@/utils/constants'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const posts = ref([])
const stats = ref({})
const filters = reactive({
  type: 'all',
  category: '',
  postStatus: '',
  keyword: route.query.keyword || ''
})

const categoryOptions = computed(() => [
  { label: '全部', value: '' },
  ...CATEGORIES.map(c => ({ label: c, value: c }))
])

const statItems = computed(() => [
  { label: '寻物信息', value: stats.value.lostCount ?? 0 },
  { label: '招领信息', value: stats.value.foundCount ?? 0 },
  { label: '智能匹配', value: stats.value.matchCount ?? 0 },
  { label: '已成功认领', value: stats.value.claimedCount ?? 0 }
])

watch(() => route.query.keyword, (v) => {
  filters.keyword = v || ''
  load()
})

function selectCategory(val) {
  filters.category = filters.category === val ? '' : val
  load()
}

async function loadStats() {
  stats.value = await statsApi.public()
}

async function load() {
  loading.value = true
  try {
    const params = { keyword: filters.keyword || undefined }
    if (filters.type !== 'all') params.type = filters.type
    if (filters.category) params.category = filters.category
    if (filters.postStatus) params.postStatus = filters.postStatus
    posts.value = await postApi.list(params)
  } finally {
    loading.value = false
  }
}

async function seedData(force) {
  if (force) {
    await ElMessageBox.confirm('将清空现有帖子并重新生成示例数据，确定继续？', '重置数据', { type: 'warning' })
  }
  const res = await seedApi.seed(force)
  ElMessage.success(res.skipped ? res.message : `数据已就绪：${res.posts} 条帖子，${res.matched} 条匹配`)
  loadStats()
  load()
}

onMounted(() => {
  loadStats()
  load()
})
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #ff6a00 0%, #ff9248 100%);
  color: #fff;
  padding: 36px 20px;
  margin-bottom: 8px;
}

.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
}

.hero-text h1 {
  margin: 0 0 8px;
  font-size: 28px;
}

.hero-text p {
  margin: 0 0 20px;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  min-width: 280px;
}

.stat-item {
  background: rgba(255,255,255,0.15);
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  opacity: 0.85;
  margin-top: 4px;
}

.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.category-chip {
  padding: 6px 16px;
  border-radius: 20px;
  background: #fff;
  border: 1px solid var(--border);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.category-chip:hover,
.category-chip.active {
  background: var(--primary-light);
  border-color: var(--primary);
  color: var(--primary);
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.filter-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-count {
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
