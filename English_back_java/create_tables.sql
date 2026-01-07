-- 检查并创建学习时长统计相关的数据表
-- 数据库: english_for_kiro

USE english_for_kiro;

-- 检查现有表
SHOW TABLES LIKE 'user_study%';

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
    KEY `idx_user_study_daily_updated` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日学习时长';

-- 用户学习总时长统计表
DROP TABLE IF EXISTS `user_study_summary`;
CREATE TABLE `user_study_summary` (
    `user_id`    BIGINT        NOT NULL COMMENT '用户ID',
    `total_sec`  BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '累计学习秒数',
    `updated_at` DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                              ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
    PRIMARY KEY (`user_id`)
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

-- 显示创建的表
SHOW TABLES LIKE 'user_study%';

SELECT 'Database tables created successfully!' AS status;
