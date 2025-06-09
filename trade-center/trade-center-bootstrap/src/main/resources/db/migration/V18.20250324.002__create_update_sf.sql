ALTER TABLE `tc_cancel_reason_history`
    MODIFY COLUMN `cancel_reason_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL
    COMMENT '取消原因';