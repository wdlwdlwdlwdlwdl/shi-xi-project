ALTER TABLE `tc_order`
    ADD COLUMN `payment_date` datetime NULL COMMENT '支付成功时间' AFTER `category_commission_rate`,
ADD COLUMN `currency` varchar(64) NULL COMMENT '货币单位' AFTER `payment_date`,
ADD COLUMN `order_delivered` tinyint(1) NULL COMMENT '订单是否交货' AFTER `currency`,
ADD COLUMN `order_delivery_date` datetime NULL COMMENT '订单实际完成时间' AFTER `order_delivered`;