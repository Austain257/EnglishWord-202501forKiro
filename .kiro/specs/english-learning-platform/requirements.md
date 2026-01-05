# 需求文档 - 英语学习平台

## 简介

本文档定义了一个现代化英语学习平台的功能需求。该平台采用前后端分离架构，旨在解决英语学习者在词汇学习、句子学习、学习规划和知识积累方面的核心痛点。平台提供系统化的学习路径、智能复习机制和激励系统，帮助用户建立持续有效的英语学习习惯。

## 术语表

- **System**: 英语学习平台系统
- **User**: 使用平台进行英语学习的用户
- **Word_Book**: 单词课本，包含一组相关的英语单词
- **Word**: 英语单词实体，包含单词、音标、中文释义等信息
- **Sentence**: 英语句子实体，包含英文原句和中文翻译
- **Error_Word**: 用户标记为错误的单词，需要重点复习
- **Error_Sentence**: 用户标记为错误的句子，需要重点复习
- **Learning_Checklist**: 基于艾宾浩斯遗忘曲线的学习清单
- **Jotting**: 用户的随身记录，用于积累零散的单词或句子
- **Motivation_Quote**: 激励性文案（鸡汤文），用于鼓励用户学习
- **Grasp_Status**: 掌握状态，0-未掌握，1-已掌握，2-错词/错句
- **API_Base_URL**: 后端 API 基础路径 http://192.168.43.106:8080

## 需求

### 需求 1: 用户认证与授权

**用户故事**: 作为用户，我想要安全地登录和注册账号，以便访问个性化的学习内容和保存学习进度。

#### 验收标准

1. WHEN 未登录用户访问系统 THEN THE System SHALL 仅允许访问登录页面和注册页面
2. WHEN 用户提交有效的登录凭证 THEN THE System SHALL 验证凭证并返回认证令牌
3. WHEN 用户提交注册信息 THEN THE System SHALL 验证信息唯一性并创建新用户账号
4. WHEN 用户登录成功 THEN THE System SHALL 记录最后登录时间并允许访问所有功能
5. WHEN 用户请求登出 THEN THE System SHALL 清除认证状态并重定向到登录页面
6. IF 用户凭证无效或过期 THEN THE System SHALL 返回明确的错误信息并拒绝访问

### 需求 2: 课本管理系统

**用户故事**: 作为用户，我想要浏览和选择不同的单词课本，以便根据我的学习目标选择合适的学习内容。

#### 验收标准

1. WHEN 用户请求课本列表 THEN THE System SHALL 返回该用户可访问的所有课本（包括公开课本和用户私有课本）
2. WHEN 显示课本信息 THEN THE System SHALL 展示课本名称、描述、封面、预计词汇量和可见范围
3. WHEN 用户选择特定课本 THEN THE System SHALL 将该课本设置为当前学习上下文
4. WHILE 用户在特定课本上下文中 THE System SHALL 限定所有单词相关功能仅操作该课本的单词
5. WHEN 用户请求课本详情 THEN THE System SHALL 返回课本的完整信息和学习进度统计
6. WHERE 课本状态为禁用 THE System SHALL NOT 在课本列表中显示该课本

### 需求 3: 单词初学模式

**用户故事**: 作为初学者，我想要逐个学习新单词，以便系统地掌握词汇的基本信息。

#### 验收标准

1. WHEN 用户进入初学模式 THEN THE System SHALL 显示单个单词卡片（包含单词、音标、中文释义）
2. WHEN 用户点击[下一个]按钮 THEN THE System SHALL 显示下一个未掌握的单词
3. WHEN 用户点击[上一个]按钮 THEN THE System SHALL 显示上一个单词
4. WHEN 显示单词 THEN THE System SHALL 显示当前学习进度（已学/总词数）和进度条
5. WHEN 显示单词 THEN THE System SHALL 显示该单词在错词本中的出现次数
6. WHERE 单词掌握状态为已掌握(is_grasp=1) THE System SHALL 以灰色显示错词统计
7. WHERE 单词掌握状态为错词(is_grasp=2) THE System SHALL 高亮显示错词统计
8. WHEN 用户指定学习范围 THEN THE System SHALL 仅加载指定范围内的单词（如第1-50词）

