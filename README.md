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

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

## 功能模块与分工

| 成员 | 模块 |
|------|------|
| 陈炫竣 | Spring Boot 框架、数据库设计、公共基础设施 |
| 林熹 | 用户认证、管理端审核、用户/帖子管理 |
| 田钰天 | 寻物/招领发布、认领、评论、收藏 |
| 陈奕城 | 智能匹配算法与推荐 |

## 演示数据

管理员登录后，在首页点击「生成演示数据」，或调用：

```bash
curl -X POST http://localhost:8080/api/seed
```

## Git 分成员提交建议

```bash
git config user.name "陈炫竣"
git config user.email "your-email@example.com"
git add backend/ ...
git commit -m "feat: 初始化 Spring Boot 框架与数据库设计"
```

按模块依次切换成员身份提交即可。

## 项目结构

```
校园失物WEB/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── docs/API.md       # 接口文档
└── README.md
```
