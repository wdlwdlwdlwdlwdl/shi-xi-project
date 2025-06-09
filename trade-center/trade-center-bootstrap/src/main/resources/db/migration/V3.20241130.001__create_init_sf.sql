ALTER TABLE tc_order ADD COLUMN `sales_info` varchar(1024) DEFAULT NULL COMMENT '发货地址';
ALTER TABLE tc_order ADD COLUMN `cancel_code` varchar(255) DEFAULT NULL COMMENT '取消原因';