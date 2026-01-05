

-- 建立英语单词表
-- TODO 数据库操作：添加用户id和单词本id的外键；添加触发器：当'is_grasp'字段从2变为1时，向二次复习表’word_error_review‘中添加该数据，添加时’id‘、’is_grasp'、‘error_time’、‘create_time’字段不添加，保持默认即可
-- TODO 非数据库操作：提供创建单词本的接口;提供用户自己标记已掌握/还不会功能，还不会或者听写错误时添加错误次数字段，前端同步显示错误次数，当标记为未掌握时，错误次数不归零，前端显示灰色；更新已掌握字段时，当从未掌握标记为已掌握时，触发触发器，向二次复习表中添加该数据
DROP TABLE IF EXISTS `english_word_01`;
CREATE TABLE `english_word_01` (
                                    `id` int NOT NULL AUTO_INCREMENT COMMENT '单词编号',
                                    `user_id` int NOT NULL default 0 COMMENT '用户编号',
                                    `book_id` int NOT NULL COMMENT '单词本编号',
                                    `word` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
                                    `chinese` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文释义',
                                    `pronounce` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音标',
                                    `times` int NOT NULL DEFAULT '0' COMMENT '四级出现次数',
                                    `is_grasp` int(1) DEFAULT '0' COMMENT '是否掌握(1掌握，0未掌握，2错词，默认0)',
                                    `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='生单词表';


-- 建立错词二次复习表
-- TODO 数据库操作：添加用户id和单词本id的外键；
-- TODO 非数据库操作：先建立，后续看看前端如何使功能更加完善
DROP TABLE IF EXISTS `word_error_review`;
CREATE TABLE `word_error_review` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT '错词词编号',
                                   `user_id` int NOT NULL default 0 COMMENT '用户编号',
                                   `book_id` int NOT NULL COMMENT '单词本编号',
                                   `word` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
                                   `chinese` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文释义',
                                   `pronounce` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音标',
                                   `times` int NOT NULL DEFAULT '0' COMMENT '四级出现次数',
                                   `is_grasp` int(1) DEFAULT '0' COMMENT '是否掌握(1掌握，0未掌握，默认0)',
                                   `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错词二次复习表';


-- 建立学习清单，每天定时给用户反馈，采用艾宾浩斯遗忘曲线规律返回
-- TODO 数据库操作：添加用户id的外键
-- TODO 非数据库操作：提供tab标签选择添加的记录是单词还是句子或是听力
DROP TABLE IF EXISTS `learning_checklist`;
CREATE TABLE `learning_checklist` (
                                `id` int NOT NULL AUTO_INCREMENT COMMENT '清单/计划编号',
                                `user_id` int NOT NULL DEFAULT 0 COMMENT '用户编号(默认0管理员)',
                                `learning_record` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学习记录',
                                `type` int NOT NULL COMMENT '学习类型（句子0/单词1/听力2）',
                                `selected` int DEFAULT '0' COMMENT '选中今天的复习内容',
                                `already_reviewed` int DEFAULT '0' COMMENT '今日已经复习',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习清单';

-- 建立随身记录表
-- TODO 数据库操作：添加用户id的外键
-- TODO 非数据库操作：提供按钮tab选择添加的是句子还是单词
DROP TABLE IF EXISTS `jottings`;
CREATE TABLE `jottings` (
                            `id` int NOT NULL AUTO_INCREMENT COMMENT '日常学习编号',
                            `user_id` int NOT NULL DEFAULT 0 COMMENT '用户编号(默认0管理员)',
                            `word` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英语单词',
                            `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
                            `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文',
                            `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录类型（单词/句子）',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `reviewed` tinyint(1) DEFAULT '0' COMMENT '已经复习',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='随身记录积累表';

-- 建立句子学习表
-- TODO 数据库操作：添加用户id的外键；添加触发器：当'is_grasp'字段从2变为1时，向二次复习表’sentence_error_review‘中添加该数据，添加时’id‘、’is_grasp'、‘error_time’、‘create_time’字段不添加，保持默认即可
-- TODO 非数据库操作：提供创建/添加句子的功能
DROP TABLE IF EXISTS `english_sentence01`;
CREATE TABLE `english_sentence01` (
                               `id` int NOT NULL AUTO_INCREMENT COMMENT '句子编号',
                               `user_id` int NOT NULL default 0 COMMENT '用户编号(默认管理员0)',
                               `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
                               `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文翻译',
                               `is_grasp` int(1) DEFAULT '0' COMMENT '是否掌握(1掌握，0未掌握，2错词，默认0)',
                               `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=527 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='英语句子表';


-- 建立错误句子二次复习表
-- TODO 添加用户id的外键
DROP TABLE IF EXISTS `sentence_error_review`;
CREATE TABLE `sentence_error_review` (
                                      `id` int NOT NULL AUTO_INCREMENT COMMENT '句子编号',
                                      `user_id` int NOT NULL default 0 COMMENT '用户编号(默认管理员0)',
                                      `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
                                      `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文翻译',
                                      `is_grasp` int(1) DEFAULT '0' COMMENT '是否掌握(1掌握，0未掌握，默认0)',
                                      `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=527 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错误句子二次复习表';


-- 建立鼓励文表（鸡汤文）
CREATE TABLE `motivation_quotes` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `content` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文案内容',
                                     `author` varchar(120) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '署名',
                                     `tag` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主题标签',
                                     `priority` int NOT NULL DEFAULT '0' COMMENT '权重，越大越优先',
                                     `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用 0-停用',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_motivation_status` (`status`),
                                     KEY `idx_motivation_priority` (`priority`)
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习激励鸡汤文';


-- 建立词本表
-- TODO 非数据库操作：管理员可以添加几本书，作为系统内置的图书
CREATE TABLE `user_books` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `user_id` bigint NOT NULL DEFAULT 0 COMMENT '所属用户(默认管理员0)',
                              `book_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课本名称',
                              `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '有点懒哦，还没给我添加介绍' COMMENT '简介',
                              `cover_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT 'https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/word_cover.jpg' COMMENT '封面地址',
                              `word_count` int DEFAULT NULL COMMENT '预计词汇量',
                              `visibility` enum('PRIVATE','PUBLIC') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PRIVATE' COMMENT '可见范围',
                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-启用 0-禁用',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_user_book_name` (`user_id`,`book_name`),
                              KEY `idx_user_books_user_id` (`user_id`),
                              CONSTRAINT `fk_user_books_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户自定义课本表';


-- 建立用户表
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
                         `password` varchar(300) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码(加密)',
                         `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                         `nickname` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
                         `avatar` varchar(500) COLLATE utf8mb4_general_ci DEFAULT 'https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/chenhaoxing.jpg' COMMENT '头像URL',
                         `role` enum('USER','ADMIN') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER' COMMENT '用户角色',
                         `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1:正常 0:禁用',
                         `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_username` (`username`),
                         KEY `idx_email` (`email`),
                         KEY `idx_role` (`role`),
                         KEY `idx_status` (`status`),
                         KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

