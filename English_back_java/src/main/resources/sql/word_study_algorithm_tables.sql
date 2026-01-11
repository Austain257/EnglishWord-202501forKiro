-- 英语学习算法核心数据表创建脚本
-- 创建日期: 2026-01-09

USE english_for_kiro;

-- =============================================================================
-- 1. 单词学习记录表 (核心表)
-- =============================================================================

DROP TABLE IF EXISTS `word_study_record`;
CREATE TABLE `word_study_record` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，自增。唯一标识每次学习记录',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id，外键，关联用户表，标识是哪个用户的学习记录',
  `book_id` bigint UNSIGNED NOT NULL COMMENT '课本id，外键，关联课本表，标识所学范围所属课本',
  `start_time` datetime NOT NULL COMMENT '开始学习时间（用户点击"开始学习"时记录的开始时间）',
  `start_id` int UNSIGNED NOT NULL COMMENT '本轮学习单词范围的起始ID（计时开始时记录）',
  `end_time` datetime DEFAULT NULL COMMENT '实际结束时间（用户点击"结束学习"时记录的结束时间）',
  `end_id` int UNSIGNED DEFAULT NULL COMMENT '本轮学习单词范围的结束ID（计时结束时记录）',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '会话状态：1-进行中，0-正常结束，4-异常退出。用于容错',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录添加数据的时间，数据库默认当前时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的最后一次修改时间修改时间',
  `round_1_review_time` datetime DEFAULT NULL COMMENT '第一轮复习结束的时间（默认值null，表示未复习）',
  `round_2_review_time` datetime DEFAULT NULL COMMENT '第二轮复习结束的时间（默认值null，表示未复习）',
  `round_3_review_time` datetime DEFAULT NULL COMMENT '第三轮复习结束的时间（默认值null，表示未复习）',
  `round_4_review_time` datetime DEFAULT NULL COMMENT '第四轮复习结束的时间（默认值null，表示未复习）',
  `round_5_review_time` datetime DEFAULT NULL COMMENT '第五轮复习结束的时间（默认值null，表示未复习）',
  `round_6_review_time` datetime DEFAULT NULL COMMENT '第六轮复习结束的时间（默认值null，表示未复习）',
  `round_7_review_time` datetime DEFAULT NULL COMMENT '第七轮复习结束的时间（默认值null，表示未复习）',
  `round_8_review_time` datetime DEFAULT NULL COMMENT '第八轮复习结束的时间（默认值null，表示未复习）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户单词学习记录表';

-- =============================================================================
-- 2. 每日学习清单自动生成存储过程
-- =============================================================================

-- 创建每日学习清单生成存储过程
DELIMITER $$
DROP PROCEDURE IF EXISTS GenerateDailyLearningChecklist$$
CREATE PROCEDURE GenerateDailyLearningChecklist()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_user_id BIGINT;
    DECLARE v_book_id BIGINT;
    DECLARE v_book_name VARCHAR(120);
    DECLARE v_min_start_id INT;
    DECLARE v_max_end_id INT;
    DECLARE v_yesterday DATE;
    
    -- 游标声明（按用户+课本维度）
    DECLARE user_cursor CURSOR FOR 
        SELECT DISTINCT user_id, book_id 
        FROM word_study_record 
        WHERE DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
        AND status = 0;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    SET v_yesterday = DATE_SUB(CURDATE(), INTERVAL 1 DAY);
    
    OPEN user_cursor;
    
    read_loop: LOOP
        FETCH user_cursor INTO v_user_id, v_book_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 获取该用户在指定课本下昨天的学习范围
        SELECT MIN(start_id), MAX(end_id) 
        INTO v_min_start_id, v_max_end_id
        FROM word_study_record 
        WHERE user_id = v_user_id 
        AND book_id = v_book_id
        AND DATE(create_time) = v_yesterday
        AND status = 0;
        
        -- 查询课本名称
        SELECT book_name INTO v_book_name
        FROM user_books 
        WHERE id = v_book_id 
        LIMIT 1;
        
        -- 插入学习清单
        IF v_min_start_id IS NOT NULL AND v_max_end_id IS NOT NULL THEN
            INSERT INTO learning_checklist (
                user_id, 
                learning_record, 
                type, 
                selected, 
                already_reviewed, 
                create_time, 
                update_time
            ) VALUES (
                v_user_id,
                CONCAT('学习新单词：【', IFNULL(v_book_name, '未命名课本'), '】的', v_min_start_id, '到', v_max_end_id, '号单词'),
                1,
                0,
                0,
                DATE_ADD(v_yesterday, INTERVAL 15 HOUR),
                NOW()
            );
        END IF;
        
    END LOOP;
    
    CLOSE user_cursor;
END$$
DELIMITER ;

-- 显示创建结果
SELECT 'word_study_record表和存储过程创建成功!' AS status;
