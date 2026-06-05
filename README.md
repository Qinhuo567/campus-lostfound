# 校园失物招领与寻物系统

基于 Spring Boot + SQLite + Vue 3 的校园失物招领平台，支持寻物/招领发布、管理员审核、认领互动、智能匹配。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2、JPA、SQLite、JWT |
| 前端 | Vue 3、Vite、Element Plus、Pinia |

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+

### 后端

```bash
cd backend
mvn spring-boot:run
```

服务地址：http://localhost:8080

### 前端

```bash
cd frontend
npm install
npm run dev
```

访问：http://localhost:5173

## 预置账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 审核、统计、匹配 |
| 普通用户 | demo | demo123 | 有寻物帖、待处理认领 |
| 小明 | xiaoming | demo123 | 有寻物帖、评论 |
| 丽丽 | lili | demo123 | 有招领帖、待审核认领 |

**首次启动**会自动初始化示例数据（8 条帖子、评论、匹配、1 条待审核认领）。

登录页支持**快捷登录**。

## 使用流程

1. 管理员登录 → 待审核 → 通过「U盘」「钥匙」等待审帖
2. 用户端 → 智能匹配 → 查看寻物/招领配对
3. 用 demo 登录 → 打开 AirPods 招领帖 → 审核认领申请
4. 用 xiaoming 登录 → 对校园卡招领帖发起认领

## 功能模块与分工

| 成员 | 模块 |
|------|------|
| 陈炫竣 | Spring Boot 框架、数据库设计、公共基础设施 |
| 林熹 | 用户认证、管理端审核、用户/帖子管理 |
| 田钰天 | 寻物/招领发布、认领、评论、收藏 |
| 陈奕城 | 智能匹配算法与推荐 |

## 示例数据

首次启动自动初始化。如需手动重置：

```bash
curl -X POST "http://localhost:8080/api/seed?force=true"
```

管理员也可在后台「重置示例数据」。

## 项目结构

```
校园失物WEB/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── docs/API.md       # 接口文档
└── README.md
```
