<template>
  <el-card v-loading="loading">
    <el-table :data="posts">
      <el-table-column prop="title" label="标题" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">{{ TYPE_LABELS[row.type] }}</template>
      </el-table-column>
      <el-table-column prop="category" label="类别" width="100" />
      <el-table-column prop="location" label="地点" />
      <el-table-column prop="publisherName" label="发布者" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="approve(row.id)">通过</el-button>
          <el-button size="small" type="danger" @click="reject(row.id)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="!posts.length && !loading" class="empty-state">暂无待审核帖子</div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api'
import { TYPE_LABELS } from '@/utils/constants'

const loading = ref(false)
const posts = ref([])

async function load() {
  loading.value = true
  posts.value = await adminApi.pending()
  loading.value = false
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

onMounted(load)
</script>
