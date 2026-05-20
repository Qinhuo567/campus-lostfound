<template>
  <div class="page-container">
    <div class="filter-bar">
      <el-radio-group v-model="filters.type" @change="load">
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="lost">寻物</el-radio-button>
        <el-radio-button label="found">招领</el-radio-button>
      </el-radio-group>
      <el-select v-model="filters.category" placeholder="类别" clearable style="width:140px" @change="load">
        <el-option v-for="c in CATEGORIES" :key="c" :label="c" :value="c" />
      </el-select>
      <el-select v-model="filters.postStatus" placeholder="状态" clearable style="width:120px" @change="load">
        <el-option label="进行中" value="open" />
        <el-option label="认领中" value="pending_claim" />
        <el-option label="已关闭" value="closed" />
      </el-select>
      <el-button v-if="userStore.isAdmin" @click="seedData">生成演示数据</el-button>
    </div>

    <div v-loading="loading">
      <div v-if="posts.length" class="card-grid">
        <PostCard v-for="p in posts" :key="p.id" :post="p" />
      </div>
      <div v-else class="empty-state">暂无帖子，快来发布第一条吧</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import { postApi, seedApi } from '@/api'
import { CATEGORIES } from '@/utils/constants'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const posts = ref([])
const filters = reactive({
  type: 'all',
  category: '',
  postStatus: '',
  keyword: route.query.keyword || ''
})

watch(() => route.query.keyword, (v) => {
  filters.keyword = v || ''
  load()
})

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

async function seedData() {
  const res = await seedApi.seed(false)
  ElMessage.success(res.skipped ? res.message : `已生成演示数据，匹配 ${res.matched} 条`)
  load()
}

onMounted(load)
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}
</style>
