-- =============================================
-- 智能客服工单系统 - 数据库初始化脚本
-- =============================================

-- 设置字符集
SET NAMES utf8mb4;

-- =============================================
-- 1. 用户表 (t_user)
-- =============================================
CREATE TABLE IF NOT EXISTS t_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL COMMENT '用户名',
    password_hash   VARCHAR(255)    DEFAULT NULL COMMENT '密码哈希',
    email           VARCHAR(100)    DEFAULT NULL COMMENT '邮箱',
    phone           VARCHAR(20)     DEFAULT NULL COMMENT '手机号',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE/BANNED',
    last_login_at   DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    UNIQUE KEY uk_user_email (email),
    KEY idx_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 工单表 (t_ticket)
-- =============================================
CREATE TABLE IF NOT EXISTS t_ticket (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    title           VARCHAR(200)    NOT NULL COMMENT '工单标题',
    description     TEXT            DEFAULT NULL COMMENT '详细描述',
    category        VARCHAR(20)     NOT NULL COMMENT '分类：LOGISTICS/REFUND/ACCOUNT/PAYMENT/PRODUCT/OTHER',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/RESOLVED/CLOSED',
    priority        INT             NOT NULL DEFAULT 5 COMMENT '优先级(1-5)，1最高',
    order_id        VARCHAR(50)     DEFAULT NULL COMMENT '关联订单号',
    escalate_count  INT             NOT NULL DEFAULT 0 COMMENT '催办次数',
    escalated_at    DATETIME        DEFAULT NULL COMMENT '最后催办时间',
    version         INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    closed_at       DATETIME        DEFAULT NULL COMMENT '关闭时间',
    PRIMARY KEY (id),
    KEY idx_ticket_user_id (user_id),
    KEY idx_ticket_status (status),
    KEY idx_ticket_category (category),
    KEY idx_ticket_created_at (created_at),
    KEY idx_ticket_order_id (order_id),
    KEY idx_ticket_deleted (deleted),
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- =============================================
-- 3. 工单日志表 (t_ticket_log)
-- =============================================
CREATE TABLE IF NOT EXISTS t_ticket_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    ticket_id       BIGINT          NOT NULL COMMENT '工单ID',
    action          VARCHAR(50)     NOT NULL COMMENT '操作类型：CREATE/UPDATE/CLOSE/ESCALATE',
    old_status      VARCHAR(20)     DEFAULT NULL COMMENT '原状态',
    new_status      VARCHAR(20)     DEFAULT NULL COMMENT '新状态',
    operator_id     BIGINT          DEFAULT NULL COMMENT '操作人ID',
    remark          TEXT            DEFAULT NULL COMMENT '备注',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_ticket_log_ticket_id (ticket_id),
    KEY idx_ticket_log_created_at (created_at),
    KEY idx_ticket_log_action (action),
    KEY idx_ticket_log_operator_id (operator_id),
    CONSTRAINT fk_ticket_log_ticket FOREIGN KEY (ticket_id) REFERENCES t_ticket(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_ticket_log_operator FOREIGN KEY (operator_id) REFERENCES t_user(id) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单日志表';

-- =============================================
-- 4. 初始化测试数据
-- =============================================

-- 插入测试用户
INSERT INTO t_user (id, username, email, phone, status) VALUES
(1, 'test_user', 'test@example.com', '13800138000', 'ACTIVE'),
(2, 'zhang_san', 'zhangsan@example.com', '13800138001', 'ACTIVE'),
(3, 'li_si', 'lisi@example.com', '13800138002', 'ACTIVE');

-- 插入测试工单
INSERT INTO t_ticket (id, user_id, title, description, category, status, priority, order_id) VALUES
(1, 1, '商品未发货', '订单2024031500123一直未发货，已经过了5天了', 'LOGISTICS', 'PENDING', 3, '2024031500123'),
(2, 1, '退款申请', '商品质量问题申请退款', 'REFUND', 'PROCESSING', 2, '2024031600456'),
(3, 2, '账户登录问题', '无法登录账户，提示密码错误', 'ACCOUNT', 'RESOLVED', 4, NULL);

-- 插入测试工单日志
INSERT INTO t_ticket_log (ticket_id, action, old_status, new_status, operator_id, remark) VALUES
(1, 'CREATE', NULL, 'PENDING', 1, '用户创建工单'),
(2, 'CREATE', NULL, 'PENDING', 1, '用户创建工单'),
(2, 'UPDATE', 'PENDING', 'PROCESSING', 1, '工单开始处理'),
(3, 'CREATE', NULL, 'PENDING', 2, '用户创建工单'),
(3, 'UPDATE', 'PENDING', 'PROCESSING', 2, '工单开始处理'),
(3, 'UPDATE', 'PROCESSING', 'RESOLVED', 2, '问题已解决');
