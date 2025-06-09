ALTER TABLE `tc_order`
    MODIFY COLUMN `loan_cycle` int UNSIGNED NULL DEFAULT NULL COMMENT '贷款周期' AFTER `last_name`;