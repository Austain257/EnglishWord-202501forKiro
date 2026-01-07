-- 修复学习会话防抖功能：添加暂停状态支持
-- 执行时间: 2026-01-07
-- 说明: 修复页面后台时继续计时的问题

-- 1. 为会话表添加暂停相关字段
ALTER TABLE `user_study_session` 
ADD COLUMN `paused_at` DATETIME(3) NULL COMMENT '暂停时间' AFTER `last_heartbeat`,
ADD COLUMN `pause_duration_sec` INT UNSIGNED DEFAULT 0 COMMENT '累计暂停秒数' AFTER `paused_at`,
ADD COLUMN `is_paused` TINYINT(1) DEFAULT 0 COMMENT '是否暂停中' AFTER `pause_duration_sec`;

-- 2. 添加暂停状态索引
ALTER TABLE `user_study_session` 
ADD INDEX `idx_session_paused` (`is_paused`, `paused_at`);

-- 3. 更新状态字段说明
ALTER TABLE `user_study_session` 
MODIFY COLUMN `status` TINYINT NOT NULL DEFAULT 0 
COMMENT '0=进行中 1=已结束 2=强制结束 3=暂停中';

-- 4. 修改触发器：会话结束时扣除暂停时长
DROP TRIGGER IF EXISTS `trg_user_study_session_au`;

DELIMITER $$
CREATE TRIGGER `trg_user_study_session_au`
AFTER UPDATE ON `user_study_session`
FOR EACH ROW
BEGIN
    -- 当会话状态从非结束变为已结束，且有有效时长时，更新统计数据
    IF OLD.status <> 1 AND NEW.status = 1 AND NEW.duration_sec IS NOT NULL AND NEW.duration_sec > 0 THEN
        -- 计算实际学习时长（总时长 - 暂停时长）
        SET @actual_study_sec = NEW.duration_sec - IFNULL(NEW.pause_duration_sec, 0);
        
        -- 只有实际学习时长 > 0 才更新统计
        IF @actual_study_sec > 0 THEN
            -- 更新每日统计
            INSERT INTO `user_study_daily` (`user_id`, `stat_date`, `total_sec`, `updated_at`)
            VALUES (NEW.user_id, DATE(NEW.end_time), @actual_study_sec, NOW(3))
            ON DUPLICATE KEY UPDATE
                `total_sec` = `total_sec` + @actual_study_sec,
                `updated_at` = NOW(3);

            -- 更新累计总时长
            INSERT INTO `user_study_summary` (`user_id`, `total_sec`, `updated_at`)
            VALUES (NEW.user_id, @actual_study_sec, NOW(3))
            ON DUPLICATE KEY UPDATE
                `total_sec` = `total_sec` + @actual_study_sec,
                `updated_at` = NOW(3);
        END IF;
    END IF;
END$$
DELIMITER ;

-- 5. 创建自动关闭超时会话的存储过程
DROP PROCEDURE IF EXISTS `sp_auto_close_timeout_sessions`;

DELIMITER $$
CREATE PROCEDURE `sp_auto_close_timeout_sessions`(
    IN timeout_minutes INT DEFAULT 30
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE session_id BIGINT;
    DECLARE session_start DATETIME(3);
    DECLARE session_heartbeat DATETIME(3);
    DECLARE session_paused_at DATETIME(3);
    DECLARE session_pause_duration INT;
    DECLARE total_duration INT;
    
    -- 游标：查找超时的活跃会话
    DECLARE timeout_cursor CURSOR FOR
        SELECT id, start_time, last_heartbeat, paused_at, IFNULL(pause_duration_sec, 0)
        FROM `user_study_session`
        WHERE status = 0 -- 进行中
        AND (
            -- 未暂停且心跳超时
            (is_paused = 0 AND last_heartbeat < DATE_SUB(NOW(3), INTERVAL timeout_minutes MINUTE))
            OR
            -- 暂停超过2小时
            (is_paused = 1 AND paused_at < DATE_SUB(NOW(3), INTERVAL 120 MINUTE))
        );
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    START TRANSACTION;
    
    OPEN timeout_cursor;
    
    timeout_loop: LOOP
        FETCH timeout_cursor INTO session_id, session_start, session_heartbeat, session_paused_at, session_pause_duration;
        
        IF done THEN
            LEAVE timeout_loop;
        END IF;
        
        -- 计算总时长（从开始到最后心跳/暂停时间）
        IF session_paused_at IS NOT NULL THEN
            SET total_duration = TIMESTAMPDIFF(SECOND, session_start, session_paused_at);
        ELSE
            SET total_duration = TIMESTAMPDIFF(SECOND, session_start, session_heartbeat);
        END IF;
        
        -- 更新会话状态为强制结束
        UPDATE `user_study_session`
        SET 
            status = 2, -- 强制结束
            end_time = NOW(3),
            duration_sec = total_duration,
            updated_at = NOW(3)
        WHERE id = session_id;
        
    END LOOP;
    
    CLOSE timeout_cursor;
    
    COMMIT;
    
    -- 返回处理的会话数量
    SELECT ROW_COUNT() as closed_sessions;
END$$
DELIMITER ;

SELECT '学习会话防抖功能修复完成！' AS message;
SELECT '新增功能：暂停状态支持、自动超时关闭、实际学习时长计算' AS features;
