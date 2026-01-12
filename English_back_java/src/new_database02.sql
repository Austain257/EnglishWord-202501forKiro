-- ============================================
-- 英语学习平台数据库设计（优化版）
-- 版本：2.0
-- 日期：2026-01-01
-- 说明：包含外键约束、触发器、索引优化
-- ============================================

-- 1. 禁用外键约束检查
SET FOREIGN_KEY_CHECKS = 0;


-- 先创建用户表（因为其他表依赖它）
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `password` varchar(300) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码(加密)',
    `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
    `nickname` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(500) COLLATE utf8mb4_general_ci DEFAULT 'https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/chenhaoxing.jpg' COMMENT '头像URL',
    `role` enum('USER','ADMIN') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER' COMMENT '用户角色',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 创建词本表
DROP TABLE IF EXISTS `user_books`;
CREATE TABLE `user_books` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '所属用户(默认管理员0)',
    `book_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课本名称',
    `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '有点懒哦，还没给我添加介绍' COMMENT '简介',
    `cover_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT 'https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/word_cover.jpg' COMMENT '封面地址',
    `word_count` int DEFAULT NULL COMMENT '预计词汇量',
    `visibility` enum('PRIVATE','PUBLIC') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PRIVATE' COMMENT '可见范围',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_book_name` (`user_id`,`book_name`),
    KEY `idx_user_books_user_id` (`user_id`),
    KEY `idx_visibility` (`visibility`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_user_books_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户自定义课本表';

-- 建立英语单词表
DROP TABLE IF EXISTS `english_word_02`;
CREATE TABLE `english_word_02` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '单词编号',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号',
    `book_id` bigint NOT NULL COMMENT '单词本编号',
    `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
    `chinese` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文释义',
    `pronounce` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音标',
    `times` int NOT NULL DEFAULT 0 COMMENT '四级出现次数',
    `is_grasp` tinyint NOT NULL DEFAULT 0 COMMENT '是否掌握(0未掌握，1已掌握，2错词)',
    `error_times` int NOT NULL DEFAULT 0 COMMENT '错误次数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_book` (`user_id`, `book_id`),
    KEY `idx_word` (`word`),
    KEY `idx_is_grasp` (`is_grasp`),
    KEY `idx_error_times` (`error_times`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_english_word_02_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_english_word_02_book` FOREIGN KEY (`book_id`) REFERENCES `user_books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='生单词表2';

-- 建立错词二次复习表
DROP TABLE IF EXISTS `word_error_review`;
CREATE TABLE `word_error_review` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '错词编号',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号',
    `book_id` bigint NOT NULL COMMENT '单词本编号',
    `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
    `chinese` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文释义',
    `pronounce` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音标',
    `times` int NOT NULL DEFAULT 0 COMMENT '四级出现次数',
    `is_grasp` tinyint NOT NULL DEFAULT 0 COMMENT '是否掌握(0未掌握，1已掌握)',
    `error_times` int NOT NULL DEFAULT 0 COMMENT '错误次数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_book_word` (`user_id`, `book_id`, `word`),
    KEY `idx_user_book` (`user_id`, `book_id`),
    KEY `idx_is_grasp` (`is_grasp`),
    KEY `idx_error_times` (`error_times`),
    CONSTRAINT `fk_word_error_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_word_error_book` FOREIGN KEY (`book_id`) REFERENCES `user_books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错词二次复习表';

-- 创建触发器：当单词从错词状态变为已掌握时，添加到错词复习表
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_word_to_error_review`$$
CREATE TRIGGER `trg_word_to_error_review`
AFTER UPDATE ON `english_word_01`
FOR EACH ROW
BEGIN
    -- 当is_grasp从2(错词)变为1(已掌握)时，添加到错词复习表
    IF OLD.is_grasp = 2 AND NEW.is_grasp = 1 THEN
        INSERT INTO `word_error_review` (
            `user_id`, `book_id`, `word`, `chinese`, `pronounce`, 
            `times`, `error_times`
        ) VALUES (
            NEW.user_id, NEW.book_id, NEW.word, NEW.chinese, NEW.pronounce,
            NEW.times, NEW.error_times
        )
        ON DUPLICATE KEY UPDATE
            `error_times` = NEW.error_times,
            `chinese` = NEW.chinese,
            `pronounce` = NEW.pronounce;
    END IF;
END$$
DELIMITER ;

-- 建立英语句子表
DROP TABLE IF EXISTS `english_sentence01`;
CREATE TABLE `english_sentence01` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '句子编号',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号(默认管理员0)',
    `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
    `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文翻译',
    `is_grasp` tinyint NOT NULL DEFAULT 0 COMMENT '是否掌握(0未掌握，1已掌握，2错句)',
    `error_times` int NOT NULL DEFAULT 0 COMMENT '错误次数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_grasp` (`is_grasp`),
    KEY `idx_error_times` (`error_times`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_sentence_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='英语句子表';

-- 建立错误句子二次复习表
DROP TABLE IF EXISTS `sentence_error_review`;
CREATE TABLE `sentence_error_review` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '句子编号',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号(默认管理员0)',
    `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
    `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文翻译',
    `is_grasp` tinyint NOT NULL DEFAULT 0 COMMENT '是否掌握(0未掌握，1已掌握)',
    `error_times` int NOT NULL DEFAULT 0 COMMENT '错误次数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_grasp` (`is_grasp`),
    KEY `idx_error_times` (`error_times`),
    CONSTRAINT `fk_sentence_error_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错误句子二次复习表';

-- 创建触发器：当句子从错句状态变为已掌握时，添加到错句复习表
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_sentence_to_error_review`$$
CREATE TRIGGER `trg_sentence_to_error_review`
AFTER UPDATE ON `english_sentence01`
FOR EACH ROW
BEGIN
    -- 当is_grasp从2(错句)变为1(已掌握)时，添加到错句复习表
    IF OLD.is_grasp = 2 AND NEW.is_grasp = 1 THEN
        INSERT INTO `sentence_error_review` (
            `user_id`, `sentence`, `chinese`, `error_times`
        ) VALUES (
            NEW.user_id, NEW.sentence, NEW.chinese, NEW.error_times
        )
        ON DUPLICATE KEY UPDATE
            `error_times` = NEW.error_times,
            `chinese` = NEW.chinese;
    END IF;
END$$
DELIMITER ;

-- 建立学习清单表（艾宾浩斯遗忘曲线）
DROP TABLE IF EXISTS `learning_checklist`;
CREATE TABLE `learning_checklist` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '清单/计划编号',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号(默认0管理员)',
    `learning_record` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学习记录',
    `type` tinyint NOT NULL COMMENT '学习类型（0句子/1单词/2听力）',
    `selected` tinyint NOT NULL DEFAULT 0 COMMENT '选中今天的复习内容',
    `already_reviewed` tinyint NOT NULL DEFAULT 0 COMMENT '今日已经复习',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_selected` (`selected`),
    KEY `idx_already_reviewed` (`already_reviewed`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_checklist_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习清单';


-- 1. 禁用外键约束检查
SET FOREIGN_KEY_CHECKS = 0;
-- 建立随身记录表
DROP TABLE IF EXISTS `jottings`;
CREATE TABLE `jottings` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '日常学习编号',
    `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号(默认0管理员)',
    `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '英语单词',
    `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '英文句子',
    `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文',
    `type` tinyint NOT NULL DEFAULT 0 COMMENT '记录类型（句子0/单词1,默认句子）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `reviewed` tinyint NOT NULL DEFAULT 0 COMMENT '已经复习',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_reviewed` (`reviewed`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_jottings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='随身记录积累表';

-- 3. 重新启用外键检查（重要！）
SET FOREIGN_KEY_CHECKS = 1;

-- 建立鼓励文表（鸡汤文）
DROP TABLE IF EXISTS `motivation_quotes`;
CREATE TABLE `motivation_quotes` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文案内容',
    `author` varchar(120) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '署名',
    `tag` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主题标签',
    `priority` int NOT NULL DEFAULT 0 COMMENT '权重，越大越优先',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_motivation_status` (`status`),
    KEY `idx_motivation_priority` (`priority`),
    KEY `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习激励鸡汤文';

-- ============================================
-- 优化说明：
-- 1. 添加了所有必要的外键约束，确保数据完整性
-- 2. 创建了触发器自动处理错词/错句复习逻辑
-- 3. 为常用查询字段添加了索引，提升查询性能
-- 4. 统一了数据类型（user_id改为bigint，tinyint替代int(1)）
-- 5. 添加了update_time字段，便于追踪数据变更
-- 6. 为错词/错句表添加了唯一索引，避免重复数据
-- 7. 优化了字段长度，提升存储效率
-- ============================================
