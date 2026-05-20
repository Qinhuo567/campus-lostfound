<template>
  <div class="page-container">
    <h2 class="section-title">{{ postType === 'lost' ? '发布寻物' : '发布招领' }}</h2>
    <el-card>
      <el-form :model="form" label-width="100px" style="max-width:600px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="简要描述物品" />
        </el-form-item>
        <el-form-item label="类别" required>
          <el-select v-model="form.category" style="width:100%">
            <el-option v-for="c in CATEGORIES" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点" required>
          <el-input v-model="form.location" placeholder="丢失/拾取地点" />
        </el-form-item>
        <el-form-item label="时间" required>
          <el-date-picker v-model="form.eventTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="详细描述" required>
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="物品特征、备注信息" />
        </el-form-item>
        <el-form-item label="联系方式" required>
          <el-input v-model="form.contact" placeholder="微信/QQ/手机号" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload :show-file-list="false" :http-request="uploadImage" accept="image/*">
            <img v-if="form.imageUrl" :src="form.imageUrl" class="preview" />
            <el-button v-else>上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">提交审核</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi, uploadApi } from '@/api'
import { CATEGORIES } from '@/utils/constants'

const props = defineProps({ postType: { type: String, default: 'lost' } })
const router = useRouter()
const loading = ref(false)
const form = reactive({
  type: props.postType,
  title: '',
  category: '',
  location: '',
  eventTime: '',
  description: '',
  contact: '',
  imageUrl: ''
})

async function uploadImage({ file }) {
  const res = await uploadApi.upload(file)
  form.imageUrl = res.url
}

async function submit() {
  loading.value = true
  try {
    form.type = props.postType
    await postApi.create(form)
    ElMessage.success('发布成功，等待管理员审核')
    router.push('/mine/posts')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}
</style>
