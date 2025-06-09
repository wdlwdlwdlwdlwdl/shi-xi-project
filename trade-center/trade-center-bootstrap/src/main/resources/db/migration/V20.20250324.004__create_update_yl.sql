ALTER TABLE `tc_fee_rules`
    MODIFY COLUMN `cust_amount` bigint NULL DEFAULT NULL COMMENT '城内顾客最低金额' AFTER `fee_rules_code`,
    MODIFY COLUMN `merchant_amount` bigint NULL DEFAULT NULL COMMENT '城内商家最低金额' AFTER `cust_amount`,
    MODIFY COLUMN `inter_cust_amount` bigint NULL DEFAULT NULL COMMENT '城际顾客最低金额' AFTER `merchant_amount`,
    MODIFY COLUMN `inter_merchant_amount` bigint NULL DEFAULT NULL COMMENT '城际商家最低金额' AFTER `inter_cust_amount`;


ALTER TABLE `tc_fee_rules_history`
    MODIFY COLUMN `cust_amount` bigint NULL DEFAULT NULL COMMENT '城内顾客最低金额' AFTER `fee_rules_code`,
    MODIFY COLUMN `merchant_amount` bigint NULL DEFAULT NULL COMMENT '城内商家最低金额' AFTER `cust_amount`,
    MODIFY COLUMN `inter_cust_amount` bigint NULL DEFAULT NULL COMMENT '城际顾客最低金额' AFTER `merchant_amount`,