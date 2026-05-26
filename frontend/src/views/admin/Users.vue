<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称/学号" clearable style="width:240px" @keyup.enter="load" />
      <el-button @click="load">搜索</el-button>
    </div>
    <el-table :data="users" v-loading="loading">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="studentNo" label="学号" />
      <el-table-column prop="role" label="角色" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
            {{ row.status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            v-if="row.role !== 'admin'"
            size="small"
            :type="row.status === 'active' ? 'danger' : 'success'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      style="margin-top:16px"
      @current-change="load"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api'

const loading = ref(false)
const users = ref([])
const keyword = ref('')
const page = ref(1)
const size = ref(20)
const total = ref(0)

async function load() {
  loading.value = true
  const res = await adminApi.users({ keyword: keyword.value || undefined, page: page.value - 1, size: size.value })
  users.value = res.content
  total.value = res.totalElements
  loading.value = false
}

async function toggleStatus(row) {
  const status = row.status === 'active' ? 'disabled' : 'active'
  await adminApi.updateUserStatus(row.id, status)
  ElMessage.success('操作成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
