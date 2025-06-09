ALTER TABLE `tc_order`
    MODIFY COLUMN `receive_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '收件人信息';
