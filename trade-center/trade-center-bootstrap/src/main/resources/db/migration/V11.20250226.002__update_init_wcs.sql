ALTER TABLE `tc_reversal`
    ADD COLUMN `reversal_completed_time` datetime NULL COMMENT '退单完成时间' AFTER `last_name`;