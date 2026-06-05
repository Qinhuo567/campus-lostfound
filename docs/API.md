# API 接口文档

基础路径：`/api`

统一响应格式：

```json
{ "code": 200, "message": "success", "data": {} }
```

## 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/register | 注册 |
| POST | /auth/login | 登录 |
| GET | /auth/me | 当前用户（需 Token） |

## 帖子（田钰天）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /posts | 大厅列表 |
| GET | /posts/{id} | 详情 |
| POST | /posts | 发布 |
| GET | /posts/my | 我的发布 |
| PUT | /posts/{id} | 编辑 |
| POST | /posts/{id}/claims | 发起认领 |
| POST | /posts/{postId}/claims/{claimId}/approve | 同意认领 |
| POST | /posts/{postId}/claims/{claimId}/reject | 拒绝认领 |
| GET | /posts/my/claims | 我的认领 |

## 互动

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /posts/{id}/comments | 评论列表 |
| POST | /posts/{id}/comments | 发表评论 |
| POST | /posts/{id}/favorite | 收藏/取消 |
| GET | /favorites | 我的收藏 |

## 匹配（陈奕城）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /matches/for-post/{postId} | 帖子推荐匹配 |
| GET | /matches/my | 我的匹配 |
| POST | /matches/{id}/accept | 确认匹配 |
| POST | /matches/{id}/dismiss | 忽略匹配 |
| POST | /matches/run | 全量匹配（管理员） |

## 管理（林熹）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /admin/stats | 统计数据 |
| GET | /admin/pending | 待审核 |
| POST | /admin/posts/{id}/approve | 通过 |
| POST | /admin/posts/{id}/reject | 拒绝 |
| GET | /admin/posts | 帖子管理 |
| DELETE | /admin/posts/{id} | 下架 |
| GET | /admin/users | 用户列表 |
| PUT | /admin/users/{id}/status | 启用/禁用 |

## 其他

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /health | 健康检查 |
| GET | /stats | 公开统计数据 |
| POST | /upload | 图片上传 |
| POST | /seed | 示例数据（force=true 重置） |

认证方式：请求头 `Authorization: Bearer <token>`
