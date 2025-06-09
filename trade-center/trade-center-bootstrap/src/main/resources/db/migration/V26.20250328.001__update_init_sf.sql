DROP TABLE IF EXISTS `tc_order_auto_cancel_config`;
CREATE TABLE `tc_order_auto_cancel_config`  (
     `id` int  AUTO_INCREMENT  NOT NULL COMMENT 'id',
     `cancel_reason_code`varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'code',
     `order_status` int NOT NULL COMMENT '订单状态，主订单或子订单状态',
     `gmt_create` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '更新时间',
     `deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='订单自动取消设置表';