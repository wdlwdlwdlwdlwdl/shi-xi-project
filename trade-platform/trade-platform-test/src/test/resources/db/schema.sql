
/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = check_file_record   */
/******************************************/
CREATE TABLE `check_file_record` (
	`record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录流水',
	`gather_task_log_id` decimal(18, 2) NOT NULL COMMENT '采集任务日志标识',
	`obj_type` decimal(5, 2) NOT NULL COMMENT '对象类型',
	`obj_id` varchar(20) NOT NULL COMMENT '对象标识',
	`deal_day` decimal(10, 2) DEFAULT NULL COMMENT '交易数据所属日期',
	`check_day` decimal(10, 2) DEFAULT NULL COMMENT '发生对账的日期',
	`pay_serial_id` varchar(50) DEFAULT NULL COMMENT '支付中心流水',
	`pay_serial_nbr` varchar(50) DEFAULT NULL COMMENT '外部交易流水 对象标识1 此字段为接入使用系统流水 对象标识2 此字段为支付渠道流水',
	`amount` decimal(18, 2) DEFAULT NULL COMMENT '支付中心交易金额 对象标识1 此字段为接入系统发起金额 对象标识2 此字段为支付系统扣费金额',
	`extern_amount` decimal(18, 2) DEFAULT NULL COMMENT '外部交易金额',
	`status_cd` decimal(11, 2) NOT NULL COMMENT '对账状态',
	`deal_date` datetime NOT NULL COMMENT '交易发生时间',
	`status_date` datetime NOT NULL COMMENT '状态变更时间',
	`oper_serial_id` varchar(50) DEFAULT NULL COMMENT '被操作内部支付流水',
	`oper_serial_nbr` varchar(50) DEFAULT NULL COMMENT '被操作外部支付流水',
	`pay_channel_type` decimal(11, 2) NOT NULL COMMENT '支付渠道类型',
	`store_code` decimal(11, 2) DEFAULT '0.00' COMMENT '接入门户编号',
	`app_id` decimal(11, 2) DEFAULT '0.00' COMMENT '应用系统编号',
	`pay_method_type_id` decimal(11, 2) DEFAULT NULL COMMENT '支付方式类型标识',
	`pay_type_id` decimal(11, 2) DEFAULT NULL COMMENT '交易类型标识',
	`deal_id` varchar(50) DEFAULT NULL COMMENT '交易所属标识',
	`practical_shop_id` decimal(11, 2) DEFAULT NULL COMMENT '实际门店编号',
	`service_charge` decimal(18, 2) DEFAULT NULL COMMENT '渠道佣金(分)',
	`reality_charge` decimal(18, 2) DEFAULT NULL COMMENT '渠道实拨金额',
	`terminal` varchar(32) DEFAULT NULL COMMENT '终端号',
	`card_number` varchar(50) DEFAULT NULL COMMENT '卡号',
	`card_type` varchar(20) DEFAULT NULL COMMENT '卡类型',
	`channel_pay_type` varchar(10) DEFAULT NULL COMMENT '渠道交易类型',
	`action` varchar(32) DEFAULT NULL COMMENT '对账文件中的操作动作',
	`PAY_CHANNEL_ID` decimal(11, 2) DEFAULT NULL COMMENT '支付渠道流水',
	`PAY_CHANNEL_SERIAL` varchar(50) DEFAULT NULL COMMENT '支付渠道流水',
	`gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
	`gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
	`deleted` tinyint DEFAULT NULL COMMENT '是否删除',
	`remark` varchar(255) DEFAULT NULL COMMENT '备注',
	PRIMARY KEY (`record_id`),
	KEY (`gather_task_log_id`),
	KEY (`obj_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = distribution_click_record   */
/******************************************/
CREATE TABLE `distribution_click_record` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '分销点击记录表主键id',
	`cust_id` bigint NOT NULL COMMENT '用户id',
	`item_id` bigint NOT NULL COMMENT '商品id',
	`agent_no` varchar(100) NOT NULL COMMENT '分销客编码',
	`agent_name` varchar(255) DEFAULT NULL COMMENT '分销客名称',
	`timestamp` bigint NOT NULL COMMENT '链接时间戳',
	`status` bigint NOT NULL COMMENT '0:有效；1:失效',
	`gmt_create` datetime NOT NULL COMMENT '创建时间',
	`gmt_modified` datetime NOT NULL COMMENT '修改时间',
	`features` varchar(400) DEFAULT NULL COMMENT '扩展字段',
	`useless_key` bigint NOT NULL COMMENT '分库分表组合字段',
	`version` bigint NOT NULL COMMENT '版本',
	PRIMARY KEY (`id`),
	KEY (`useless_key`),
	KEY (`cust_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = shard_sequence   */
/******************************************/
CREATE TABLE `shard_sequence` (
	`name` varchar(100) NOT NULL,
	`value` bigint DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`name`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_async_task   */
/******************************************/
CREATE TABLE `tc_async_task` (
	`id` bigint NOT NULL,
	`task_type` varchar(100) NOT NULL COMMENT '任务类型',
	`task_status` tinyint NOT NULL COMMENT '任务状态, 1:未执行, 2:异常, 3:完成',
	`task_params` varchar(500) DEFAULT NULL COMMENT '任务参数',
	`schedule_time` datetime NOT NULL COMMENT '预定执行时间',
	`execute_message` varchar(2000) DEFAULT NULL COMMENT '最近一次执行结果',
	`execute_count` tinyint NOT NULL COMMENT '执行次数',
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime DEFAULT NULL,
	`version` bigint DEFAULT NULL COMMENT '版本',
	`primary_order_id` bigint DEFAULT NULL COMMENT '关联的主订单ID',
	PRIMARY KEY (`id`),
	KEY (`task_status`, `schedule_time`),
	KEY (`primary_order_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_async_task_execute   */
/******************************************/
CREATE TABLE `tc_async_task_execute` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`task_id` bigint NOT NULL,
	`gmt_create` datetime NOT NULL,
	`success` tinyint NOT NULL COMMENT '1:成功, 2:失败',
	`result_message` varchar(2000) NOT NULL,
	PRIMARY KEY (`id`),
	KEY (`task_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_cart   */
/******************************************/
CREATE TABLE `tc_cart` (
	`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime NOT NULL,
	`cust_id` bigint NOT NULL,
	`item_id` bigint NOT NULL,
	`sku_id` bigint NOT NULL,
	`seller_id` bigint NOT NULL,
	`quantity` int NOT NULL COMMENT '加购数量',
	`add_cart_time` datetime NOT NULL COMMENT '加购时间',
	`add_cart_price` bigint DEFAULT NULL,
	`cart_type` int NOT NULL COMMENT '0: 默认购物车,  其他: 业务购物车',
	`features` varchar(4000) DEFAULT NULL COMMENT '扩展KV',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`cust_id`, `cart_type`, `item_id`, `sku_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_evaluation   */
/******************************************/
CREATE TABLE `tc_evaluation` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
	`reply_id` bigint DEFAULT NULL COMMENT '被回复的评论id',
	`primary_order_id` bigint NOT NULL COMMENT '主订单ID',
	`order_id` bigint NOT NULL COMMENT '订单ID、订单ID可能是子订单也可能是主订单ID',
	`item_id` bigint DEFAULT NULL COMMENT '商品id',
	`cust_id` bigint DEFAULT NULL COMMENT '买家id',
	`seller_id` bigint DEFAULT NULL COMMENT '卖家id',
	`rate_score` int DEFAULT NULL COMMENT '评论分数1-5',
	`rate_desc` text COMMENT '评价内容',
	`rate_pic` text COMMENT '评价图片',
	`rate_video` text COMMENT '评价视频',
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime NOT NULL,
	`extend` text COMMENT '扩展信息',
	PRIMARY KEY (`id`),
	KEY (`primary_order_id`),
	KEY (`gmt_modified`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_evoucher   */
/******************************************/
CREATE TABLE `tc_evoucher` (
	`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`gmt_create` datetime NOT NULL COMMENT '创建时间',
	`gmt_modified` datetime NOT NULL COMMENT '修改时间',
	`order_id` bigint NOT NULL COMMENT '订单ID',
	`start_time` datetime DEFAULT NULL COMMENT '生效时间',
	`end_time` datetime DEFAULT NULL COMMENT '失效时间',
	`features` varchar(2000) DEFAULT NULL COMMENT '扩展字段',
	`status` tinyint NOT NULL COMMENT '状态, 1有效, 2无效',
	`version` bigint NOT NULL COMMENT '版本号',
	`ev_code` bigint NOT NULL COMMENT '电子凭证码',
	`seller_id` bigint DEFAULT NULL COMMENT '卖家ID',
	`seller_name` varchar(100) DEFAULT NULL COMMENT '卖家（店铺）名称',
	`cust_id` bigint DEFAULT NULL COMMENT '顾客ID',
	`cust_name` varchar(100) DEFAULT NULL COMMENT '顾客名称',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`ev_code`),
	KEY (`order_id`),
	KEY (`ev_code`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_finance_flow_record   */
/******************************************/
CREATE TABLE `tc_finance_flow_record` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
	`uid` varchar(200) NOT NULL COMMENT '唯一键,正向:主+子订单+pay_id+flow_id;逆向:主+子售后+refund_id+flow_id',
	`biz_order_id` varchar(32) DEFAULT NULL COMMENT '正向：主订单号；售后：售后主单id',
	`pay_refund_id` varchar(32) NOT NULL COMMENT '支付/售后单业务id',
	`biz_flow_id` varchar(32) DEFAULT NULL COMMENT '正向流水/逆向流水',
	`biz_type` tinyint(1) DEFAULT NULL COMMENT '业务类型：1正向2售后',
	`create_date` datetime DEFAULT NULL COMMENT '订单创建时间',
	`biz_date` datetime DEFAULT NULL COMMENT '业务日期，正向：支付时间，逆向：退款时间',
	`confirm_time` datetime DEFAULT NULL COMMENT '确收时间',
	`sale_mode` char(2) DEFAULT NULL COMMENT '01直销02代销',
	`cust_id` varchar(32) NOT NULL COMMENT '购买用户的id',
	`seller_id` varchar(32) NOT NULL COMMENT '卖家ID',
	`order_type` varchar(3) NOT NULL COMMENT '订单类型1普通2多阶段10电子凭证',
	`order_status` varchar(3) NOT NULL COMMENT '订单状态',
	`pay_channel` varchar(15) NOT NULL COMMENT '支付方式',
	`order_channel` varchar(15) NOT NULL COMMENT '下单渠道',
	`sub_biz_order_id` varchar(32) NOT NULL COMMENT '子订单号',
	`price` bigint DEFAULT NULL COMMENT '单价',
	`qty` int DEFAULT NULL COMMENT '数量',
	`freight` bigint DEFAULT NULL COMMENT '运费',
	`pay_type` varchar(15) NOT NULL COMMENT '正向支付类型，逆向退款类型',
	`amount` bigint DEFAULT NULL COMMENT '支付/退款总数',
	`point_grant` bigint DEFAULT NULL COMMENT '积分发放金额',
	`amount_type` varchar(15) NOT NULL COMMENT 'amount_type为currency时有效，人民币：yuan',
	`return_amount` bigint DEFAULT NULL COMMENT '返还数量',
	`return_amount_type` varchar(15) NOT NULL COMMENT '目前只有积分类型member_point',
	`return_currency_ype` varchar(15) NOT NULL COMMENT 'fen',
	`return_point_grant` bigint DEFAULT NULL COMMENT '退积分金额',
	`record_feature` varchar(2048) DEFAULT NULL COMMENT '扩展属性，记录推送情况等',
	`gmt_create` datetime NOT NULL COMMENT '流水创建时间',
	`gmt_modified` datetime NOT NULL COMMENT '最新修改时间',
	`deleted` tinyint(1) DEFAULT NULL COMMENT '是否有效、1有效；0无效',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`uid`),
	UNIQUE KEY (`biz_flow_id`, `sub_biz_order_id`, `biz_type`),
	KEY (`biz_date`),
	KEY (`cust_id`),
	KEY (`biz_order_id`),
	KEY (`pay_refund_id`),
	KEY (`pay_refund_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_item_buy_limit   */
/******************************************/
CREATE TABLE `tc_item_buy_limit` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`item_id` bigint NOT NULL,
	`sku_id` bigint NOT NULL,
	`camp_id` bigint NOT NULL,
	`cust_id` bigint NOT NULL,
	`buy_ord_cnt` bigint NOT NULL,
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime NOT NULL,
	`features` varchar(200) DEFAULT NULL,
	`useless_key` bigint NOT NULL,
	`version` bigint NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY (`cust_id`, `camp_id`, `item_id`),
	KEY (`cust_id`),
	KEY (`useless_key`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_logistics   */
/******************************************/
CREATE TABLE `tc_logistics` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
	`primary_order_id` bigint NOT NULL COMMENT '主订单ID',
	`order_id` bigint DEFAULT NULL COMMENT '订单ID、订单ID可能是子订单也可能是主订单ID',
	`cust_id` bigint DEFAULT NULL COMMENT '买家id',
	`seller_id` bigint DEFAULT NULL COMMENT '卖家id',
	`company_type` int DEFAULT NULL COMMENT '物流公司类型',
	`logistics_id` varchar(128) DEFAULT NULL COMMENT '物流公司返回的物流编号',
	`logistics_status` int DEFAULT NULL COMMENT '物流状态',
	`primary_reversal_id` bigint DEFAULT NULL COMMENT '售后单主id',
	`reversal_id` bigint DEFAULT NULL COMMENT '售后单子id',
	`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
	`receiver_phone` varchar(100) NOT NULL COMMENT '收货人电话',
	`receiver_addr` varchar(256) DEFAULT NULL COMMENT '收货人详细地址',
	`logistics_attr` text COMMENT '扩展信息',
	`type` int NOT NULL COMMENT '1实物物流 2 虚拟物流 3自提',
	PRIMARY KEY (`id`),
	KEY (`primary_order_id`),
	KEY (`gmt_modified`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order   */
/******************************************/
CREATE TABLE `tc_order` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
	`primary_order_id` bigint NOT NULL COMMENT '主订单ID',
	`order_id` bigint NOT NULL COMMENT '订单ID，主订单或子订单ID',
	`primary_order_flag` int NOT NULL COMMENT '是否主订单记录，1主订单，0子订单',
	`biz_code` varchar(256) NOT NULL COMMENT '业务身份，JSON字符串数组',
	`order_status` int NOT NULL COMMENT '订单状态，主订单或子订单状态',
	`primary_order_status` int NOT NULL COMMENT '主订单状态',
	`order_attr` varchar(4000) DEFAULT NULL COMMENT '订单扩展',
	`snapshot_path` varchar(128) DEFAULT NULL COMMENT '订单快照，oss地址',
	`order_channel` varchar(32) DEFAULT NULL COMMENT '下单渠道',
	`evaluate` tinyint(1) DEFAULT '0' COMMENT '是否评价',
	`reversal_type` int DEFAULT NULL COMMENT '售后类型',
	`cust_id` bigint NOT NULL COMMENT '买家id',
	`cust_name` varchar(128) NOT NULL COMMENT '买家name',
	`cust_memo` varchar(256) DEFAULT NULL COMMENT '用户下单备注',
	`item_id` bigint DEFAULT NULL COMMENT '商品id',
	`sku_id` bigint DEFAULT NULL COMMENT 'skuid',
	`sku_desc` varchar(256) DEFAULT NULL COMMENT 'sku描述',
	`item_title` varchar(512) DEFAULT NULL COMMENT '商品标题',
	`item_quantity` int DEFAULT NULL COMMENT '商品数量',
	`item_pic` varchar(256) DEFAULT NULL COMMENT '商品主图oss地址',
	`item_feature` varchar(4000) DEFAULT NULL COMMENT '商品扩展字段',
	`order_price` decimal(19, 0) NOT NULL COMMENT '订单价格（不含优惠）, 子订单时为单价x数量，主订单时sum(子订单价格)',
	`sale_price` decimal(19, 0) DEFAULT NULL COMMENT '商品单价（不含优惠）',
	`real_price` int NOT NULL COMMENT '订单实际价格（优惠后）',
	`order_fee_attr` varchar(4000) DEFAULT NULL COMMENT '费用扩展字段',
	`promotion_attr` varchar(4000) DEFAULT NULL COMMENT '营销扩展字段',
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime NOT NULL,
	`receive_info` varchar(1024) DEFAULT NULL COMMENT '收件人信息',
	`seller_id` bigint NOT NULL COMMENT '卖家ID',
	`seller_name` varchar(256) NOT NULL COMMENT '卖家昵称',
	`cust_deleted` tinyint NOT NULL DEFAULT '0',
	`version` bigint DEFAULT NULL COMMENT '版本号',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`order_id`),
	KEY (`cust_id`),
	KEY (`gmt_modified`),
	KEY (`primary_order_id`),
	KEY (`gmt_create`),
	KEY (`cust_id`, `primary_order_flag`, `order_status`),
	KEY (`cust_deleted`, `cust_id`, `primary_order_flag`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order_check_error   */
/******************************************/
CREATE TABLE `tc_order_check_error` (
	`id` bigint NOT NULL COMMENT '主键',
	`primary_order_id` bigint NOT NULL COMMENT '对账主订单',
	`primary_reversal_id` bigint DEFAULT NULL COMMENT '对账售后主单、如无售后、为空',
	`check_type` int NOT NULL COMMENT '对账类型',
	`check_type_describe` varchar(64) DEFAULT NULL COMMENT '对账类型描述',
	`check_error_detail` varchar(2048) DEFAULT NULL COMMENT '对账出错明细说明',
	`gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
	`features` varchar(2048) DEFAULT NULL COMMENT '扩展字段',
	PRIMARY KEY (`id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order_distribution   */
/******************************************/
CREATE TABLE `tc_order_distribution` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
	`primary_order_id` bigint NOT NULL COMMENT '主订单ID',
	`order_id` bigint NOT NULL COMMENT '订单ID，主订单或子订单ID',
	`item_title` varchar(512) DEFAULT NULL COMMENT '商品标题',
	`sku_id` bigint DEFAULT NULL COMMENT 'skuid',
	`sku_desc` varchar(256) DEFAULT NULL COMMENT 'sku描述',
	`warehouse_id` bigint NOT NULL COMMENT '仓库id',
	`warehouse_name` varchar(32) NOT NULL COMMENT '仓库名称',
	`item_distribution` int DEFAULT NULL COMMENT '配货件数',
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY (`sku_id`, `warehouse_id`),
	KEY (`gmt_modified`),
	KEY (`sku_id`),
	KEY (`primary_order_id`),
	KEY (`gmt_create`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order_extend   */
/******************************************/
CREATE TABLE `tc_order_extend` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
	`primary_order_id` bigint NOT NULL COMMENT '主订单ID',
	`order_id` bigint NOT NULL COMMENT '子订单ID',
	`cust_id` bigint NOT NULL COMMENT '买家ID',
	`extend_type` varchar(128) NOT NULL COMMENT '扩展属性类型',
	`extend_key` varchar(128) NOT NULL COMMENT '扩展属性key',
	`extend_value` varchar(2048) NOT NULL COMMENT '扩展属性value',
	`extend_name` varchar(128) NOT NULL COMMENT '扩展属性名称',
	`valid` tinyint NOT NULL COMMENT '是否有效',
	`gmt_create` datetime NOT NULL COMMENT '创建时间',
	`gmt_modified` datetime NOT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`primary_order_id`, `order_id`, `extend_type`, `extend_key`),
	KEY (`cust_id`),
	KEY (`primary_order_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order_invoice   */
/******************************************/
CREATE TABLE `tc_order_invoice` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
	`primary_order_id` bigint DEFAULT NULL COMMENT '主订单id',
	`sale_type` int DEFAULT NULL COMMENT '销售类型',
	`master_order_amount` varchar(20) DEFAULT NULL COMMENT '主单金额',
	`invoice_type` int DEFAULT NULL COMMENT '发票类型：0普通发票，1专票',
	`invoice_id` bigint DEFAULT NULL COMMENT '远程返回的 开票id',
	`request_no` varchar(50) DEFAULT NULL COMMENT '请求号',
	`invoice_code` varchar(100) DEFAULT NULL COMMENT '发票代码',
	`invoice_no` varchar(100) DEFAULT NULL COMMENT '发票号码',
	`invoice_amount` varchar(20) DEFAULT NULL COMMENT '开票金额',
	`invoice_time` datetime DEFAULT NULL COMMENT '开票时间',
	`pdf_key` varchar(200) DEFAULT NULL COMMENT '文件存储key',
	`png_key` varchar(200) DEFAULT NULL COMMENT '预览存储key',
	`status` int DEFAULT NULL COMMENT '开票状态:未开票,已开票,部分开票,已撤销,已作废',
	`invoice_reject` int DEFAULT NULL COMMENT '撤销发票 默认0未发起，1撤销中， 3同意 4拒绝',
	`invoice_reject_reason` text COMMENT '撤销发票拒绝原因',
	`invoice_title` varchar(1000) DEFAULT NULL COMMENT '开票抬头',
	`invoice_apply_req` text COMMENT '申请开票详情',
	`invoice_result` text COMMENT '开票结果详情',
	`seller_id` bigint DEFAULT NULL COMMENT '卖家',
	`cust_id` bigint DEFAULT NULL COMMENT '买家',
	`features` text COMMENT '扩展字段',
	`gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
	`deleted` int DEFAULT NULL COMMENT '删除',
	PRIMARY KEY (`id`),
	KEY (`invoice_id`),
	KEY (`invoice_type`),
	KEY (`seller_id`),
	KEY (`cust_id`),
	KEY (`primary_order_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order_operate_flow   */
/******************************************/
CREATE TABLE `tc_order_operate_flow` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
	`primary_order_id` bigint NOT NULL COMMENT '主订单ID',
	`order_id` bigint NOT NULL COMMENT '子订单ID',
	`from_order_status` int DEFAULT NULL COMMENT '变更前订单状态',
	`to_order_status` int NOT NULL COMMENT '变更后订单状态',
	`operator_type` int NOT NULL COMMENT '1:customer, 0:seller',
	`op_name` varchar(32) NOT NULL COMMENT '操作名称',
	`operator` varchar(32) NOT NULL COMMENT '操作者名称',
	`gmt_create` datetime NOT NULL COMMENT '操作时间',
	`gmt_modified` datetime NOT NULL COMMENT '更新时间',
	`features` varchar(200) DEFAULT NULL COMMENT '扩展',
	PRIMARY KEY (`id`),
	KEY (`primary_order_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_order_sync   */
/******************************************/
CREATE TABLE `tc_order_sync` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
	`primary_order_id` varchar(64) DEFAULT NULL COMMENT '主订单号',
	`sale_type` int DEFAULT NULL COMMENT '销售类型 0=直销 1=代销',
	`fail_count` int DEFAULT NULL COMMENT '失败次数',
	`result` text COMMENT '结果',
	`status` int DEFAULT NULL COMMENT '状态 0-未上传，1-已上传，2-无需上传',
	`gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
	`gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
	`version` int DEFAULT NULL COMMENT '版本号',
	`deleted` int DEFAULT NULL COMMENT '删除标识：0-有效，1-失效',
	PRIMARY KEY (`id`),
	KEY (`status`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_reversal   */
/******************************************/
CREATE TABLE `tc_reversal` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
	`router_id` int NOT NULL COMMENT '分区路由键',
	`order_id` bigint NOT NULL COMMENT '订单业务id',
	`primary_order_id` bigint NOT NULL COMMENT '主订单业务id',
	`reversal_id` bigint NOT NULL COMMENT '售后单业务id',
	`primary_reversal_id` bigint NOT NULL COMMENT '售后主单业务id',
	`is_reversal_main` tinyint(1) NOT NULL COMMENT '是否售后主单：0-否，1-是',
	`cust_id` bigint NOT NULL COMMENT '购买用户的id，分库分表健',
	`cust_name` varchar(256) NOT NULL COMMENT '购买者姓名',
	`reversal_type` tinyint NOT NULL COMMENT '售后类型：1-仅退款、2-退货退款',
	`reversal_status` tinyint NOT NULL COMMENT '售后单状态:待审核,审核不通过,审核通过(审核通过状态去掉),回收中,退货完成,待退款,退款中,退款完成,售后完成,售后关闭',
	`reversal_reason` bigint NOT NULL COMMENT '售后原因',
	`cust_memo` varchar(1024) DEFAULT NULL COMMENT '退款备注，记录客户退款申请时的备注信息',
	`cust_medias` varchar(4096) DEFAULT NULL COMMENT '售后证据图片等媒体链接',
	`seller_id` bigint NOT NULL COMMENT '商家ID',
	`seller_name` varchar(256) NOT NULL COMMENT '供应商名称',
	`seller_memo` varchar(1024) DEFAULT NULL COMMENT '卖家备注',
	`seller_medias` varchar(4096) DEFAULT NULL COMMENT '卖家举证图片等媒体链接',
	`item_id` bigint DEFAULT NULL COMMENT '商品标识',
	`sku_id` bigint DEFAULT NULL COMMENT '商品SKU',
	`gmt_create` datetime NOT NULL COMMENT '售后单创建时间',
	`gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
	`reversal_features` varchar(2000) DEFAULT NULL COMMENT '售后属性',
	`cancel_qty` int NOT NULL COMMENT '退换数量',
	`cancel_amt` decimal(19, 0) NOT NULL COMMENT '取消金额',
	`reversal_channel` varchar(32) NOT NULL COMMENT '申请售后渠道编码,枚举同tc_order表order_channel_code',
	`version` bigint DEFAULT NULL COMMENT '版本号',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`reversal_id`),
	KEY (`order_id`),
	KEY (`primary_order_id`),
	KEY (`primary_reversal_id`),
	KEY (`gmt_modified`),
	KEY (`router_id`),
	KEY (`gmt_create`),
	KEY (`primary_order_id`, `reversal_status`),
	KEY (`cust_id`, `reversal_status`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_reversal_flow   */
/******************************************/
CREATE TABLE `tc_reversal_flow` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
	`primary_reversal_id` bigint NOT NULL COMMENT '售后单ID',
	`from_reversal_status` int NOT NULL COMMENT '售后单状态',
	`to_reversal_status` int NOT NULL COMMENT '售后单状态',
	`cust_or_seller` tinyint(1) NOT NULL COMMENT '1:customer, 0:seller',
	`op_name` varchar(32) NOT NULL COMMENT '操作名称',
	`gmt_create` datetime NOT NULL COMMENT '操作时间',
	`features` varchar(200) DEFAULT NULL COMMENT '扩展',
	PRIMARY KEY (`id`),
	KEY (`primary_reversal_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_reversal_reason   */
/******************************************/
CREATE TABLE `tc_reversal_reason` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
	`reversal_type` tinyint NOT NULL COMMENT '1-仅退款, 2-退货退款',
	`reason_code` bigint NOT NULL COMMENT '售后原因编码',
	`reason_message` varchar(256) NOT NULL COMMENT '售后原因内容',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`reason_code`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_seller_config   */
/******************************************/
CREATE TABLE `tc_seller_config` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
	`seller_id` bigint NOT NULL COMMENT '卖家ID，0代表默认配置',
	`conf_name` varchar(100) NOT NULL COMMENT '配置项名称',
	`conf_value` varchar(100) NOT NULL COMMENT '配置项值',
	`remark` varchar(200) DEFAULT NULL COMMENT '备注',
	`gmt_create` datetime NOT NULL,
	`gmt_modified` datetime NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY (`seller_id`, `conf_name`),
	KEY (`seller_id`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_step_order   */
/******************************************/
CREATE TABLE `tc_step_order` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '无意义主键',
	`primary_order_id` bigint NOT NULL COMMENT '主订单号',
	`step_no` int NOT NULL COMMENT '阶段序号',
	`step_name` varchar(50) NOT NULL COMMENT '阶段名称',
	`price_attr` varchar(4000) NOT NULL COMMENT '阶段价格',
	`status` int NOT NULL COMMENT '阶段状态',
	`features` varchar(4000) DEFAULT NULL COMMENT '扩展字段',
	`gmt_create` datetime NOT NULL COMMENT '创建时间',
	`gmt_modified` datetime NOT NULL COMMENT '修改时间',
	`version` bigint NOT NULL COMMENT '版本号',
	PRIMARY KEY (`id`),
	UNIQUE KEY (`primary_order_id`, `step_no`)
)
;

/******************************************/
/*   DatabaseName = trade_dev   */
/*   TableName = tc_step_template   */
/******************************************/
CREATE TABLE `tc_step_template` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`content` varchar(4000) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY (`name`)
)
;
