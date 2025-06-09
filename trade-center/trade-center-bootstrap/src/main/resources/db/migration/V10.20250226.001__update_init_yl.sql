ALTER TABLE tc_logistics
    ADD COLUMN `return_code` varchar(255) NULL COMMENT '退回otp' AFTER `logistics_url`;