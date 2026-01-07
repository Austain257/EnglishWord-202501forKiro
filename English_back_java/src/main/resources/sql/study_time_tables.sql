-- 学习时长统计功能数据表创建脚本
-- 创建时间: 2026-01-07
-- 说明: 实现用户学习时长统计，包括会话记录、每日统计和累计统计

-- =============================================================================
-- 1. 汇总表（依赖触发器，先建）
-- =============================================================================

-- 用户每日学习时长统计表
DROP TABLE IF EXISTS `user_study_daily`;
CREATE TABLE `user_study_daily` (
    `user_id`      BIGINT      NOT NULL COMMENT '用户ID',
    `stat_date`    DATE        NOT NULL COMMENT '统计日期',
    `total_sec`    INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '当日累计秒数',
    `updated_at`   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
    PRIMARY KEY (`user_id`, `stat_date`),
    KEY `idx_user_study_daily_updated` (`updated_at`),
    CONSTRAINT `fk_user_study_daily_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日学习时长';

-- 用户学习总时长统计表
DROP TABLE IF EXISTS `user_study_summary`;
CREATE TABLE `user_study_summary` (
    `user_id`    BIGINT        NOT NULL COMMENT '用户ID',
    `total_sec`  BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '累计学习秒数',
    `updated_at` DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                              ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_user_study_summary_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户学习总时长';

-- =============================================================================
-- 2. 会话表 `user_study_session`
-- =============================================================================

DROP TABLE IF EXISTS `user_study_session`;
CREATE TABLE `user_study_session` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`         BIGINT        NOT NULL COMMENT '用户ID',
    `book_id`         BIGINT        NOT NULL COMMENT '当前课本ID',
    `study_scene`     VARCHAR(64)   NOT NULL COMMENT '学习场景编码（路由/模式）',
    `start_time`      DATETIME(3)   NOT NULL COMMENT '会话开始时间',
    `end_time`        DATETIME(3)            DEFAULT NULL COMMENT '会话结束时间',
    `duration_sec`    INT UNSIGNED           DEFAULT NULL COMMENT '会话总秒数',
    `status`          TINYINT       NOT NULL DEFAULT 0 COMMENT '0进行中 1已结束 2强制结束',
    `last_heartbeat`  DATETIME(3)            DEFAULT NULL COMMENT '最近心跳时间',
    `source`          VARCHAR(32)            DEFAULT 'ROUTE' COMMENT '触发来源（路由/组件/自动）',
    `client_meta`     JSON                   DEFAULT NULL COMMENT '前端上报细节',
    `created_at`      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at`      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                   ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_study_session_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_study_session_book`
        FOREIGN KEY (`book_id`) REFERENCES `user_books`(`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_duration_non_negative`
        CHECK (`duration_sec` IS NULL OR `duration_sec` >= 0),

    KEY `idx_session_user_start` (`user_id`, `start_time`),
    KEY `idx_session_status` (`status`, `last_heartbeat`),
    KEY `idx_session_scene` (`study_scene`, `user_id`),
    KEY `idx_session_book` (`book_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习会话表';

-- =============================================================================
-- 3. 触发器
-- =============================================================================

-- 3.1 防止并发会话 & 初始化心跳
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_user_study_session_bi`$$
CREATE TRIGGER `trg_user_study_session_bi`
BEFORE INSERT ON `user_study_session`
FOR EACH ROW
BEGIN
    IF NEW.status = 0 THEN
        -- 检查是否已有运行中的会话
        IF EXISTS (SELECT 1 FROM `user_study_session`
                   WHERE user_id = NEW.user_id AND status = 0) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '已有进行中的学习会话，需先结束后再创建';
        END IF;
        -- 初始化心跳时间
        IF NEW.last_heartbeat IS NULL THEN
            SET NEW.last_heartbeat = NEW.start_time;
        END IF;
    END IF;
END$$
DELIMITER ;

-- 3.2 更新时保持单活
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_user_study_session_bu`$$
CREATE TRIGGER `trg_user_study_session_bu`
BEFORE UPDATE ON `user_study_session`
FOR EACH ROW
BEGIN
    IF NEW.status = 0 THEN
        -- 检查是否有其他运行中的会话
        IF EXISTS (SELECT 1 FROM `user_study_session`
                   WHERE user_id = NEW.user_id AND status = 0 AND id <> OLD.id) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '该用户已有其他进行中的学习会话';
        END IF;
        -- 确保心跳时间不为空
        IF NEW.last_heartbeat IS NULL THEN
            SET NEW.last_heartbeat = NEW.start_time;
        END IF;
    END IF;
END$$
DELIMITER ;

-- 3.3 会话结束后自动汇总
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_user_study_session_au`$$
CREATE TRIGGER `trg_user_study_session_au`
AFTER UPDATE ON `user_study_session`
FOR EACH ROW
BEGIN
    -- 当会话状态从非结束变为已结束，且有有效时长时，更新统计数据
    IF OLD.status <> 1 AND NEW.status = 1 AND NEW.duration_sec IS NOT NULL AND NEW.duration_sec > 0 THEN
        -- 更新每日统计
        INSERT INTO `user_study_daily` (`user_id`, `stat_date`, `total_sec`, `updated_at`)
        VALUES (NEW.user_id, DATE(NEW.end_time), NEW.duration_sec, NOW(3))
        ON DUPLICATE KEY UPDATE
            `total_sec` = `total_sec` + NEW.duration_sec,
            `updated_at` = NOW(3);

        -- 更新累计总时长
        INSERT INTO `user_study_summary` (`user_id`, `total_sec`, `updated_at`)
        VALUES (NEW.user_id, NEW.duration_sec, NOW(3))
        ON DUPLICATE KEY UPDATE
            `total_sec` = `total_sec` + NEW.duration_sec,
            `updated_at` = NOW(3);
    END IF;
END$$
DELIMITER ;

-- =============================================================================
-- 4. 初始化数据和索引优化
-- =============================================================================

-- 添加说明注释
ALTER TABLE `user_study_session` COMMENT = '学习会话表 - 记录每次学习会话的详细信息，包括开始时间、结束时间、时长等';
ALTER TABLE `user_study_daily` COMMENT = '用户每日学习时长 - 按日汇总用户学习时长，用于快速查询今日学习数据';
ALTER TABLE `user_study_summary` COMMENT = '用户学习总时长 - 用户累计学习时长统计，用于快速查询总体学习数据';

-- 创建完成提示
SELECT '学习时长统计功能数据表创建完成！' AS message;
SELECT '包含表：user_study_session (会话表)、user_study_daily (每日统计)、user_study_summary (累计统计)' AS tables;
SELECT '包含触发器：防止并发会话、自动统计汇总' AS triggers;
