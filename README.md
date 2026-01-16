# 轻风智语（EnglishWord-202501forKiro） 🧭

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot) [![Vue.js](https://img.shields.io/badge/Vue.js-3.5.25-4FC08D.svg)](https://vuejs.org/) [![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

> 一个聚合 **英语学习（词汇 + 句子 + 文章）** 与 **AI 伴学** 的智能学习平台，基于艾宾浩斯遗忘曲线，为坚持与高效而设计。


## ✨ 核心特色
- 🧠 **科学记忆节奏**：30 分钟学习 + 多轮复习，状态锁定防止漏复习。
- 🤖 **AI 赋能**：OpenAI + 通义千问，多场景生成释义、推荐与纠错。
- 📊 **学习追踪**：今日/累计时长、学习会话管理、进度可视化。
- 🎮 **趣味练习**：听写、速记、泡泡龙、错词本等多模式练习。
- 📱 **多端适配**：PC 全宽布局，移动端卡片紧凑，弹窗防溢出。

## 🏗 技术栈
**后端**：Spring Boot 3.5.4 · JDK 17 · MySQL 8 · MyBatis 3.0.4 · Spring AI · OpenAI/通义千问 · PageHelper · Spring Security Crypto  
**前端**：Vue 3.5.25 · Vite 7.2.4 · Pinia 3.0.4 · Vue Router 4.6.4 · Tailwind CSS 3.4.19 · Axios 1.13.2 · ECharts 6.0.0

## ⚙️ 环境与约束
- 后端端口固定：`8080`（Java Spring）。
- 前端访问后端基础路径：`http://austain.top/api`（全局统一）。
- 环境变量文件（Vite）：
  - `.env`：通用配置（被其他环境叠加）。
  - `.env.development`：仅 `npm run dev` 时加载。
  - `.env.production`：仅 `npm run build` 时加载。
  - 变量需以 `VITE_` 前缀，例如 `VITE_API_BASE_URL=http://192.168.0.106/api`。

## 🚀 快速开始
### 环境要求
- Java 17+ · Node.js 20.19.0+ (或 22.12.0+) · MySQL 8.0+ · Maven 3.6+

### 1）数据库
```sql
CREATE DATABASE english_for_kiro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
source English_back_java/create_tables.sql;
```

### 2）后端（端口 8080）
```bash
cd English_back_java
# 配置 application.yml：数据库连接 & AI Key
./mvnw spring-boot:run   # 或 IDE 运行 EnglishAndWordApplication.java
# 启动后：http://localhost:8080
```

### 3）前端（基于 Vite）
```bash
cd English_front_vue
# 设置 VITE_API_BASE_URL=http://192.168.0.106/api （写入 .env.development/.env.production）
npm install
npm run dev   # 开发
npm run build # 打包
```

## 📋 功能概览
- 词汇/句子学习与复习：学习、听写、错词本、速记挑战、泡泡龙。
- 学习会话：自动开始/结束、时长累积、防抖、并发控制。
- 数据面板：今日/累计时长、坚持天数、进度条与可视化。
- AI 场景：释义生成、内容推荐、错误分析（OpenAI/通义千问）。

## 🧠 学习流程 & 复习规律
1. **开启 30 分钟学习**：在 WordLearning 页选择课本与范围（≤50 词）后启动计时器，自动创建 `word_study_record` 并锁定其他功能。
2. **完成学习并锁定**：学习结束/强制结束都会触发状态锁定，用户仅能进入复习与错题中心。
3. **多轮复习**：WordReview、WordOption、WordGame 等页面会自动读取最近一次 `word_study_record` 的 `bookId/startId/endId`，提示学习范围并调用 `wordStore.fetchWords`。
4. **解锁条件**：完成至少两轮复习后系统自动解锁；若中途离开可重新进入继续复习。

**复习节奏（艾宾浩斯曲线）**

| 轮次 | 触发时间（相对学习结束） |
| --- | --- |
| 第 1 轮 | 当天学习后 30 分钟 |
| 第 2 轮 | 第 1 轮结束后 1 小时内 |
| 第 3 轮 | 当天睡前 |
| 第 4~9 轮 | 分别为学习后第 1/2/4/7/15/30 天 |

系统会在待办提醒与复习页面提示当前轮次与范围，帮助用户坚持计划。

## 🎨 设计与体验
- 品牌：轻风智语 / BreezeWise，Logo 见上方链接。
- 配色：低亮度青绿/湖蓝渐变，护眼；导航/卡片采用柔和透明与毛玻璃。
- 响应式：PC 全宽布局，移动端卡片紧凑，弹窗防溢出。

## 📁 目录（简）
```
EnglishWord-202501forKiro/
├── English_back_java/      # 后端（Spring Boot）
├── English_front_vue/      # 前端（Vite + Vue 3）
├── TODO.md                 # 待办
├── IMPLEMENTATION_GUIDE.md # 开发指引
└── README.md
```

## 🔧 开发要点
- API 响应统一 `Result<T>`（code/msg/data）。
- MyBatis + PageHelper，SQL 放对应 mapper.xml；事务保障一致性。
- 前端路由 `meta.title` 自动写入浏览器标题，未登录跳转登录。
- 环境变量通过 `import.meta.env` 读取，必须 `VITE_` 前缀。

## 🔁 前后端联调自检
1. **数据库**：按 `new-database-add-sentence-record.sql` 初始化，确认 `english_for_kiro` 可连接。
2. **后端**：IDE 运行 `EnglishAndWordApplication`，确保 `http://localhost:8080/actuator/health` 返回 `UP`。
3. **前端**：`.env.*` 中统一设置 `VITE_API_BASE_URL=http://192.168.0.106/api`，并在浏览器 Network 中确认请求走该前缀。
4. **功能验证**：
   - 登录后进入 WordLearning，能正常锁定会话。
   - 跳转 WordReview/WordOption/WordGame 时自动带出最新学习范围。
   - 完成两轮复习后，学习按钮重新可用，首页统计可刷新。
5. **移动端适配**：通过浏览器设备模式检查卡片排版与弹窗高度，确认无溢出与可点击区域过小问题。

## 🧪 测试
- 后端：`cd English_back_java && ./mvnw test`
- 前端：`cd English_front_vue && npm run lint`

## 📦 部署
```bash
# 后端
cd English_back_java
./mvnw clean package -DskipTests

# 前端
cd English_front_vue
npm run build   # 会读取 .env.production
```
Docker（可选）：
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📄 许可证
MIT，详见 [LICENSE](LICENSE)。

## 📞 联系
- Issues：项目仓库 Issues
- 邮箱：your-email@example.com

**让英语学习更智能，让记忆更持久！** 🚀