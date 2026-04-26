-- =============================================
-- Docker MySQL 初始化脚本
-- 仅创建数据库，表结构由 Flyway 管理
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS ai_ticket DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
