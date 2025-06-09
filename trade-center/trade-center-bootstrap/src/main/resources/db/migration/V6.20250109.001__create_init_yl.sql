DROP TABLE IF EXISTS `tc_cancel_refund_log`;
CREATE TABLE `tc_cancel_refund_log`  (
                                         `id` int NOT NULL,
                                         `primary_order_id` bigint NULL DEFAULT NULL COMMENT '订单编号',
                                         `order_id` int NULL DEFAULT NULL,
                                         `primary_reversal_id` bigint NULL DEFAULT NULL COMMENT '售后ID',
                                         `refund_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '退款请求信息',
                                         `return_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求返回结果',
                                         `cancel_source` int NULL DEFAULT NULL COMMENT '取消来源 0 系统取消 1客户取消 2商家取消',
                                         `cancel_reason` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
                                         `gmt_create` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `gmt_modified` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                         `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `index_primary_order_id`(`primary_order_id` ASC) USING BTREE,
                                         INDEX `index_primary_reversal_id`(`primary_reversal_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `tc_cancel_reason`
    ADD COLUMN `channel` int NULL COMMENT '0 客户 1 商家' AFTER `operator`;