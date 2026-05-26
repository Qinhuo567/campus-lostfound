<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="filters.type" placeholder="类型" clearable style="width:120px" @change="load">
        <el-option label="寻物" value="lost" />
        <el-option label="招领" value="found" />
      </el-select>
      <el-select v-model="filters.modStatus" placeholder="审核状态" clearable style="width:120px" @change="load">
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
      <el-input v-model="filters.keyword" placeholder="搜索" clearable style="width:200px" @keyup.enter="load" />
      <el-button @click="load">搜索</el-button>
    </div>
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
      <el-table-column prop="publisherName" label="发布者" width="100" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-popconfirm title="确定下架？" @confirm="remove(row.id)">
            <template #reference>
              <el-button size="small" type="danger">下架</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api'
import { TYPE_LABELS, MOD_STATUS } from '@/utils/constants'

const loading = ref(false)
const posts = ref([])
const filters = reactive({ type: '', modStatus: '', keyword: '' })

async function load() {
  loading.value = true
  posts.value = await adminApi.posts(filters)
  loading.value = false
}

async function remove(id) {
  await adminApi.deletePost(id)
  ElMessage.success('已下架')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