### 需求 4: 单词复习模式

**用户故事**: 作为学习者，我想要复习已学单词并测试记忆，以便巩固词汇掌握程度。

#### 验收标准

1. WHEN 用户进入复习模式 THEN THE System SHALL 默认隐藏中文释义
2. WHEN 用户点击[显示中文]按钮 THEN THE System SHALL 显示当前单词的中文释义
3. WHEN 用户点击[已掌握]按钮 THEN THE System SHALL 将单词标记为已掌握(is_grasp=1)并触发数据库触发器
4. WHEN 用户点击[加入错词本]按钮 THEN THE System SHALL 将单词标记为错词(is_grasp=2)并增加错误次数
5. WHEN 用户点击[下一个]或[上一个]按钮 THEN THE System SHALL 导航到相应单词
6. WHEN 单词从错词状态变为已掌握 THEN THE System SHALL 自动将该单词添加到错词复习表

### 需求 5: 单词听写模式

**用户故事**: 作为学习者，我想要通过听写测试单词拼写和翻译，以便提高词汇的实际应用能力。

#### 验收标准

1. WHERE 听写模式为英译汉 THE System SHALL 显示英文单词并要求用户输入中文释义
2. WHERE 听写模式为汉译英 THE System SHALL 显示中文释义并要求用户输入英文单词
3. WHEN 用户提交答案 THEN THE System SHALL 立即校对并高亮显示错误部分
4. IF 用户答案正确 THEN THE System SHALL 触发彩带动画效果并自动进入下一词
5. IF 用户答案错误 THEN THE System SHALL 将单词标记为错词并增加错误次数
6. WHEN 用户答错 THEN THE System SHALL 提供[跳过]和[查看答案]选项
7. WHEN 显示反馈 THEN THE System SHALL 使用内联UI元素而非弹窗

### 需求 6: 句子学习模式

**用户故事**: 作为学习者，我想要学习完整的英语句子，以便提高语境理解和表达能力。

#### 验收标准

1. WHEN 用户进入生句学习模式 THEN THE System SHALL 同时显示英文原句和中文翻译
2. WHEN 用户点击[下一句]按钮 THEN THE System SHALL 显示下一个未掌握的句子
3. WHEN 用户点击[上一句]按钮 THEN THE System SHALL 显示上一个句子
4. WHEN 显示句子 THEN THE System SHALL 提供音频播放按钮
5. WHEN 用户进入句子听写模式 THEN THE System SHALL 隐藏中文翻译并要求用户输入
6. IF 用户听写错误 THEN THE System SHALL 将句子标记为错句(is_grasp=2)并增加错误次数
7. WHEN 用户请求提示 THEN THE System SHALL 提供部分句子内容作为提示

### 需求 7: 学习清单系统

**用户故事**: 作为学习者，我想要基于科学的复习算法管理学习计划，以便提高学习效率和记忆保持率。

#### 验收标准

1. WHEN 用户访问学习清单 THEN THE System SHALL 显示三个标签页（单词、句子、听力）
2. WHEN 用户选择标签 THEN THE System SHALL 显示对应类型的学习记录
3. WHEN 用户添加新清单项 THEN THE System SHALL 接受学习内容、类型和创建时间
4. WHEN 用户选中清单项 THEN THE System SHALL 标记该项为今日复习内容(selected=1)
5. WHEN 用户标记完成 THEN THE System SHALL 更新已复习状态(already_reviewed=1)
6. WHEN 用户批量选择多项 THEN THE System SHALL 支持批量标记完成操作
7. WHEN 用户删除清单项 THEN THE System SHALL 从数据库中移除该记录
8. WHEN 显示学习清单 THEN THE System SHALL 显示激励性鸡汤文

### 需求 8: 错词本系统

**用户故事**: 作为学习者，我想要专门复习我标记为错误的单词，以便重点攻克薄弱环节。

#### 验收标准

