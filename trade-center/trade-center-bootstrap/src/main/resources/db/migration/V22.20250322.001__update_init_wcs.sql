ALTER TABLE `tc_order_operate_flow`
    MODIFY COLUMN `op_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '操作名称' AFTER `operator_type`;

ALTER TABLE `tc_reversal_flow`
    MODIFY COLUMN `op_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '操作名称' AFTER `cust_or_seller`;