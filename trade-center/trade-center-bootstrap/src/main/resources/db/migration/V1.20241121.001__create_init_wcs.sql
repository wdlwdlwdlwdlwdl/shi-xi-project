DROP TABLE IF EXISTS `check_file_record`;
CREATE TABLE `check_file_record`  (
      `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录流水',
      `gather_task_log_id` decimal(18, 2) NOT NULL COMMENT '采集任务日志标识',
      `obj_type` decimal(5, 2) NOT NULL COMMENT '对象类型',
      `obj_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对象标识',
      `deal_day` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易数据所属日期',
      `check_day` decimal(10, 2) NULL DEFAULT NULL COMMENT '发生对账的日期',
      `pay_serial_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付中心流水',
      `pay_serial_nbr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部交易流水\r\n            对象标识1 此字段为接入使用系统流水\r\n            对象标识2 此字段为支付渠道流水',
      `amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '支付中心交易金额\r\n            对象标识1 此字段为接入系统发起金额\r\n            对象标识2 此字段为支付系统扣费金额',
      `extern_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '外部交易金额',
      `status_cd` decimal(11, 2) NOT NULL COMMENT '对账状态',
      `deal_date` datetime NOT NULL COMMENT '交易发生时间',
      `status_date` datetime NOT NULL COMMENT '状态变更时间',
      `oper_serial_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被操作内部支付流水',
      `oper_serial_nbr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被操作外部支付流水',
      `pay_channel_type` decimal(11, 2) NOT NULL COMMENT '支付渠道类型',
      `store_code` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '接入门户编号',
      `app_id` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '应用系统编号',
      `pay_method_type_id` decimal(11, 2) NULL DEFAULT NULL COMMENT '支付方式类型标识',
      `pay_type_id` decimal(11, 2) NULL DEFAULT NULL COMMENT '交易类型标识',
      `deal_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易所属标识',
      `practical_shop_id` decimal(11, 2) NULL DEFAULT NULL COMMENT '实际门店编号',
      `service_charge` decimal(18, 2) NULL DEFAULT NULL COMMENT '渠道佣金(分)',
      `reality_charge` decimal(18, 2) NULL DEFAULT NULL COMMENT '渠道实拨金额',
      `terminal` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '终端号',
      `card_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卡号',
      `card_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卡类型',
      `channel_pay_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '渠道交易类型',
      `action` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对账文件中的操作动作',
      `PAY_CHANNEL_ID` decimal(11, 2) NULL DEFAULT NULL COMMENT '支付渠道流水',
      `PAY_CHANNEL_SERIAL` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付渠道流水',
      `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
      `gmt_modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
      `deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除',
      `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
      PRIMARY KEY (`record_id`) USING BTREE,
      INDEX `idx_check_file_record1`(`gather_task_log_id` ASC) USING BTREE,
      INDEX `idx_check_file_record2`(`obj_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 872 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '按对账日期采集记录各个平台提供的交易流水信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for distribution_click_record
-- ----------------------------
DROP TABLE IF EXISTS `distribution_click_record`;
CREATE TABLE `distribution_click_record`  (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分销点击记录表主键id',
      `cust_id` bigint NOT NULL COMMENT '用户id',
      `item_id` bigint NOT NULL COMMENT '商品id',
      `agent_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分销客编码',
      `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分销客名称',
      `timestamp` bigint NOT NULL COMMENT '链接时间戳',
      `status` bigint NOT NULL COMMENT '0:有效；1:失效',
      `gmt_create` datetime NOT NULL COMMENT '创建时间',
      `gmt_modified` datetime NOT NULL COMMENT '修改时间',
      `features` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '扩展字段',
      `useless_key` bigint NOT NULL COMMENT '分库分表组合字段',
      `version` bigint NOT NULL COMMENT '版本',
      PRIMARY KEY (`id`) USING BTREE,
      INDEX `auto_shard_key_cust_id`(`cust_id` ASC) USING BTREE,
      INDEX `auto_shard_key_useless_key`(`useless_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 200003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `shard_sequence`;
CREATE TABLE `shard_sequence`  (
   `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
   `value` bigint NULL DEFAULT NULL,
   `update_time` datetime NULL DEFAULT NULL,
   PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_async_task
-- ----------------------------
DROP TABLE IF EXISTS `tc_async_task`;
CREATE TABLE `tc_async_task`  (
      `id` bigint NOT NULL,
      `task_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务类型',
      `task_status` tinyint NOT NULL COMMENT '任务状态, 1:未执行, 2:异常, 3:完成',
      `task_params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '任务参数',
      `schedule_time` datetime NOT NULL COMMENT '预定执行时间',
      `execute_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最近一次执行结果',
      `execute_count` tinyint NOT NULL COMMENT '执行次数',
      `gmt_create` datetime NOT NULL,
      `gmt_modified` datetime NULL DEFAULT NULL,
      `version` bigint NULL DEFAULT NULL COMMENT '版本',
      `primary_order_id` bigint NULL DEFAULT NULL COMMENT '关联的主订单ID',
      PRIMARY KEY (`id`) USING BTREE,
      INDEX `idx_status_and_schedule`(`task_status` ASC, `schedule_time` ASC) USING BTREE,
      INDEX `auto_shard_key_primary_order_id`(`primary_order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_async_task_execute
-- ----------------------------
DROP TABLE IF EXISTS `tc_async_task_execute`;
CREATE TABLE `tc_async_task_execute`  (
      `id` bigint NOT NULL AUTO_INCREMENT,
      `task_id` bigint NOT NULL,
      `gmt_create` datetime NOT NULL,
      `success` tinyint NOT NULL COMMENT '1:成功, 2:失败',
      `result_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      PRIMARY KEY (`id`) USING BTREE,
      INDEX `auto_shard_key_task_id`(`task_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1598289040831873027 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务执行记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cancel_reason
-- ----------------------------
DROP TABLE IF EXISTS `tc_cancel_reason`;
CREATE TABLE `tc_cancel_reason`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `cancel_reason_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'code',
     `cancel_reason_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '取消原因',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_cancel_reason_code`(`cancel_reason_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 281 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '取消原因设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cancel_reason_fee
-- ----------------------------
DROP TABLE IF EXISTS `tc_cancel_reason_fee`;
CREATE TABLE `tc_cancel_reason_fee`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `reason_fee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee code',
     `cancel_reason_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'code',
     `cancel_reason_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '取消原因',
     `cust_fee` tinyint(1) NULL DEFAULT NULL COMMENT '买家承担类型',
     `merchat_fee` tinyint(1) NULL DEFAULT NULL COMMENT '卖家承担类型',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_reason_fee_code`(`reason_fee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 272 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '取消原因费用设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cancel_reason_fee_history
-- ----------------------------
DROP TABLE IF EXISTS `tc_cancel_reason_fee_history`;
CREATE TABLE `tc_cancel_reason_fee_history`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `reason_fee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee code',
     `cancel_reason_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'code',
     `cancel_reason_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '取消原因',
     `cust_fee` tinyint(1) NULL DEFAULT NULL COMMENT '买家承担类型',
     `merchat_fee` tinyint(1) NULL DEFAULT NULL COMMENT '卖家承担类型',
     `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型，1 新增，2 修改',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_reason_fee_code`(`reason_fee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 278 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '取消原因费用记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cancel_reason_history
-- ----------------------------
DROP TABLE IF EXISTS `tc_cancel_reason_history`;
CREATE TABLE `tc_cancel_reason_history`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `cancel_reason_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'code',
     `cancel_reason_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '取消原因',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型，1 新增，2 修改',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '标记',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_cancel_reason_code`(`cancel_reason_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 273 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '取消原因设置历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cart
-- ----------------------------
DROP TABLE IF EXISTS `tc_cart`;
CREATE TABLE `tc_cart`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL,
    `gmt_modified` datetime NOT NULL,
    `cust_id` bigint NOT NULL,
    `item_id` bigint NOT NULL,
    `sku_id` bigint NOT NULL,
    `seller_id` bigint NOT NULL,
    `quantity` int NOT NULL COMMENT '加购数量',
    `add_cart_time` datetime NOT NULL COMMENT '加购时间',
    `add_cart_price` bigint NULL DEFAULT NULL,
    `cart_type` int NOT NULL COMMENT '0: 默认购物车,  其他: 业务购物车',
    `features` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '扩展KV',
    `pay_mode` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '支付方式',
    `sku_quote_id` bigint NULL DEFAULT NULL COMMENT '商品-商户关联标识',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk`(`cust_id` ASC, `cart_type` ASC, `item_id` ASC, `sku_id` ASC, `seller_id` ASC, `pay_mode` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 735794 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_city
-- ----------------------------
DROP TABLE IF EXISTS `tc_city`;
CREATE TABLE `tc_city`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'parent_id',
    `city_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'city code',
    `city_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'city name',
    `city_index` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'city index',
    `active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '激活标记 0 激活 1未激活',
    `polygon` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '物流配送范围经纬度',
    `latitude` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '经纬度',
    `longitude` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '经纬度',
    `zoom` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zoom',
    `color` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'color',
    `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
    `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
    `gmt_modified` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
    `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `deleted` int NULL DEFAULT 0,
    `canton` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '城市所在的州',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_city_code`(`city_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 294 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '城市物流配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cust_delivery_fee
-- ----------------------------
DROP TABLE IF EXISTS `tc_cust_delivery_fee`;
CREATE TABLE `tc_cust_delivery_fee`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `cust_fee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee code',
     `delivery_route` int NOT NULL COMMENT 'code',
     `delivery_type` int NULL DEFAULT NULL COMMENT '物流类型',
     `fee` decimal(10, 0) NULL DEFAULT NULL COMMENT '费用',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     `active` int NULL DEFAULT 0 COMMENT '是否可用 0 正常 1非正常',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_cust_fee_code`(`cust_fee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 273 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '顾客物流费用配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_cust_delivery_fee_history
-- ----------------------------
DROP TABLE IF EXISTS `tc_cust_delivery_fee_history`;
CREATE TABLE `tc_cust_delivery_fee_history`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `cust_fee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee code',
     `delivery_route` int NULL DEFAULT NULL COMMENT 'code',
     `delivery_type` int NULL DEFAULT NULL COMMENT '物流类型',
     `fee` decimal(10, 0) NULL DEFAULT NULL COMMENT '费用',
     `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型，1 新增，2 修改',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     `active` int NULL DEFAULT 0 COMMENT '是否可用',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_cust_fee_code`(`cust_fee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 290 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '顾客物流费用配置记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_delivery_fee
-- ----------------------------
DROP TABLE IF EXISTS `tc_delivery_fee`;
CREATE TABLE `tc_delivery_fee`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `fee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee code',
    `delivery_route` int NOT NULL COMMENT 'code',
    `category_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '类目',
    `category_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类目名称',
    `merchant_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '服务商code',
    `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '服务商名称',
    `delivery_type` int NOT NULL COMMENT '物流类型',
    `fee` decimal(10, 0) NULL DEFAULT NULL COMMENT '费用',
    `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记',
    `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
    `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
    `gmt_modified` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
    `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `active` int NULL DEFAULT 0 COMMENT '是否可用',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_fee_code`(`fee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 273 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '物流费用配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_delivery_fee_history
-- ----------------------------
DROP TABLE IF EXISTS `tc_delivery_fee_history`;
CREATE TABLE `tc_delivery_fee_history`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `fee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee code',
    `delivery_route` int NULL DEFAULT NULL COMMENT 'code',
    `category_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类目',
    `category_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类目名称',
    `merchant_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '服务商code',
    `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '服务商名称',
    `delivery_type` int NULL DEFAULT NULL COMMENT '物流类型',
    `fee` decimal(10, 0) NULL DEFAULT NULL COMMENT '费用',
    `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型，1 新增，2 修改',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
    `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
    `gmt_modified` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
    `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `active` int NULL DEFAULT 0 COMMENT '是否可用',
    `path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_fee_code`(`fee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 276 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '物流费用配置记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `tc_evaluation`;
CREATE TABLE `tc_evaluation`  (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
      `reply_id` bigint NULL DEFAULT NULL COMMENT '被回复的评论id',
      `primary_order_id` bigint NOT NULL COMMENT '主订单ID',
      `order_id` bigint NOT NULL COMMENT '订单ID、订单ID可能是子订单也可能是主订单ID',
      `item_id` bigint NULL DEFAULT NULL COMMENT '商品id',
      `sku_id` bigint NULL DEFAULT NULL COMMENT 'skuID',
      `cust_id` bigint NULL DEFAULT NULL COMMENT '买家id',
      `seller_id` bigint NULL DEFAULT NULL COMMENT '卖家id',
      `rate_score` int NULL DEFAULT NULL COMMENT '评论分数1-5',
      `rate_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '评价内容',
      `rate_pic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '评价图片',
      `rate_video` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '评价视频',
      `gmt_create` datetime NOT NULL,
      `gmt_modified` datetime NOT NULL,
      `extend` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '扩展信息',
      `bin_or_iin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'BIN或者IIN',
      PRIMARY KEY (`id`) USING BTREE,
      INDEX `auto_shard_key`(`primary_order_id` ASC) USING BTREE,
      INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 900956 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_evoucher
-- ----------------------------
DROP TABLE IF EXISTS `tc_evoucher`;
CREATE TABLE `tc_evoucher`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `start_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
    `end_time` datetime NULL DEFAULT NULL COMMENT '失效时间',
    `features` varchar(2000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '扩展字段',
    `status` tinyint NOT NULL COMMENT '状态, 1有效, 2无效',
    `version` bigint NOT NULL COMMENT '版本号',
    `ev_code` bigint NOT NULL COMMENT '电子凭证码',
    `seller_id` bigint NULL DEFAULT NULL COMMENT '卖家ID',
    `seller_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '卖家（店铺）名称',
    `cust_id` bigint NULL DEFAULT NULL COMMENT '顾客ID',
    `cust_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '顾客名称',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_evcode`(`ev_code` ASC) USING BTREE,
    INDEX `idx_orderid`(`order_id` ASC) USING BTREE,
    INDEX `auto_shard_key_ev_code`(`ev_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 500279 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '电子凭证表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_fee_rules
-- ----------------------------
DROP TABLE IF EXISTS `tc_fee_rules`;
CREATE TABLE `tc_fee_rules`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `fee_rules_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee rules code',
     `cust_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城内顾客最低金额',
     `merchant_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城内商家最低金额',
     `inter_cust_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城际顾客最低金额',
     `inter_merchant_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城际商家最低金额',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_fee_rules_code`(`fee_rules_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 273 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '包邮配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_fee_rules_history
-- ----------------------------
DROP TABLE IF EXISTS `tc_fee_rules_history`;
CREATE TABLE `tc_fee_rules_history`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
     `fee_rules_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'fee rules code',
     `cust_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城内顾客最低金额',
     `merchant_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城内商家最低金额',
     `inter_cust_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城际顾客最低金额',
     `inter_merchant_amount` decimal(10, 0) NULL DEFAULT NULL COMMENT '城际商家最低金额',
     `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型，1 新增，2 修改',
     `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
     `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_fee_rules_code`(`fee_rules_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 280 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '包邮配置记录表' ROW_FORMAT = DYNAMIC;

