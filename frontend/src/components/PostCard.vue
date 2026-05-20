<template>
  <div class="post-card" @click="$router.push(`/posts/${post.id}`)">
    <div class="cover">
      <img :src="post.imageUrl || placeholderImage(post.category)" :alt="post.title" />
      <span class="type-tag" :class="post.type">{{ TYPE_LABELS[post.type] }}</span>
    </div>
    <div class="body">
      <h3 class="title">{{ post.title }}</h3>
      <p class="meta">
        <el-icon><Location /></el-icon> {{ post.location }}
      </p>
      <p class="meta">
        <el-icon><Clock /></el-icon> {{ formatTime(post.eventTime) }}
      </p>
      <div class="footer">
        <el-tag size="small" :type="POST_STATUS[post.postStatus]?.type">{{ POST_STATUS[post.postStatus]?.label }}</el-tag>
        <span class="category">{{ post.category }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Location, Clock } from '@element-plus/icons-vue'
import { TYPE_LABELS, POST_STATUS, formatTime, placeholderImage } from '@/utils/constants'

defineProps({
  post: { type: Object, required: true }
})
</script>

<style scoped>
.post-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 1px solid var(--border);
}

.post-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.type-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.type-tag.lost { background: #409eff; }
.type-tag.found { background: var(--primary); }

.body { padding: 12px 14px; }

.title {
  margin: 0 0 8px;
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta {
  margin: 4px 0;
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.category {
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
