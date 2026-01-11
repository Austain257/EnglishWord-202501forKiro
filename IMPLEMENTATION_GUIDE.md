# 英语学习系统单词学习算法实现指南

## 🎯 实现概述

本指南详细说明了基于艾宾浩斯遗忘曲线理论的英语学习系统实现，核心功能包括**30分钟新词学习 + 两轮强制复习**的最小记忆强化单元。

## 📋 已完成功能

### ✅ 阶段一：数据库层实现
- **核心数据表**: `word_study_record` 表创建完成
- **存储过程**: 每日学习清单自动生成存储过程
- **文件位置**: `English_back_java/src/main/resources/sql/word_study_algorithm_tables.sql`

### ✅ 阶段二：后端API实现
- **实体类**: WordStudyRecord.java 及相关DTO类
- **控制器**: WordStudyController.java, StudyLockController.java  
- **服务层**: WordStudyService, StudyLockService 接口及实现
- **数据访问**: WordStudyRecordMapper.java 完整数据库操作

### ✅ 阶段三：前端组件实现
- **StudyTimer**: 30分钟学习计时器组件 (`src/components/learning/StudyTimer.vue`)
- **RangeSelector**: 学习范围选择组件 (`src/components/learning/RangeSelector.vue`)
- **StudyLockGuard**: 状态锁定检查组件 (`src/components/common/StudyLockGuard.vue`)
- **ReviewCompleteButton**: 复习完成标记组件 (`src/components/common/ReviewCompleteButton.vue`)
- **状态管理**: wordStudy.js Pinia store (`src/stores/wordStudy.js`)
- **API服务**: wordStudy.service.js (`src/services/wordStudy.service.js`)

### ✅ 阶段四：路由守卫和集成
- **路由守卫**: 学习锁定检查守卫 (`src/router/guards.js`)
- **全局集成**: StudyLockGuard组件已集成到App.vue
- **页面集成**: StudyTimer已集成到WordLearning.vue，ReviewCompleteButton已集成到WordReview.vue

## 🔧 部署和运行步骤

### 1. 数据库配置

执行以下SQL脚本创建核心表：
```bash
# 在MySQL中执行
source d:\develop\code-file\kiro\EnglishWord-202501forKiro\English_back_java\src\main\resources\sql\word_study_algorithm_tables.sql
```

### 2. 后端启动

```bash
cd English_back_java
./mvnw spring-boot:run
# 或使用IDE运行 EnglishAndWordApplication.java
```

后端将在 http://localhost:8080 启动

### 3. 前端启动

```bash
cd English_front_vue
npm install
npm run dev
```

前端将在开发模式下启动，自动配置代理到后端

## 🎮 功能使用流程

### 核心学习流程

1. **开始学习**
   - 用户访问单词学习页面 `/word/learning`
   - 设置学习范围（推荐每次50个单词以内）
   - 点击"开始学习 (30分钟)"按钮启动计时器
   - 系统创建学习会话记录

2. **30分钟学习过程**
   - 计时器显示倒计时进度
   - 用户可以暂停/恢复学习
   - 支持提前结束（需确认）
   - 学习结束时自动保存记录

3. **状态锁定机制**
   - 学习结束后系统自动锁定其他功能
   - 用户无法访问新词学习、游戏等功能
   - 只允许访问复习中心和积累本

4. **第一轮复习**
   - 访问复习页面 `/word/review/first`
   - 进行英译汉卡片复习
   - 点击"标记第1轮复习已完成"按钮
   - 系统记录第一轮复习时间

5. **第二轮复习**
   - 继续在复习页面进行第二轮复习
   - 可以是汉译英或其他模式
   - 点击"标记第2轮复习已完成"按钮
   - 系统记录第二轮复习时间

6. **功能解锁**
   - 完成两轮复习后系统自动解锁所有功能
   - 用户可以开始新的学习周期

### API接口说明

#### 学习会话管理
```http
POST /api/word-study/start
# 开始学习会话

POST /api/word-study/end/{sessionId}  
# 结束学习会话

GET /api/word-study/review-status/{userId}
# 检查复习状态

POST /api/word-study/review/complete
# 标记复习完成
```

#### 功能锁定检查
```http
GET /api/study-lock/check/{userId}
# 检查锁定状态

GET /api/study-lock/pending-reviews/{userId}  
# 获取待完成复习任务
```

## 🗃️ 数据库表结构

### word_study_record 表
- `id`: 主键，学习记录唯一标识
- `user_id`: 用户ID
- `book_id`: 课本ID
- `start_time`: 学习开始时间
- `end_time`: 学习结束时间  
- `start_id`/`end_id`: 学习范围的起止单词ID
- `status`: 会话状态（1-进行中，0-正常结束，4-异常退出）
- `round_1_review_time` ~ `round_8_review_time`: 各轮复习完成时间
- `create_time`/`update_time`: 记录的创建和更新时间

## 🎨 前端组件说明

### StudyTimer.vue
- 圆形进度条显示剩余时间
- 支持开始、暂停、恢复、提前结束
- 集成学习会话API调用
- 响应式设计，适配移动端

### StudyLockGuard.vue  
- 全局状态锁定检查
- 自动显示复习提醒弹窗
- 待完成复习任务列表
- 引导用户进入复习中心

### ReviewCompleteButton.vue
- 复习进度可视化显示
- 支持标记第1、2轮复习完成
- 进度步骤指示器
- 完成后自动解锁提示

## ⚠️ 注意事项

1. **CSS Linting警告**: `@apply` 规则警告是正常的Tailwind CSS语法，不影响功能
2. **API路径配置**: 确保前端API基础路径配置为 `http://192.168.43.106:8080/api`
3. **数据库连接**: 确保MySQL数据库 `english_for_kiro` 已创建并可访问
4. **用户权限**: 需要用户登录后才能使用学习功能

## 🔄 状态流转图

```
开始30分钟学习 → 选择学习范围 → 启动计时器 → 学习单词
                                    ↓
检查复习状态 ← 保存学习记录 ← 30分钟结束 ← 强制结束学习
    ↓
功能锁定 → 进入复习中心 → 第一轮复习 → 标记完成
    ↓                              ↓
解锁检查 ← 第二轮复习完成 ← 第二轮复习 ← 继续复习
    ↓
全功能解锁
```

## 📞 技术支持

如遇到问题，请检查：
1. 数据库连接是否正常
2. 后端服务是否启动在正确端口
3. 前端API路径是否配置正确
4. 用户是否已登录并选择课本

---

**实现完成时间**: 2026-01-09  
**技术栈**: Spring Boot 3.5.4 + Vue 3.5.25 + MySQL 8.0 + Tailwind CSS  
**核心特性**: 艾宾浩斯遗忘曲线 + 强制复习机制 + 状态锁定系统
