ALTER TABLE tc_logistics
    ADD COLUMN `company_name` varchar(255) NULL COMMENT '物流公司名称' AFTER `company_type`;