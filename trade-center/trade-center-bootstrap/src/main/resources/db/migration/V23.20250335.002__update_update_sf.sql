ALTER TABLE tc_delivery_fee ADD COLUMN `is_category_all` char(2) DEFAULT NULL DEFAULT 0 COMMENT '分类生效' ;
ALTER TABLE tc_delivery_fee ADD COLUMN `is_merchant_all` char(2) DEFAULT NULL DEFAULT 0 COMMENT '卖家生效' ;

ALTER TABLE tc_delivery_fee_history ADD COLUMN `is_category_all` char(2) DEFAULT NULL DEFAULT 0 COMMENT '分类生效' ;
ALTER TABLE tc_delivery_fee_history ADD COLUMN `is_merchant_all` char(2) DEFAULT NULL DEFAULT 0 COMMENT '卖家生效' ;
