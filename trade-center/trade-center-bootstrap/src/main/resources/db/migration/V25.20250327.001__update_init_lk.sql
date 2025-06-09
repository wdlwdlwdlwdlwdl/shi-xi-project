ALTER TABLE `tc_order`
ADD COLUMN `to_pay_date` datetime NULL COMMENT '开始支付成功时间' AFTER `category_commission_rate`,
ADD COLUMN `pay_status` tinyint(1) NULL DEFAULT 0 COMMENT '是否支付0:未支付，1:已支付' AFTER `to_pay_date`;