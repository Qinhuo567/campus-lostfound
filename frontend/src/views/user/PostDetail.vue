<template>
  <div class="page-container" v-loading="loading">
    <el-row :gutter="24" v-if="post">
      <el-col :span="16">
        <el-card>
          <div class="detail-header">
            <el-tag :type="post.type === 'lost' ? '' : 'warning'">{{ TYPE_LABELS[post.type] }}</el-tag>
            <h1>{{ post.title }}</h1>
            <div class="meta-row">
              <span>{{ post.category }}</span>
              <span>{{ post.location }}</span>
              <span>{{ formatTime(post.eventTime) }}</span>
            </div>
          </div>
          <img class="cover" :src="post.imageUrl || placeholderImage(post.category)" />
          <div class="desc">{{ post.description }}</div>
          <div class="actions">
            <el-button v-if="post.type === 'found' && post.postStatus === 'open'" type="primary" @click="showClaim = true">我要认领</el-button>
            <el-button :type="post.favorited ? 'warning' : 'default'" @click="toggleFav">
              {{ post.favorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>

          <div class="comments">
            <h3>评论 ({{ comments.length }})</h3>
            <el-input v-if="userStore.isLoggedIn" v-model="commentText" placeholder="写下你的留言..." @keyup.enter="addComment">
              <template #append><el-button @click="addComment">发送</el-button></template>
            </el-input>
            <div v-for="c in comments" :key="c.id" class="comment-item">
              <strong>{{ c.nickname }}</strong>
              <span>{{ c.content }}</span>
              <small>{{ formatTime(c.createdAt) }}</small>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card v-if="matches.length">
          <template #header>为你推荐</template>
          <div v-for="m in matches.slice(0, 3)" :key="m.id" class="match-item">
            <div class="score">{{ m.score }}分</div>
            <p>{{ m.reason }}</p>
            <router-link :to="`/posts/${m.lostPost?.id === post.id ? m.foundPost?.id : m.lostPost?.id}`">
              查看匹配帖
            </router-link>
          </div>
        </el-card>

        <el-card v-if="isOwner && pendingClaims.length" style="margin-top:16px">
          <template #header>待处理认领</template>
          <div v-for="c in pendingClaims" :key="c.id" class="claim-item">
            <p>{{ c.claimerName }}：{{ c.proof }}</p>
            <el-button size="small" type="primary" @click="approveClaim(c.id)">同意</el-button>
            <el-button size="small" @click="rejectClaim(c.id)">拒绝</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showClaim" title="发起认领" width="400px">
      <el-input v-model="claimProof" type="textarea" :rows="4" placeholder="请描述物品特征以证明是你的" />
      <template #footer>
        <el-button @click="showClaim = false">取消</el-button>
        <el-button type="primary" @click="submitClaim">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi, interactionApi, matchApi } from '@/api'
import { TYPE_LABELS, formatTime, placeholderImage } from '@/utils/constants'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(true)
const post = ref(null)
const comments = ref([])
const matches = ref([])
const pendingClaims = ref([])
const showClaim = ref(false)
const claimProof = ref('')
const commentText = ref('')

const isOwner = computed(() => post.value && userStore.user?.id === post.value.userId)

async function load() {
  loading.value = true
  try {
    const id = route.params.id
    post.value = await postApi.detail(id)
    comments.value = await interactionApi.comments(id)
    matches.value = await matchApi.forPost(id)
    if (isOwner.value && post.value.type === 'found') {
      const claims = await postApi.postClaims(id)
      pendingClaims.value = claims.filter(c => c.status === 'pending')
    }
  } finally {
    loading.value = false
  }
}

async function toggleFav() {
  if (!userStore.isLoggedIn) return
  const res = await interactionApi.toggleFavorite(post.value.id)
  post.value.favorited = res.favorited
}

async function addComment() {
  if (!commentText.value.trim()) return
  await interactionApi.addComment(post.value.id, commentText.value)
  commentText.value = ''
  comments.value = await interactionApi.comments(post.value.id)
}

async function submitClaim() {
  await postApi.claim(post.value.id, { proof: claimProof.value })
  ElMessage.success('认领申请已提交')
  showClaim.value = false
  load()
}

async function approveClaim(claimId) {
  await postApi.approveClaim(post.value.id, claimId)
  ElMessage.success('已同意认领')
  load()
}

async function rejectClaim(claimId) {
  await postApi.rejectClaim(post.value.id, claimId)
  ElMessage.success('已拒绝认领')
  load()
}

onMounted(load)
</script>

<style scoped>
.detail-header h1 { margin: 12px 0; font-size: 22px; }
.meta-row { display: flex; gap: 16px; color: var(--text-secondary); font-size: 14px; }
.cover { width: 100%; max-height: 400px; object-fit: cover; border-radius: 8px; margin: 16px 0; }
.desc { line-height: 1.8; white-space: pre-wrap; }
.actions { margin: 20px 0; display: flex; gap: 12px; }
.comments { margin-top: 24px; border-top: 1px solid var(--border); padding-top: 16px; }
.comment-item { padding: 10px 0; border-bottom: 1px solid var(--border); display: flex; flex-direction: column; gap: 4px; }
.comment-item small { color: var(--text-secondary); }
.match-item { padding: 10px 0; border-bottom: 1px solid var(--border); }
.score { color: var(--primary); font-weight: 700; font-size: 18px; }
.claim-item { padding: 10px 0; border-bottom: 1px solid var(--border); }
</style>
