
-- ========================
-- 账户模块（Account Context）
-- ========================

-- 用户现金账户表（核心实体）
CREATE TABLE user_accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账户ID（主键）',
    user_id BIGINT NOT NULL COMMENT '用户ID（唯一标识）',
    balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额（精确计算）',
    currency VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 索引与约束
    UNIQUE INDEX idx_user_id (user_id),
    CHECK (balance >= 0) -- 余额不可为负[3](@ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户现金账户表';

-- 商家收入账户表（核心实体）
CREATE TABLE merchant_accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账户ID（主键）',
    merchant_id BIGINT NOT NULL COMMENT '商家ID（唯一标识）',
    balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    settlement_status ENUM('PENDING', 'COMPLETED') DEFAULT 'PENDING' COMMENT '结算状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 索引与约束
    UNIQUE INDEX idx_merchant_id (merchant_id),
    CHECK (balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家收入账户表';

-- 用户交易流水表（新增核心表）[4,1](@ref)
CREATE TABLE user_account_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易流水ID（主键）',
    account_id BIGINT NOT NULL COMMENT '关联账户ID（用户/商家）',
    amount DECIMAL(12, 2) NOT NULL COMMENT '变动金额（正数入账，负数出账）',
    type ENUM('RECHARGE', 'PAYMENT', 'SETTLEMENT') NOT NULL COMMENT '交易类型（充值/支付/结算）',
    order_id BIGINT COMMENT '关联订单ID（可为空）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    -- 索引优化
    INDEX idx_account_time (account_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户交易流水表（记录所有资金变动）';


-- 商户交易流水表（新增核心表）[4,1](@ref)
CREATE TABLE merchant_account_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易流水ID（主键）',
    account_id BIGINT NOT NULL COMMENT '关联账户ID（用户/商家）',
    amount DECIMAL(12, 2) NOT NULL COMMENT '变动金额（正数入账，负数出账）',
    type ENUM('RECHARGE', 'PAYMENT', 'SETTLEMENT') NOT NULL COMMENT '交易类型（充值/支付/结算）',
    order_id BIGINT COMMENT '关联订单ID（可为空）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    -- 索引优化
    INDEX idx_account_time (account_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户账户交易流水表（记录所有资金变动）';


-- ========================
-- 商品库存模块（Inventory Context）
-- ========================

-- 商品信息与库存表（聚合根）
CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID（主键）',
    sku VARCHAR(50) NOT NULL COMMENT '商品唯一编码',
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '当前售价',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    merchant_id BIGINT NOT NULL COMMENT '所属商家ID',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否上架（1-是，0-否）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 索引与约束
    UNIQUE INDEX idx_sku (sku),
    INDEX idx_merchant_product (merchant_id, product_id),
    CHECK (price > 0 AND stock_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息与库存表';

-- 库存变动流水表（值对象集合）
CREATE TABLE inventory_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '流水ID（主键）',
    product_id BIGINT NOT NULL COMMENT '关联商品ID',
    change_quantity INT NOT NULL COMMENT '变动数量（正数入库，负数出库）',
    type ENUM('PURCHASE', 'SALE', 'ADJUST') NOT NULL COMMENT '操作类型（采购/销售/调整）',
    order_id BIGINT COMMENT '关联订单ID（可为空）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    -- 外键约束
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动流水表';

-- ========================
-- 订单交易模块（Order Context）
-- ========================

-- 订单表（聚合根，单商品模式）
CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID（主键）',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    -- 商品快照（冗余防篡改）[3](@ref)
    product_name VARCHAR(255) NOT NULL COMMENT '下单时商品名称',
    product_price DECIMAL(10, 2) NOT NULL COMMENT '下单时单价',
    quantity INT NOT NULL COMMENT '购买数量',
    total_amount DECIMAL(12, 2) NOT NULL COMMENT '订单总金额',
    status ENUM('CREATED', 'PAID', 'COMPLETED', 'CANCELLED') DEFAULT 'CREATED' COMMENT '订单状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
    -- 索引与约束
    INDEX idx_user_order (user_id, status),
    INDEX idx_merchant_order (merchant_id, created_at),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    CHECK (quantity >= 1 AND total_amount = product_price * quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表（单商品模式）';

-- ========================
-- 结算模块（Settlement Context）
-- ========================

-- 商家每日结算对账表（领域服务）
CREATE TABLE settlements (
    settlement_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结算ID（主键）',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    settlement_date DATE NOT NULL COMMENT '结算日期',
    total_sales DECIMAL(12, 2) NOT NULL COMMENT '商品销售总额（库存流水统计）',
    actual_income DECIMAL(12, 2) NOT NULL COMMENT '实际账户收入（账户流水统计）',
    status ENUM('MATCHED', 'MISMATCH') NOT NULL COMMENT '对账结果',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    -- 索引与约束
    UNIQUE INDEX idx_merchant_date (merchant_id, settlement_date),
    CHECK (total_sales >= 0 AND actual_income >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家每日结算对账表';