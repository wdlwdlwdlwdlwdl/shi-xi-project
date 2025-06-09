ALTER TABLE `tc_city`
    MODIFY COLUMN `polygon` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '物流配送范围经纬度' AFTER `active`;