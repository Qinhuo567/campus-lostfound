<template>
  <div class="page-container">
    <h2 class="section-title">我的收藏</h2>
    <div v-if="posts.length" class="card-grid">
      <PostCard v-for="p in posts" :key="p.id" :post="p" />
    </div>
    <div v-else class="empty-state">暂无收藏</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PostCard from '@/components/PostCard.vue'
import { interactionApi } from '@/api'

const posts = ref([])
onMounted(async () => { posts.value = await interactionApi.favorites() })
</script>