1. WHEN 用户进入错词本 THEN THE System SHALL 显示所有标记为错词(is_grasp=2)的单词
2. WHEN 用户在错词本中学习 THEN THE System SHALL 提供复习模式和听写模式
3. WHEN 用户在复习模式标记已掌握 THEN THE System SHALL 更新单词状态为已掌握(is_grasp=1)并进入下一词
4. WHEN 用户在复习模式标记未掌握 THEN THE System SHALL 增加错误次数并进入下一词
5. WHEN 用户在听写模式答对 THEN THE System SHALL 自动标记为已掌握并进入下一词
6. WHEN 用户在听写模式答错 THEN THE System SHALL 增加错误次数并进入下一词
7. WHEN 错词从错词状态变为已掌握 THEN THE System SHALL 触发数据库触发器将该词添加到错词复习表
8. WHEN 显示错词 THEN THE System SHALL 显示该词的累计错误次数

### 需求 9: 错句本系统

**用户故事**: 作为学习者，我想要专门复习我标记为错误的句子，以便提高句子理解和记忆能力。

#### 验收标准

1. WHEN 用户进入错句本 THEN THE System SHALL 显示所有标记为错句(is_grasp=2)的句子
2. WHEN 用户在错句本中学习 THEN THE System SHALL 提供复习模式和听写模式
3. WHEN 用户在复习模式标记已掌握 THEN THE System SHALL 更新句子状态为已掌握(is_grasp=1)并进入下一句
4. WHEN 用户在复习模式标记未掌握 THEN THE System SHALL 增加错误次数并进入下一句
5. WHEN 用户在听写模式答对 THEN THE System SHALL 自动标记为已掌握并进入下一句
6. WHEN 用户在听写模式答错 THEN THE System SHALL 增加错误次数并进入下一句
7. WHEN 错句从错句状态变为已掌握 THEN THE System SHALL 触发数据库触发器将该句添加到错句复习表
8. WHEN 显示错句 THEN THE System SHALL 显示该句的累计错误次数

### 需求 10: 知识积累系统（随身记）

**用户故事**: 作为学习者，我想要随时记录遇到的生词和好句，以便积累零散的学习内容。

#### 验收标准

1. WHEN 用户在任意页面 THEN THE System SHALL 在右下角显示悬浮的随身记按钮
2. WHEN 用户点击随身记按钮 THEN THE System SHALL 展开输入面板
3. WHEN 用户提交记录 THEN THE System SHALL 接受内容和类型（单词/句子）并保存
4. WHEN 用户访问积累本 THEN THE System SHALL 分页显示所有积累内容
5. WHEN 用户选择标签 THEN THE System SHALL 过滤显示单词或句子类型的记录
6. WHEN 用户编辑记录 THEN THE System SHALL 更新记录内容
7. WHEN 用户删除记录 THEN THE System SHALL 支持单条删除和批量删除
8. WHEN 用户标记已掌握 THEN THE System SHALL 更新记录的复习状态(reviewed=1)

### 需求 11: 激励系统

**用户故事**: 作为学习者，我想要在学习过程中获得鼓励和激励，以便保持学习动力和积极性。

#### 验收标准

1. WHEN 用户访问首页 THEN THE System SHALL 显示轮播的激励文案
2. WHEN 系统获取激励文案 THEN THE System SHALL 从数据库中随机选择启用状态的文案
3. WHEN 显示激励文案 THEN THE System SHALL 包含文案内容、作者和主题标签
4. WHERE 激励文案优先级更高 THE System SHALL 更频繁地显示该文案
5. WHEN 用户完成学习任务 THEN THE System SHALL 显示鼓励性反馈

### 需求 12: 响应式设计

**用户故事**: 作为用户，我想要在不同设备上使用平台，以便随时随地进行学习。

#### 验收标准

1. WHEN 用户在桌面设备访问(≥1200px) THEN THE System SHALL 显示完整的多列布局
2. WHEN 用户在平板设备访问(768px-1199px) THEN THE System SHALL 调整为适配平板的布局
3. WHEN 用户在移动设备访问(<768px) THEN THE System SHALL 显示单列移动优化布局
4. WHEN 页面加载 THEN THE System SHALL 在1.5秒内完成关键内容渲染
5. WHEN 用户交互 THEN THE System SHALL 在300毫秒内响应用户操作
6. WHEN 显示UI元素 THEN THE System SHALL 确保触摸目标至少44x44像素

