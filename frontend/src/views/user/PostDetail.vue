<template>
  <div class="page-container" v-loading="loading">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>{{ TYPE_LABELS[post?.type] || '详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="24" v-if="post">
      <el-col :span="16">
        <el-card>
          <div class="detail-header">
            <div class="tags">
              <el-tag :type="post.type === 'lost' ? 'primary' : 'warning'">{{ TYPE_LABELS[post.type] }}</el-tag>
              <el-tag :type="POST_STATUS[post.postStatus]?.type">{{ POST_STATUS[post.postStatus]?.label }}</el-tag>
              <el-tag type="info">{{ post.category }}</el-tag>
            </div>
            <h1>{{ post.title }}</h1>
            <div class="meta-row">
              <span><el-icon><Location /></el-icon> {{ post.location }}</span>
              <span><el-icon><Clock /></el-icon> {{ formatTime(post.eventTime) }}</span>
              <span><el-icon><User /></el-icon> {{ post.publisherName || '匿名用户' }}</span>
            </div>
          </div>
          <img class="cover" :src="post.imageUrl || placeholderImage(post.category)" />
          <div class="desc">{{ post.description }}</div>
          <div class="actions">
            <el-button
              v-if="post.type === 'found' && post.postStatus === 'open'"
              type="primary"
              @click="handleClaim"
            >我要认领</el-button>
            <el-button
              v-if="post.type === 'found' && post.postStatus === 'pending_claim' && !isOwner"
              disabled
            >认领审核中</el-button>
            <el-button :type="post.favorited ? 'warning' : 'default'" @click="handleFav">
              {{ post.favorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button @click="$router.push('/matches')">查看匹配</el-button>
          </div>

          <div class="comments">
            <h3>评论互动 ({{ comments.length }})</h3>
            <el-input
              v-if="userStore.isLoggedIn"
              v-model="commentText"
              placeholder="写下你的留言..."
              @keyup.enter="addComment"
            >
              <template #append><el-button @click="addComment">发送</el-button></template>
            </el-input>
            <p v-else class="login-hint">登录后可发表评论</p>
            <div v-if="comments.length" class="comment-list">
              <div v-for="c in comments" :key="c.id" class="comment-item">
                <el-avatar :size="32">{{ c.nickname?.[0] || 'U' }}</el-avatar>
                <div class="comment-body">
                  <strong>{{ c.nickname }}</strong>
                  <span>{{ c.content }}</span>
                  <small>{{ formatTime(c.createdAt) }}</small>
                </div>
              </div>
            </div>
            <div v-else class="empty-comments">暂无评论</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="side-card">
          <template #header>联系方式</template>
          <div v-if="userStore.isLoggedIn" class="contact-box">
            <p>{{ post.contact }}</p>
            <el-text type="info" size="small">请通过上述方式联系发布者</el-text>
          </div>
          <div v-else class="contact-mask">
            <p>登录后查看联系方式</p>
            <el-button type="primary" size="small" @click="$router.push({ name: 'login', query: { redirect: $route.fullPath } })">去登录</el-button>
          </div>
        </el-card>

        <el-card v-if="matches.length" class="side-card">
          <template #header>智能匹配推荐</template>
          <div v-for="m in matches.slice(0, 3)" :key="m.id" class="match-item">
            <div class="match-top">
              <span class="score">{{ m.score }}分</span>
              <el-tag size="small">{{ m.reason || '智能推荐' }}</el-tag>
            </div>
            <router-link class="match-link" :to="`/posts/${m.lostPost?.id === post.id ? m.foundPost?.id : m.lostPost?.id}`">
              查看匹配帖 →
            </router-link>
          </div>
        </el-card>

        <el-card v-if="isOwner && pendingClaims.length" class="side-card">
          <template #header>待处理认领</template>
          <div v-for="c in pendingClaims" :key="c.id" class="claim-item">
            <p><strong>{{ c.claimerName }}</strong></p>
            <p class="proof">{{ c.proof }}</p>
            <div class="claim-actions">
              <el-button size="small" type="primary" @click="approveClaim(c.id)">同意</el-button>
              <el-button size="small" @click="rejectClaim(c.id)">拒绝</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showClaim" title="发起认领" width="440px">
      <el-alert type="info" :closable="false" show-icon title="请详细描述物品特征，便于发布者核实" style="margin-bottom:16px" />
      <el-input v-model="claimProof" type="textarea" :rows="4" placeholder="例如：充电盒背面刮痕位置、贴纸样式等" />
      <template #footer>
        <el-button @click="showClaim = false">取消</el-button>
        <el-button type="primary" :loading="claimLoading" @click="submitClaim">提交认领</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Clock, User } from '@element-plus/icons-vue'
import { postApi, interactionApi, matchApi } from '@/api'
import { TYPE_LABELS, POST_STATUS, formatTime, placeholderImage } from '@/utils/constants'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(true)
const claimLoading = ref(false)
const post = ref(null)
const comments = ref([])
const matches = ref([])
const pendingClaims = ref([])
const showClaim = ref(false)
const claimProof = ref('')
const commentText = ref('')

const isOwner = computed(() => post.value && userStore.user?.id === post.value.userId)

function requireLogin(redirect) {
  ElMessage.warning('请先登录')
  router.push({ name: 'login', query: { redirect: redirect || route.fullPath } })
}

async function load() {
  loading.value = true
  try {
    const id = route.params.id
    post.value = await postApi.detail(id)
    comments.value = await interactionApi.comments(id)
    matches.value = await matchApi.forPost(id)
    if (post.value.userId === userStore.user?.id && post.value.type === 'found') {
      const claims = await postApi.postClaims(id)
      pendingClaims.value = claims.filter(c => c.status === 'pending')
    } else {
      pendingClaims.value = []
    }
  } finally {
    loading.value = false
  }
}

function handleClaim() {
  if (!userStore.isLoggedIn) return requireLogin()
  if (isOwner.value) return ElMessage.warning('不能认领自己发布的物品')
  showClaim.value = true
}

function handleFav() {
  if (!userStore.isLoggedIn) return requireLogin()
  toggleFav()
}

async function toggleFav() {
  const res = await interactionApi.toggleFavorite(post.value.id)
  post.value.favorited = res.favorited
  ElMessage.success(res.favorited ? '已收藏' : '已取消收藏')
}

async function addComment() {
  if (!commentText.value.trim()) return
  await interactionApi.addComment(post.value.id, commentText.value)
  commentText.value = ''
  comments.value = await interactionApi.comments(post.value.id)
  ElMessage.success('评论成功')
}

async function submitClaim() {
  if (!claimProof.value.trim()) return ElMessage.warning('请填写认领说明')
  claimLoading.value = true
  try {
    await postApi.claim(post.value.id, { proof: claimProof.value })
    ElMessage.success('认领申请已提交，等待发布者审核')
    showClaim.value = false
    claimProof.value = ''
    load()
  } finally {
    claimLoading.value = false
  }
}

async function approveClaim(claimId) {
  await postApi.approveClaim(post.value.id, claimId)
  ElMessage.success('已同意认领，物品状态已关闭')
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
.breadcrumb { margin-bottom: 16px; }
.detail-header h1 { margin: 12px 0; font-size: 22px; }
.tags { display: flex; gap: 8px; flex-wrap: wrap; }
.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 14px;
}
.meta-row span { display: flex; align-items: center; gap: 4px; }
.cover { width: 100%; max-height: 420px; object-fit: cover; border-radius: 8px; margin: 16px 0; }
.desc { line-height: 1.8; white-space: pre-wrap; color: #333; }
.actions { margin: 20px 0; display: flex; gap: 12px; flex-wrap: wrap; }
.comments { margin-top: 24px; border-top: 1px solid var(--border); padding-top: 16px; }
.login-hint { font-size: 13px; color: var(--text-secondary); }
.comment-list { margin-top: 16px; }
.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}
.comment-body { display: flex; flex-direction: column; gap: 4px; }
.comment-body small { color: var(--text-secondary); }
.empty-comments { color: var(--text-secondary); font-size: 13px; padding: 12px 0; }
.side-card { margin-bottom: 16px; }
.contact-box p { font-size: 16px; font-weight: 600; margin: 0 0 8px; }
.contact-mask { text-align: center; padding: 12px 0; color: var(--text-secondary); }
.match-item { padding: 12px 0; border-bottom: 1px solid var(--border); }
.match-top { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.score { color: var(--primary); font-weight: 700; font-size: 18px; }
.match-link { color: var(--primary); font-size: 13px; }
.claim-item { padding: 12px 0; border-bottom: 1px solid var(--border); }
.proof { color: var(--text-secondary); font-size: 13px; margin: 8px 0; }
.claim-actions { display: flex; gap: 8px; }
</style>
