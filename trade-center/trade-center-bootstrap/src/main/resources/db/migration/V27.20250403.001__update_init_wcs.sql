DROP TABLE IF EXISTS `tc_order_summary`;
CREATE TABLE `tc_order_summary` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `seller_id` bigint NOT NULL COMMENT '商家ID',
    `statistic_date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '统计日期 ',
    `order_num` int DEFAULT NULL COMMENT '订单数量',
    `cancel_order_num` int DEFAULT NULL COMMENT '取消的订单数量',
    `cancel_rate` varchar(255) DEFAULT NULL COMMENT '取消比例',
    `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建日期',
    `is_send` int DEFAULT '0' COMMENT '是否发送 0 未发送 1 发送成功 2 不需要发',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;