### 需求 13: 错误处理与用户反馈

**用户故事**: 作为用户，我想要在操作失败时获得清晰的错误提示，以便了解问题并采取正确的行动。

#### 验收标准

1. IF API请求失败 THEN THE System SHALL 显示用户友好的错误消息
2. IF 网络连接中断 THEN THE System SHALL 提示用户检查网络连接
3. IF 用户输入无效数据 THEN THE System SHALL 在提交前进行客户端验证并显示错误
4. WHEN 操作成功 THEN THE System SHALL 显示简短的成功提示
5. WHEN 执行耗时操作 THEN THE System SHALL 显示加载指示器
6. IF 会话过期 THEN THE System SHALL 提示用户重新登录并保存当前状态

### 需求 14: 数据持久化与同步

**用户故事**: 作为用户，我想要我的学习进度和数据被可靠保存，以便在不同设备间同步学习状态。

#### 验收标准

1. WHEN 用户更新学习状态 THEN THE System SHALL 立即将更改持久化到后端数据库
2. WHEN 用户标记单词为已掌握 THEN THE System SHALL 触发数据库触发器自动处理错词复习逻辑
3. WHEN 用户在不同设备登录 THEN THE System SHALL 加载最新的学习进度和数据
4. IF 数据保存失败 THEN THE System SHALL 提示用户并提供重试选项
5. WHEN 用户离线操作 THEN THE System SHALL 在网络恢复后自动同步数据

### 需求 15: 性能与可扩展性

**用户故事**: 作为系统管理员，我想要系统具有良好的性能和可扩展性，以便支持大量用户和数据。

#### 验收标准

1. WHEN 查询单词列表 THEN THE System SHALL 使用索引优化查询性能
2. WHEN 加载大量数据 THEN THE System SHALL 实现分页加载机制
3. WHEN 用户请求数据 THEN THE System SHALL 仅返回必要的字段以减少传输量
4. WHEN 系统负载增加 THEN THE System SHALL 保持响应时间在可接受范围内
5. WHEN 添加新功能模块 THEN THE System SHALL 确保模块间松耦合便于扩展

### 需求 16: 安全性

**用户故事**: 作为系统管理员，我想要保护用户数据和系统安全，以便防止未授权访问和数据泄露。

#### 验收标准

1. WHEN 用户注册 THEN THE System SHALL 使用加密算法存储密码
2. WHEN 用户登录 THEN THE System SHALL 验证凭证并生成安全的认证令牌
3. WHEN 用户访问API THEN THE System SHALL 验证认证令牌的有效性
4. IF 检测到可疑活动 THEN THE System SHALL 记录日志并采取防护措施
5. WHEN 传输敏感数据 THEN THE System SHALL 使用HTTPS加密传输
6. WHEN 用户删除数据 THEN THE System SHALL 使用级联删除确保数据完整性

### 需求 17: 可访问性

**用户故事**: 作为有特殊需求的用户，我想要系统支持无障碍访问，以便我能够正常使用所有功能。

#### 验收标准

1. WHEN 显示UI元素 THEN THE System SHALL 提供适当的ARIA标签和语义化HTML
2. WHEN 用户使用键盘导航 THEN THE System SHALL 支持完整的键盘操作
3. WHEN 显示颜色信息 THEN THE System SHALL 确保足够的对比度符合WCAG 2.1 AA标准
4. WHEN 显示图片 THEN THE System SHALL 提供替代文本描述
5. WHEN 用户使用屏幕阅读器 THEN THE System SHALL 确保内容可被正确读取

## 未来扩展考虑

以下功能不在当前版本范围内，但应在架构设计中预留扩展空间：

- **社交学习**: 学习小组、排行榜、成就系统
- **自适应学习**: 基于用户表现动态调整学习内容和难度
- **离线模式**: PWA支持，允许离线学习核心内容
- **发音评测**: 集成语音识别技术，提供发音准确性反馈
- **学习分析**: 可视化仪表盘，展示学习进度、弱点分析
- **内容创作**: 教师端功能，允许创建自定义课程和测试
- **AI赋能**: 智能推荐学习内容、个性化学习路径规划
