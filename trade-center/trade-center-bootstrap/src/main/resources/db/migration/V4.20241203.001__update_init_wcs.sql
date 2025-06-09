ALTER TABLE `tc_order`
    MODIFY COLUMN `sku_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'sku描述' AFTER `sku_id`;