import http from './http'

export const authApi = {
  register: (data) => http.post('/auth/register', data),
  login: (data) => http.post('/auth/login', data),
  me: () => http.get('/auth/me')
}

export const adminApi = {
  stats: () => http.get('/admin/stats'),
  pending: () => http.get('/admin/pending'),
  approve: (id) => http.post(`/admin/posts/${id}/approve`),
  reject: (id) => http.post(`/admin/posts/${id}/reject`),
  posts: (params) => http.get('/admin/posts', { params }),
  deletePost: (id) => http.delete(`/admin/posts/${id}`),
  users: (params) => http.get('/admin/users', { params }),
  updateUserStatus: (id, status) => http.put(`/admin/users/${id}/status`, { status }),
  runMatch: () => http.post('/matches/run')
}

export const postApi = {
  list: (params) => http.get('/posts', { params }),
  detail: (id) => http.get(`/posts/${id}`),
  create: (data) => http.post('/posts', data),
  update: (id, data) => http.put(`/posts/${id}`, data),
  myPosts: () => http.get('/posts/my'),
  myClaims: () => http.get('/posts/my/claims'),
  claim: (id, data) => http.post(`/posts/${id}/claims`, data),
  approveClaim: (postId, claimId) => http.post(`/posts/${postId}/claims/${claimId}/approve`),
  rejectClaim: (postId, claimId) => http.post(`/posts/${postId}/claims/${claimId}/reject`),
  postClaims: (id) => http.get(`/posts/${id}/claims`)
}

export const interactionApi = {
  comments: (id) => http.get(`/posts/${id}/comments`),
  addComment: (id, content) => http.post(`/posts/${id}/comments`, { content }),
  toggleFavorite: (id) => http.post(`/posts/${id}/favorite`),
  favorites: () => http.get('/favorites')
}

export const matchApi = {
  forPost: (id) => http.get(`/matches/for-post/${id}`),
  my: () => http.get('/matches/my'),
  accept: (id) => http.post(`/matches/${id}/accept`),
  dismiss: (id) => http.post(`/matches/${id}/dismiss`)
}

export const uploadApi = {
  upload: (file) => {
    const form = new FormData()
    form.append('file', file)
    return http.post('/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const seedApi = {
  seed: (force = false) => http.post(`/seed?force=${force}`)
}
