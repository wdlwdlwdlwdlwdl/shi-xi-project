-- ----------------------------
-- Table structure for tc_finance_flow_record
-- ----------------------------
DROP TABLE IF EXISTS `tc_finance_flow_record`;
CREATE TABLE `tc_finance_flow_record`  (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
   `uid` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '唯一键,正向:主+子订单+pay_id+flow_id;逆向:主+子售后+refund_id+flow_id',
   `biz_order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '正向：主订单号；售后：售后主单id',
   `pay_refund_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付/售后单业务id',
   `biz_flow_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '正向流水/逆向流水',
   `biz_type` tinyint(1) NULL DEFAULT NULL COMMENT '业务类型：1正向2售后',
   `create_date` datetime NULL DEFAULT NULL COMMENT '订单创建时间',
   `biz_date` datetime NULL DEFAULT NULL COMMENT '业务日期，正向：支付时间，逆向：退款时间',
   `confirm_time` datetime NULL DEFAULT NULL COMMENT '确收时间',
   `sale_mode` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '01直销02代销',
   `cust_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '购买用户的id',
   `seller_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '卖家ID',
   `order_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型1普通2多阶段10电子凭证',
   `order_status` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态',
   `pay_channel` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式',
   `order_channel` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单渠道',
   `sub_biz_order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '子订单号',
   `price` bigint NULL DEFAULT NULL COMMENT '单价',
   `qty` int NULL DEFAULT NULL COMMENT '数量',
   `freight` bigint NULL DEFAULT NULL COMMENT '运费',
   `pay_type` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '正向支付类型，逆向退款类型',
   `amount` bigint NULL DEFAULT NULL COMMENT '支付/退款总数',
   `point_grant` bigint NULL DEFAULT NULL COMMENT '积分发放金额',
   `amount_type` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'amount_type为currency时有效，人民币：yuan',
   `return_amount` bigint NULL DEFAULT NULL COMMENT '返还数量',
   `return_amount_type` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目前只有积分类型member_point',
   `return_currency_ype` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'fen',
   `return_point_grant` bigint NULL DEFAULT NULL COMMENT '退积分金额',
   `record_feature` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '扩展属性，记录推送情况等',
   `gmt_create` datetime NOT NULL COMMENT '流水创建时间',
   `gmt_modified` datetime NOT NULL COMMENT '最新修改时间',
   `deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否有效、1有效；0无效',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE INDEX `uq_uid`(`uid` ASC) USING BTREE,
   UNIQUE INDEX `uk_bizUk`(`biz_flow_id` ASC, `sub_biz_order_id` ASC, `biz_type` ASC) USING BTREE,
   INDEX `idx_tc_finance_flow_record_biz_date`(`biz_date` ASC) USING BTREE,
   INDEX `auto_shard_key_cust_id`(`cust_id` ASC) USING BTREE,
   INDEX `idx_tc_finance_flow_record_order_id`(`biz_order_id` ASC) USING BTREE,
   INDEX `auto_shard_key_pay_id`(`pay_refund_id` ASC) USING BTREE,
   INDEX `idx_tc_finance_flow_record_pay_id`(`pay_refund_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务流水记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_item_buy_limit
-- ----------------------------
DROP TABLE IF EXISTS `tc_item_buy_limit`;
CREATE TABLE `tc_item_buy_limit`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_id` bigint NOT NULL,
  `sku_id` bigint NOT NULL,
  `camp_id` bigint NOT NULL,
  `cust_id` bigint NOT NULL,
  `buy_ord_cnt` bigint NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `features` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `useless_key` bigint NOT NULL,
  `version` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_biz`(`cust_id` ASC, `camp_id` ASC, `item_id` ASC) USING BTREE,
  INDEX `auto_shard_key_cust_id`(`cust_id` ASC) USING BTREE,
  INDEX `auto_shard_key_useless_key`(`useless_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_logistics
-- ----------------------------
DROP TABLE IF EXISTS `tc_logistics`;
CREATE TABLE `tc_logistics`  (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
     `primary_order_id` bigint NOT NULL COMMENT '主订单ID',
     `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID、订单ID可能是子订单也可能是主订单ID',
     `cust_id` bigint NULL DEFAULT NULL COMMENT '买家id',
     `seller_id` bigint NULL DEFAULT NULL COMMENT '卖家id',
     `company_type` int NULL DEFAULT NULL COMMENT '物流公司类型',
     `logistics_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '物流公司返回的物流编号',
     `logistics_status` int NULL DEFAULT NULL COMMENT '物流状态',
     `primary_reversal_id` bigint NULL DEFAULT NULL COMMENT '售后单主id',
     `reversal_id` bigint NULL DEFAULT NULL COMMENT '售后单子id',
     `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收货人姓名',
     `receiver_phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收货人电话',
     `receiver_addr` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收货人详细地址',
     `logistics_attr` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '扩展信息',
     `type` int NULL DEFAULT NULL COMMENT '1实物物流 2 虚拟物流 3自提',
     `otp_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'otp',
     `logistics_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '票据url',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `auto_shard_key`(`primary_order_id` ASC) USING BTREE,
     INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 80 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order
-- ----------------------------
DROP TABLE IF EXISTS `tc_order`;
CREATE TABLE `tc_order`  (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
     `primary_order_id` bigint NOT NULL COMMENT '主订单ID',
     `order_id` bigint NOT NULL COMMENT '订单ID，主订单或子订单ID',
     `primary_order_flag` int NOT NULL COMMENT '是否主订单记录，1主订单，0子订单',
     `biz_code` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务身份，JSON字符串数组',
     `order_status` int NOT NULL COMMENT '订单状态，主订单或子订单状态',
     `primary_order_status` int NOT NULL COMMENT '主订单状态',
     `order_attr` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单扩展',
     `snapshot_path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单快照，oss地址',
     `order_channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '下单渠道',
     `evaluate` tinyint(1) NULL DEFAULT 0 COMMENT '是否评价',
     `reversal_type` int NULL DEFAULT NULL COMMENT '售后类型',
     `cust_id` bigint NOT NULL COMMENT '买家id',
     `cust_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '买家name',
     `cust_memo` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户下单备注',
     `item_id` bigint NULL DEFAULT NULL COMMENT '商品id',
     `sku_id` bigint NULL DEFAULT NULL COMMENT 'skuid',
     `sku_desc` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sku描述',
     `item_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品标题',
     `item_quantity` int NULL DEFAULT NULL COMMENT '商品数量',
     `item_pic` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品主图oss地址',
     `item_feature` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品扩展字段',
     `order_price` decimal(19, 0) NOT NULL COMMENT '订单价格（不含优惠）, 子订单时为单价x数量，主订单时sum(子订单价格)',
     `sale_price` decimal(19, 0) NULL DEFAULT NULL COMMENT '商品单价（不含优惠）',
     `real_price` int NOT NULL COMMENT '订单实际价格（优惠后）',
     `order_fee_attr` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '费用扩展字段',
     `promotion_attr` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '营销扩展字段',
     `gmt_create` datetime NOT NULL,
     `gmt_modified` datetime NOT NULL,
     `receive_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收件人信息',
     `seller_id` bigint NOT NULL COMMENT '卖家ID',
     `seller_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '卖家昵称',
     `cust_deleted` tinyint NOT NULL DEFAULT 0,
     `version` bigint NULL DEFAULT NULL COMMENT '版本号',
     `bin_or_iin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'BIN或者IIN',
     `first_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名',
     `last_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓',
     `loan_cycle` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '贷款周期',
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
     INDEX `auto_shard_key_cust_id`(`cust_id` ASC) USING BTREE,
     INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE,
     INDEX `auto_shard_key_primary_order_id`(`primary_order_id` ASC) USING BTREE,
     INDEX `idx_gmt_create`(`gmt_create` ASC) USING BTREE,
     INDEX `idx_order_status`(`cust_id` ASC, `primary_order_flag` ASC, `order_status` ASC) USING BTREE,
     INDEX `tc_order_cust_deleted_idx`(`cust_deleted` ASC, `cust_id` ASC, `primary_order_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32000048732847 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_check_error
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_check_error`;
CREATE TABLE `tc_order_check_error`  (
     `id` bigint NOT NULL COMMENT '主键',
     `primary_order_id` bigint NOT NULL COMMENT '对账主订单',
     `primary_reversal_id` bigint NULL DEFAULT NULL COMMENT '对账售后主单、如无售后、为空',
     `check_type` int NOT NULL COMMENT '对账类型',
     `check_type_describe` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对账类型描述',
     `check_error_detail` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对账出错明细说明',
     `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '更新时间',
     `features` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '扩展字段',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '交易对账错误表、记录错误信息、供后续人工或定时程序补救' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_distribution
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_distribution`;
CREATE TABLE `tc_order_distribution`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `primary_order_id` bigint NOT NULL COMMENT '主订单ID',
  `order_id` bigint NOT NULL COMMENT '订单ID，主订单或子订单ID',
  `item_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品标题',
  `sku_id` bigint NULL DEFAULT NULL COMMENT 'skuid',
  `sku_desc` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'sku描述',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `warehouse_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '仓库名称',
  `item_distribution` int NULL DEFAULT NULL COMMENT '配货件数',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sku_warehouse`(`sku_id` ASC, `warehouse_id` ASC) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE,
  INDEX `idx_sku_id`(`sku_id` ASC) USING BTREE,
  INDEX `idx_primary_order_id`(`primary_order_id` ASC) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单配货表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_extend
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_extend`;
CREATE TABLE `tc_order_extend`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `primary_order_id` bigint NOT NULL COMMENT '主订单ID',
    `order_id` bigint NOT NULL COMMENT '子订单ID',
    `cust_id` bigint NOT NULL COMMENT '买家ID',
    `extend_type` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '扩展属性类型',
    `extend_key` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '扩展属性key',
    `extend_value` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '扩展属性value',
    `extend_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '扩展属性名称',
    `valid` tinyint NOT NULL COMMENT '是否有效',
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_orderid_type_key`(`primary_order_id` ASC, `order_id` ASC, `extend_type` ASC, `extend_key` ASC) USING BTREE,
    INDEX `auto_shard_key_cust_id`(`cust_id` ASC) USING BTREE,
    INDEX `auto_shard_key_primary_order_id`(`primary_order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10000004040403 AVG_ROW_LENGTH = 1024 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_invoice
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_invoice`;
CREATE TABLE `tc_order_invoice`  (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
     `primary_order_id` bigint NULL DEFAULT NULL COMMENT '主订单id',
     `sale_type` int NULL DEFAULT NULL COMMENT '销售类型',
     `master_order_amount` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主单金额',
     `invoice_type` int NULL DEFAULT NULL COMMENT '发票类型：0普通发票，1专票',
     `invoice_id` bigint NULL DEFAULT NULL COMMENT '远程返回的 开票id',
     `request_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求号',
     `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发票代码',
     `invoice_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发票号码',
     `invoice_amount` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开票金额',
     `invoice_time` datetime NULL DEFAULT NULL COMMENT '开票时间',
     `pdf_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件存储key',
     `png_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预览存储key',
     `status` int NULL DEFAULT NULL COMMENT '开票状态:未开票,已开票,部分开票,已撤销,已作废',
     `invoice_reject` int NULL DEFAULT NULL COMMENT '撤销发票 默认0未发起，1撤销中， 3同意 4拒绝',
     `invoice_reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '撤销发票拒绝原因',
     `invoice_title` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开票抬头',
     `invoice_apply_req` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请开票详情',
     `invoice_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '开票结果详情',
     `seller_id` bigint NULL DEFAULT NULL COMMENT '卖家',
     `cust_id` bigint NULL DEFAULT NULL COMMENT '买家',
     `features` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展字段',
     `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '更新时间',
     `deleted` int NULL DEFAULT NULL COMMENT '删除',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `index_invoice_id`(`invoice_id` ASC) USING BTREE,
     INDEX `index_invoice_type`(`invoice_type` ASC) USING BTREE,
     INDEX `index_seller_id`(`seller_id` ASC) USING BTREE,
     INDEX `index_cust_id`(`cust_id` ASC) USING BTREE,
     INDEX `index_order_id`(`primary_order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 AVG_ROW_LENGTH = 1407 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_job
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_job`;
CREATE TABLE `tc_order_job`  (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
     `order_id` bigint NOT NULL COMMENT '订单ID，主订单或子订单ID',
     `order_status` int NOT NULL COMMENT '订单状态，主订单或子订单状态',
     `gmt_create` datetime NOT NULL COMMENT '创建时间',
     `gmt_modified` datetime NOT NULL COMMENT '修改时间',
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
     INDEX `idx_gmt_create`(`gmt_create` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过期订单JOB表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_operate_flow
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_operate_flow`;
CREATE TABLE `tc_order_operate_flow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `primary_order_id` bigint NOT NULL COMMENT '主订单ID',
  `order_id` bigint NOT NULL COMMENT '子订单ID',
  `from_order_status` int NULL DEFAULT NULL COMMENT '变更前订单状态',
  `to_order_status` int NOT NULL COMMENT '变更后订单状态',
  `operator_type` int NOT NULL COMMENT '1:customer, 0:seller',
  `op_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '操作名称',
  `operator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '操作者名称',
  `gmt_create` datetime NOT NULL COMMENT '操作时间',
  `gmt_modified` datetime NOT NULL COMMENT '更新时间',
  `features` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '扩展',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `auto_shard_key_primary_order_id`(`primary_order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15349693 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单操作记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_order_sync
-- ----------------------------
DROP TABLE IF EXISTS `tc_order_sync`;
CREATE TABLE `tc_order_sync`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `primary_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主订单号',
  `sale_type` int NULL DEFAULT NULL COMMENT '销售类型 0=直销 1=代销',
  `fail_count` int NULL DEFAULT NULL COMMENT '失败次数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '结果',
  `status` int NULL DEFAULT NULL COMMENT '状态 0-未上传，1-已上传，2-无需上传',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  `deleted` int NULL DEFAULT NULL COMMENT '删除标识：0-有效，1-失效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3278 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单同步第三方记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_reversal
-- ----------------------------
DROP TABLE IF EXISTS `tc_reversal`;
CREATE TABLE `tc_reversal`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `router_id` int NOT NULL COMMENT '分区路由键',
    `order_id` bigint NOT NULL COMMENT '订单业务id',
    `primary_order_id` bigint NOT NULL COMMENT '主订单业务id',
    `reversal_id` bigint NOT NULL COMMENT '售后单业务id',
    `primary_reversal_id` bigint NOT NULL COMMENT '售后主单业务id',
    `is_reversal_main` tinyint(1) NOT NULL COMMENT '是否售后主单：0-否，1-是',
    `cust_id` bigint NOT NULL COMMENT '购买用户的id，分库分表健',
    `cust_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '购买者姓名',
    `reversal_type` tinyint NOT NULL COMMENT '售后类型：1-仅退款、2-退货退款',
    `reversal_status` tinyint NOT NULL COMMENT '售后单状态:待审核,审核不通过,审核通过(审核通过状态去掉),回收中,退货完成,待退款,退款中,退款完成,售后完成,售后关闭',
    `reversal_reason` bigint NOT NULL COMMENT '售后原因',
    `cust_memo` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款备注，记录客户退款申请时的备注信息',
    `cust_medias` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '售后证据图片等媒体链接',
    `seller_id` bigint NOT NULL COMMENT '商家ID',
    `seller_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称',
    `seller_memo` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '卖家备注',
    `seller_medias` varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '卖家举证图片等媒体链接',
    `item_id` bigint NULL DEFAULT NULL COMMENT '商品标识',
    `sku_id` bigint NULL DEFAULT NULL COMMENT '商品SKU',
    `gmt_create` datetime NOT NULL COMMENT '售后单创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
    `reversal_features` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '售后属性',
    `cancel_qty` int NOT NULL COMMENT '退换数量',
    `cancel_amt` decimal(19, 0) NOT NULL COMMENT '取消金额',
    `reversal_channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请售后渠道编码,枚举同tc_order表order_channel_code',
    `version` bigint NULL DEFAULT NULL COMMENT '版本号',
    `bin_or_iin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'BIN或者IIN',
    `first_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名',
    `last_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_tc_reversal_reversal_id`(`reversal_id` ASC) USING BTREE,
    INDEX `idx_tc_reversal_order_id`(`order_id` ASC) USING BTREE,
    INDEX `idx_tc_reversal_primary_order_id`(`primary_order_id` ASC) USING BTREE,
    INDEX `idx_tc_reversal_primary_reversal_id`(`primary_reversal_id` ASC) USING BTREE,
    INDEX `idx_update_time`(`gmt_modified` ASC) USING BTREE,
    INDEX `auto_shard_key_router_id`(`router_id` ASC) USING BTREE,
    INDEX `idx_gmt_create`(`gmt_create` ASC) USING BTREE,
    INDEX `idx_porderid_status`(`primary_order_id` ASC, `reversal_status` ASC) USING BTREE,
    INDEX `idx_custid_status`(`cust_id` ASC, `reversal_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 500188 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '售后单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_reversal_flow
-- ----------------------------
DROP TABLE IF EXISTS `tc_reversal_flow`;
CREATE TABLE `tc_reversal_flow`  (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
     `primary_reversal_id` bigint NOT NULL COMMENT '售后单ID',
     `from_reversal_status` int NOT NULL COMMENT '售后单状态',
     `to_reversal_status` int NOT NULL COMMENT '售后单状态',
     `cust_or_seller` tinyint(1) NOT NULL COMMENT '1:customer, 0:seller',
     `op_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '操作名称',
     `gmt_create` datetime NOT NULL COMMENT '操作时间',
     `features` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '扩展',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `auto_shard_key_primary_reversal_id`(`primary_reversal_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1100071 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '售后单操作流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_reversal_reason
-- ----------------------------
DROP TABLE IF EXISTS `tc_reversal_reason`;
CREATE TABLE `tc_reversal_reason`  (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
   `reversal_type` tinyint NOT NULL COMMENT '1-仅退款, 2-退货退款',
   `reason_code` bigint NOT NULL COMMENT '售后原因编码',
   `reason_message` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '售后原因内容',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE INDEX `UK`(`reason_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_seller_config
-- ----------------------------
DROP TABLE IF EXISTS `tc_seller_config`;
CREATE TABLE `tc_seller_config`  (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
     `seller_id` bigint NOT NULL COMMENT '卖家ID，0代表默认配置',
     `conf_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '配置项名称',
     `conf_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '配置项值',
     `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
     `gmt_create` datetime NOT NULL,
     `gmt_modified` datetime NOT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `uk_sellerid_confname`(`seller_id` ASC, `conf_name` ASC) USING BTREE,
     INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2226 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '卖家维度的配置信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_step_order
-- ----------------------------
DROP TABLE IF EXISTS `tc_step_order`;
CREATE TABLE `tc_step_order`  (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '无意义主键',
      `primary_order_id` bigint NOT NULL COMMENT '主订单号',
      `step_no` int NOT NULL COMMENT '阶段序号',
      `step_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '阶段名称',
      `price_attr` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '阶段价格',
      `status` int NOT NULL COMMENT '阶段状态',
      `features` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '扩展字段',
      `gmt_create` datetime NOT NULL COMMENT '创建时间',
      `gmt_modified` datetime NOT NULL COMMENT '修改时间',
      `version` bigint NOT NULL COMMENT '版本号',
      PRIMARY KEY (`id`) USING BTREE,
      UNIQUE INDEX `uk_primaryid_stepno`(`primary_order_id` ASC, `step_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1626483002115723266 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_step_template
-- ----------------------------
DROP TABLE IF EXISTS `tc_step_template`;
CREATE TABLE `tc_step_template`  (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
     `content` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tc_timeout_setting
-- ----------------------------
DROP TABLE IF EXISTS `tc_timeout_setting`;
CREATE TABLE `tc_timeout_setting`  (
       `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
       `timeout_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'code',
       `order_status` int NOT NULL COMMENT '订单状态',
       `status_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态名称',
       `pay_type` int NULL DEFAULT NULL COMMENT '1 card 2 loan/installment',
       `time_rule` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '超时时间',
       `time_type` int NULL DEFAULT NULL COMMENT '0 分 1 时 2天',
       `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '标记',
       `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `create_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
       `update_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
       `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
       PRIMARY KEY (`id`) USING BTREE,
       INDEX `idx_order_status`(`order_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 308 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '超时设置表' ROW_FORMAT = DYNAMIC;
