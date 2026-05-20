<template>
  <div class="page-container">
    <h2 class="section-title">我的发布</h2>
    <el-table :data="posts" v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">{{ TYPE_LABELS[row.type] }}</template>
      </el-table-column>
      <el-table-column label="审核" width="100">
        <template #default="{ row }">
          <el-tag :type="MOD_STATUS[row.modStatus]?.type" size="small">{{ MOD_STATUS[row.modStatus]?.label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="POST_STATUS[row.postStatus]?.type" size="small">{{ POST_STATUS[row.postStatus]?.label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/posts/${row.id}`)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { postApi } from '@/api'
import { TYPE_LABELS, MOD_STATUS, POST_STATUS } from '@/utils/constants'

const loading = ref(false)
const posts = ref([])

onMounted(async () => {
  loading.value = true
  posts.value = await postApi.myPosts()
  loading.value = false
})
</script>
