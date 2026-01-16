-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: english_for_kiro
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `english_sentence01`
--

DROP TABLE IF EXISTS `english_sentence01`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `english_sentence01` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '句子编号',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号(默认管理员0)',
  `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
  `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文翻译',
  `is_grasp` tinyint NOT NULL DEFAULT '0' COMMENT '是否掌握(0未掌握，1已掌握，2错句)',
  `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_grasp` (`is_grasp`),
  KEY `idx_error_times` (`error_times`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_sentence_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='英语句子表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_sentence_to_error_review */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_sentence_to_error_review` AFTER UPDATE ON `english_sentence01` FOR EACH ROW BEGIN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `english_word_01`
--

DROP TABLE IF EXISTS `english_word_01`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `english_word_01` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '单词编号',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号',
  `book_id` bigint NOT NULL COMMENT '单词本编号',
  `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
  `chinese` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文释义',
  `pronounce` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音标',
  `times` int NOT NULL DEFAULT '0' COMMENT '四级出现次数',
  `is_grasp` tinyint NOT NULL DEFAULT '0' COMMENT '是否掌握(0未掌握，1已掌握，2错词)',
  `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_book` (`user_id`,`book_id`),
  KEY `idx_word` (`word`),
  KEY `idx_is_grasp` (`is_grasp`),
  KEY `idx_error_times` (`error_times`),
  KEY `idx_create_time` (`create_time`),
  KEY `fk_english_word_book` (`book_id`),
  CONSTRAINT `fk_english_word_book` FOREIGN KEY (`book_id`) REFERENCES `user_books` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_english_word_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='生单词表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_word_to_error_review */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_word_to_error_review` AFTER UPDATE ON `english_word_01` FOR EACH ROW BEGIN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `jottings`
--

DROP TABLE IF EXISTS `jottings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jottings` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '日常学习编号',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号(默认0管理员)',
  `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '英语单词',
  `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '英文句子',
  `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '记录类型（句子0/单词1,默认句子）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reviewed` tinyint NOT NULL DEFAULT '0' COMMENT '已经复习',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_reviewed` (`reviewed`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_jottings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='随身记录积累表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `learning_checklist`
--

DROP TABLE IF EXISTS `learning_checklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_checklist` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '清单/计划编号',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号(默认0管理员)',
  `book_id` bigint NOT NULL COMMENT '课本ID',
  `start_id` int unsigned NOT NULL COMMENT '起始单词ID',
  `end_id` int unsigned NOT NULL COMMENT '结束单词ID',
  `record_ids` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联的学习记录ID列表（逗号分隔）',
  `learning_record` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学习记录',
  `type` tinyint NOT NULL COMMENT '学习类型（0句子/1单词/2听力）',
  `selected` tinyint NOT NULL DEFAULT '0' COMMENT '选中今天的复习内容',
  `already_reviewed` tinyint NOT NULL DEFAULT '0' COMMENT '今日已经复习',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `motivation_quotes`
--

DROP TABLE IF EXISTS `motivation_quotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `motivation_quotes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文案内容',
  `author` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '署名',
  `tag` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主题标签',
  `priority` int NOT NULL DEFAULT '0' COMMENT '权重，越大越优先',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用 0-停用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_motivation_status` (`status`),
  KEY `idx_motivation_priority` (`priority`),
  KEY `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习激励鸡汤文';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sentence_error_review`
--

DROP TABLE IF EXISTS `sentence_error_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sentence_error_review` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '句子编号',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号(默认管理员0)',
  `sentence` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文句子',
  `chinese` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文翻译',
  `is_grasp` tinyint NOT NULL DEFAULT '0' COMMENT '是否掌握(0未掌握，1已掌握)',
  `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_grasp` (`is_grasp`),
  KEY `idx_error_times` (`error_times`),
  CONSTRAINT `fk_sentence_error_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错误句子二次复习表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_books`
--

DROP TABLE IF EXISTS `user_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_books` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '所属用户(默认管理员0)',
  `book_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课本名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '有点懒哦，还没给我添加介绍' COMMENT '简介',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/word_cover.jpg' COMMENT '封面地址',
  `word_count` int DEFAULT NULL COMMENT '预计词汇量',
  `visibility` enum('PRIVATE','PUBLIC') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PRIVATE' COMMENT '可见范围',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-启用 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book_name` (`user_id`,`book_name`),
  KEY `idx_user_books_user_id` (`user_id`),
  KEY `idx_visibility` (`visibility`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_user_books_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户自定义课本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_study_daily`
--

DROP TABLE IF EXISTS `user_study_daily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_study_daily` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_sec` int unsigned NOT NULL DEFAULT '0' COMMENT '当日累计秒数',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
  PRIMARY KEY (`user_id`,`stat_date`),
  KEY `idx_user_study_daily_updated` (`updated_at`),
  CONSTRAINT `fk_user_study_daily_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户每日学习时长';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_study_session`
--

DROP TABLE IF EXISTS `user_study_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_study_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '当前课本ID',
  `study_scene` varchar(64) NOT NULL COMMENT '学习场景编码（路由/模式）',
  `start_time` datetime(3) NOT NULL COMMENT '会话开始时间',
  `end_time` datetime(3) DEFAULT NULL COMMENT '会话结束时间',
  `duration_sec` int unsigned DEFAULT NULL COMMENT '会话总秒数',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0进行中 1已结束 2强制结束',
  `last_heartbeat` datetime(3) DEFAULT NULL COMMENT '最近心跳时间',
  `paused_at` datetime(3) DEFAULT NULL COMMENT 'pause time',
  `pause_duration_sec` int unsigned DEFAULT '0' COMMENT 'total pause seconds',
  `is_paused` tinyint(1) DEFAULT '0' COMMENT 'is paused',
  `source` varchar(32) DEFAULT 'ROUTE' COMMENT '触发来源（路由/组件/自动）',
  `client_meta` json DEFAULT NULL COMMENT '前端上报细节',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_user_start` (`user_id`,`start_time`),
  KEY `idx_session_status` (`status`,`last_heartbeat`),
  KEY `idx_session_scene` (`study_scene`,`user_id`),
  KEY `idx_session_book` (`book_id`,`status`),
  CONSTRAINT `fk_study_session_book` FOREIGN KEY (`book_id`) REFERENCES `user_books` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_study_session_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_duration_non_negative` CHECK (((`duration_sec` is null) or (`duration_sec` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学习会话表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_user_study_session_bi */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_user_study_session_bi` BEFORE INSERT ON `user_study_session` FOR EACH ROW BEGIN
    IF NEW.status = 0 THEN
        IF EXISTS (SELECT 1 FROM `user_study_session`
                   WHERE user_id = NEW.user_id AND status = 0) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '已有进行中的学习会话，需先结束后再创建';
        END IF;
        IF NEW.last_heartbeat IS NULL THEN
            SET NEW.last_heartbeat = NEW.start_time;
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_user_study_session_bu */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_user_study_session_bu` BEFORE UPDATE ON `user_study_session` FOR EACH ROW BEGIN
    IF NEW.status = 0 THEN
        IF EXISTS (SELECT 1 FROM `user_study_session`
                   WHERE user_id = NEW.user_id AND status = 0 AND id <> OLD.id) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '该用户已有其他进行中的学习会话';
        END IF;
        IF NEW.last_heartbeat IS NULL THEN
            SET NEW.last_heartbeat = NEW.start_time;
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_user_study_session_au */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_user_study_session_au` AFTER UPDATE ON `user_study_session` FOR EACH ROW BEGIN
    IF OLD.status <> 1 AND NEW.status = 1 AND NEW.duration_sec IS NOT NULL THEN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user_study_summary`
--

DROP TABLE IF EXISTS `user_study_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_study_summary` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_sec` bigint unsigned NOT NULL DEFAULT '0' COMMENT '累计学习秒数',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_study_summary_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户学习总时长';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码(加密)',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/chenhaoxing.jpg' COMMENT '头像URL',
  `role` enum('USER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER' COMMENT '用户角色',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1:正常 0:禁用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `learning_goal` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学习目标',
  `daily_target` int DEFAULT NULL COMMENT '每日目标',
  `reminder_enabled` tinyint NOT NULL DEFAULT '1' COMMENT '学习提醒开关 1开0关',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `word_error_review`
--

DROP TABLE IF EXISTS `word_error_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `word_error_review` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '错词编号',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号',
  `book_id` bigint NOT NULL COMMENT '单词本编号',
  `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
  `chinese` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文释义',
  `pronounce` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音标',
  `times` int NOT NULL DEFAULT '0' COMMENT '四级出现次数',
  `is_grasp` tinyint NOT NULL DEFAULT '0' COMMENT '是否掌握(0未掌握，1已掌握)',
  `error_times` int NOT NULL DEFAULT '0' COMMENT '错误次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book_word` (`user_id`,`book_id`,`word`),
  KEY `idx_user_book` (`user_id`,`book_id`),
  KEY `idx_is_grasp` (`is_grasp`),
  KEY `idx_error_times` (`error_times`),
  KEY `fk_word_error_book` (`book_id`),
  CONSTRAINT `fk_word_error_book` FOREIGN KEY (`book_id`) REFERENCES `user_books` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_word_error_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错词二次复习表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `word_study_record`
--

DROP TABLE IF EXISTS `word_study_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `word_study_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键，自增。唯一标识每次学习记录',
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `start_time` datetime NOT NULL COMMENT '开始学习时间（用户点击"开始学习"时记录的开始时间）',
  `start_id` int unsigned NOT NULL COMMENT '本轮学习单词范围的起始ID（计时开始时记录）',
  `end_time` datetime NOT NULL COMMENT '实际结束时间（用户点击"结束学习"时记录的结束时间）',
  `end_id` int unsigned NOT NULL COMMENT '本轮学习单词范围的结束ID（计时结束时记录）',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '会话状态：1-进行中，0-正常结束，4-异常退出。用于容错',
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
  `round_9_review_time` datetime DEFAULT NULL COMMENT '第九轮复习结束的时间（默认值null，表示未复习）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_status` (`status`),
  KEY `fk_word_study_book_id` (`book_id`),
  CONSTRAINT `fk_word_study_book_id` FOREIGN KEY (`book_id`) REFERENCES `user_books` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_word_study_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户单词学习记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'english_for_kiro'
--
/*!50003 DROP PROCEDURE IF EXISTS `GenerateDailyLearningChecklist` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
DROP PROCEDURE IF EXISTS `GenerateDailyLearningChecklist`;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GenerateDailyLearningChecklist`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_user_id BIGINT;
    DECLARE v_book_id BIGINT;
    DECLARE v_book_name VARCHAR(120);
    DECLARE v_min_start_id INT;
    DECLARE v_max_end_id INT;
    DECLARE v_record_ids TEXT;
    DECLARE v_yesterday DATE;

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

        SELECT
            MIN(start_id),
            MAX(end_id),
            GROUP_CONCAT(id ORDER BY id SEPARATOR ',')
        INTO
            v_min_start_id,
            v_max_end_id,
            v_record_ids
        FROM word_study_record
        WHERE user_id = v_user_id
          AND book_id = v_book_id
          AND DATE(create_time) = v_yesterday
          AND status = 0;

        SELECT book_name INTO v_book_name
        FROM user_books
        WHERE id = v_book_id
        LIMIT 1;

        IF v_min_start_id IS NOT NULL AND v_max_end_id IS NOT NULL THEN
            IF NOT EXISTS (
                SELECT 1 FROM learning_checklist lc
                WHERE lc.user_id = v_user_id
                  AND lc.book_id = v_book_id
                  AND lc.start_id = v_min_start_id
                  AND lc.end_id = v_max_end_id
                  AND lc.type = 1   -- 单词
            ) THEN
                INSERT INTO learning_checklist (
                    user_id,
                    book_id,
                    start_id,
                    end_id,
                    record_ids,
                    learning_record,
                    type,
                    selected,
                    already_reviewed,
                    create_time,
                    update_time
                ) VALUES (
                    v_user_id,
                    v_book_id,
                    v_min_start_id,
                    v_max_end_id,
                    v_record_ids,
                    CONCAT('学习新单词：【', IFNULL(v_book_name, '未命名课本'), '】的', v_min_start_id, '到', v_max_end_id, '号单词'),
                    1,
                    0,
                    0,
                    DATE_ADD(v_yesterday, INTERVAL 15 HOUR),
                    NOW()
                );
            END IF;
        END IF;
    END LOOP;

    CLOSE user_cursor;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-14 18:12:21

-- 根据用户id合并对应课本book_id当天记录，返回受影响行数----------------------------------------------------

DELIMITER ;;

DROP PROCEDURE IF EXISTS `GenerateDailyLearningChecklistWordByUserId`;

CREATE DEFINER=`root`@`localhost` PROCEDURE `GenerateDailyLearningChecklistWordByUserId`(
    IN p_user_id BIGINT,
    IN p_book_id BIGINT
)
BEGIN
    DECLARE v_book_name VARCHAR(120);
    DECLARE v_min_start_id INT;
    DECLARE v_max_end_id INT;
    DECLARE v_record_ids TEXT;
    DECLARE v_today DATE;
    DECLARE v_affected_rows INT DEFAULT 0;

    -- 设置为当天日期
    SET v_today = CURDATE();

    -- 获取当天学习记录的最小开始ID、最大结束ID和所有记录ID
    SELECT
        MIN(start_id),
        MAX(end_id),
        GROUP_CONCAT(id ORDER BY id SEPARATOR ',')
    INTO
        v_min_start_id,
        v_max_end_id,
        v_record_ids
    FROM word_study_record
    WHERE user_id = p_user_id
      AND book_id = p_book_id
      AND DATE(create_time) = v_today
      AND status = 0;

    -- 获取书籍名称
    SELECT book_name INTO v_book_name
    FROM user_books
    WHERE id = p_book_id
    LIMIT 1;

    -- 只有当存在有效记录时才插入学习清单
    IF v_min_start_id IS NOT NULL AND v_max_end_id IS NOT NULL THEN
        -- 检查是否已存在相同的学习清单记录
        IF NOT EXISTS (
            SELECT 1 FROM learning_checklist lc
            WHERE lc.user_id = p_user_id
              AND lc.book_id = p_book_id
              AND lc.start_id = v_min_start_id
              AND lc.end_id = v_max_end_id
              AND lc.type = 1   -- 单词类型
              AND DATE(lc.create_time) = v_today  -- 确保是当天的记录
        ) THEN
            INSERT INTO learning_checklist (
                user_id,
                book_id,
                start_id,
                end_id,
                record_ids,
                learning_record,
                type,
                selected,
                already_reviewed,
                create_time,
                update_time
            ) VALUES (
                p_user_id,
                p_book_id,
                v_min_start_id,
                v_max_end_id,
                v_record_ids,
                CONCAT('学习新单词：【', IFNULL(v_book_name, '未命名课本'), '】的',
                       v_min_start_id, '到', v_max_end_id, '号单词'),
                1,    -- 类型：单词
                0,    -- 是否选中
                0,    -- 是否已复习
                NOW(), -- 创建时间为当前时间
                NOW()  -- 更新时间为当前时间
            );

            -- 返回插入的影响行数
            SELECT ROW_COUNT() AS result;
        ELSE
            -- 记录已存在，返回0
            SELECT 0 AS result;
        END IF;
    ELSE
        -- 没有找到有效记录，返回0
        SELECT -1 AS result;
    END IF;

END ;;

DELIMITER ;


-- ---------------------------------------




--
-- Table structure for table `sentence_study_record`
--

DROP TABLE IF EXISTS `sentence_study_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sentence_study_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键，自增。唯一标识每次学习记录',
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `start_time` datetime NOT NULL COMMENT '开始学习时间（用户点击"开始学习"时记录的开始时间）',
  `start_id` int unsigned NOT NULL COMMENT '本轮学习单词范围的起始ID（计时开始时记录）',
  `end_time` datetime NOT NULL COMMENT '实际结束时间（用户点击"结束学习"时记录的结束时间）',
  `end_id` int unsigned NOT NULL COMMENT '本轮学习单词范围的结束ID（计时结束时记录）',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '会话状态：1-进行中，0-正常结束，4-异常退出。用于容错',
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
  `round_9_review_time` datetime DEFAULT NULL COMMENT '第九轮复习结束的时间（默认值null，表示未复习）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_sentence_study_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户句子学习记录表';
/*!40101 SET character_set_client = @saved_cs_client */;


-- 生成每日学习记录并插入清单表

DELIMITER ;;
DROP PROCEDURE IF EXISTS `GenerateDailyLearningChecklistForSentence`;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GenerateDailyLearningChecklistForSentence`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_user_id BIGINT;
    DECLARE v_book_id BIGINT;
    DECLARE v_min_start_id INT;
    DECLARE v_max_end_id INT;
    DECLARE v_record_ids TEXT;
    DECLARE v_yesterday DATE;

    DECLARE user_cursor CURSOR FOR
        SELECT DISTINCT user_id, book_id
        FROM sentence_study_record
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

        SELECT
            MIN(start_id),
            MAX(end_id),
            GROUP_CONCAT(id ORDER BY id SEPARATOR ',')
        INTO
            v_min_start_id,
            v_max_end_id,
            v_record_ids
        FROM sentence_study_record
        WHERE user_id = v_user_id
          AND book_id = v_book_id
          AND DATE(create_time) = v_yesterday
          AND status = 0;

        IF v_min_start_id IS NOT NULL AND v_max_end_id IS NOT NULL THEN
            IF NOT EXISTS (
                SELECT 1 FROM learning_checklist lc
                WHERE lc.user_id = v_user_id
                  AND lc.book_id = v_book_id
                  AND lc.start_id = v_min_start_id
                  AND lc.end_id = v_max_end_id
                  AND lc.type = 0        -- 句子
            ) THEN
                INSERT INTO learning_checklist (
                    user_id,
                    book_id,
                    start_id,
                    end_id,
                    record_ids,
                    learning_record,
                    type,
                    selected,
                    already_reviewed,
                    create_time,
                    update_time
                ) VALUES (
                    v_user_id,
                    v_book_id,
                    v_min_start_id,
                    v_max_end_id,
                    v_record_ids,
                    CONCAT('学习新句子：【常用短语】的', v_min_start_id, '到', v_max_end_id, '号句子'),
                    0,
                    0,
                    0,
                    DATE_ADD(v_yesterday, INTERVAL 15 HOUR),
                    NOW()
                );
            END IF;
        END IF;
    END LOOP;

    CLOSE user_cursor;
END ;;
DELIMITER ;

