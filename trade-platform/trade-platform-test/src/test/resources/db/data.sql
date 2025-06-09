INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (1,0,'inventoryReduceType','2','减库存方式， 1:下单减库存， 2:付款减库存','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (2,0,'oversellProcessType','2','超卖处理方式， 1:自动关单， 2:人工处理','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (3,0,'autoCloseOrderTimeInSec','1200','拍下未付款自动关闭订单时间（秒）','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (4,0,'autoUnlockInventoryTimeInSec','120','拍下未付款自动释放库存时间（秒）, 仅对付款减库存有效','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (6,0,'autoAgreeReversalTimeInSec','315360000','自动同意逆向请求时间（秒）','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (7,0,'createReversalMaxDays','1','可申请售后服务的最长天数（从确认收货后计算）','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (8,0,'autoEvaluateTimeInSec','86400','自动评价的时间 (秒) 测试环境1天','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (9,0,'autoConfirmReceiveTimeInSec','259200','自动确认收货时间（秒）','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (12,0,'autoReceiveReversalTimeInSec','315360000','逆向退货自动确认收货时间（秒）','2021-03-15 15:55:11','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (446,0,'ext.priceRiskRule.default.minDiscountPercent','0','价格风控-默认-最低折扣比例, 30代表3折','2022-10-27 19:07:47','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (447,0,'ext.priceRiskRule.default.minUnitPrice','0','价格风控-默认-最低单价, 单位分','2022-10-27 19:07:47','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (448,0,'ext.priceRiskRule.miaosha.minDiscountPercent','0','价格风控-秒杀-最低折扣比例, 30代表3折','2022-10-27 19:07:47','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (449,0,'ext.priceRiskRule.miaosha.minUnitPrice','0','价格风控-秒杀-最低单价, 单位分','2022-10-27 19:07:48','2023-02-22 10:44:06');
INSERT INTO `tc_seller_config` (`id`,`seller_id`,`conf_name`,`conf_value`,`remark`,`gmt_create`,`gmt_modified`) VALUES (518,0,'ext.gongxiao.delay.confirmReceiveTimeInSec','3600','供销延时确认收货时间（秒）','2022-12-04 19:07:48','2023-02-10 09:47:06');

INSERT INTO `tc_reversal_reason` (`id`,`reversal_type`,`reason_code`,`reason_message`) VALUES (1,1,101,'描述不符');
INSERT INTO `tc_reversal_reason` (`id`,`reversal_type`,`reason_code`,`reason_message`) VALUES (2,1,102,'退运费');
INSERT INTO `tc_reversal_reason` (`id`,`reversal_type`,`reason_code`,`reason_message`) VALUES (3,1,103,'其他');
INSERT INTO `tc_reversal_reason` (`id`,`reversal_type`,`reason_code`,`reason_message`) VALUES (4,2,201,'7天无理由');
INSERT INTO `tc_reversal_reason` (`id`,`reversal_type`,`reason_code`,`reason_message`) VALUES (5,2,202,'商品表述不符');
INSERT INTO `tc_reversal_reason` (`id`,`reversal_type`,`reason_code`,`reason_message`) VALUES (6,2,203,'其他');

INSERT INTO `tc_step_template` (`id`,`name`,`content`) VALUES (1,'test','[
    {
        "stepNo": 1,
        "stepName": "定金1",
        "customerPay": {
            "need": true,
            "timeoutInSec": 36000,
            "timeoutAction": "CLOSE",
            "otherOperation": [
                "SELLER_CLOSE",
                "CUSTOMER_CLOSE"
            ]
        },
        "sellerOperate": {
            "need": false,
        },
        "customerConfirm": {
            "need": false,
        }
    },
    {
        "stepNo": 2,
        "stepName": "定金2",
        "customerPay": {
            "need": true,
            "timeoutInSec": 36000,
            "timeoutAction": "CLOSE",
            "otherOperation": [
                "SELLER_CLOSE",
                "CUSTOMER_CLOSE"
            ]
        },
        "sellerOperate": {
            "need": true,
            "timeoutInSec": 36000,
            "timeoutAction": "CLOSE_AND_REFUND",
            "operateName": "上门测量并报价",
            "collectForm": [
                {
                    "name": "ITEM_TAIL_FEE",
                    "title": "尾款金额",
                    "type": "money"
                }
            ],
            "otherOperation": [
                "REVERSAL"
            ]
        },
        "customerConfirm": {
            "need": true,
            "timeoutInSec": 36000,
            "operateName": "确认报价",
            "timeoutAction": "NEXT",
            "otherOperation": [
                "REVERSAL"
            ]
        }
    },
    {
        "stepNo": 3,
        "stepName": "尾款",
        "customerPay": {
            "need": true,
            "amountName": "ITEM_TAIL_FEE",
            "timeoutInSec": 36000,
            "timeoutAction": "CLOSE_AND_PAYTOSELLER",
            "otherOperation": []
        },
        "sellerOperate": {
            "need": true,
            "timeoutInSec": null,
            "timeoutAction": null,
            "operateName": "发货",
            "collectForm": [
                {
                    "name": "LOGISTICS_COMPANY",
                    "title": "物流公司",
                    "type": "logistics-company"
                },
                {
                    "name": "LOGISTICS_NO",
                    "title": "物流单号",
                    "type": "string"
                }
            ],
            "otherOperation": [
                "REVERSAL"
            ]
        },
        "customerConfirm": {
            "need": true,
            "timeoutInSec": 36000,
            "operateName": "确认收货",
            "timeoutAction": "NEXT",
            "otherOperation": [
                "REVERSAL"
            ]
        }
    }
]');
INSERT INTO `tc_step_template` (`id`,`name`,`content`) VALUES (2,'deposit','[
    {
        "stepNo": 1,
        "stepName": "定金",
        "customerPay": {
            "need": true,
            "name": "支付定金",
            "statusName": "待支付定金"
        },
        "sellerOperate": {
            "need": true,
            "name": "尾款报价",
            "statusName": "待卖家处理尾款",
            "formData": [
                {
                    "name": "ITEM_TAIL_FEE",
                    "title": "尾款(元)",
                    "type": "money"
                },
                {
                    "name": "FREIGHT_FEE",
                    "title": "运费(元)",
                    "type": "money"
                },
                {
                    "name": "LAST_SEND_TIME",
                    "title": "交期",
                    "type": "datetime"
                }
            ],
            "action": [
                {
                    "name": "ADJUEST_FEE",
                    "stepNo": 2,
                    "targetFee": "ITEM_TAIL_FEE",
                    "freightFee": "FREIGHT_FEE"
                },
                {
                    "name": "PROCESS_LAST_SEND_TIME",
                    "targetTime": "LAST_SEND_TIME"
                }
            ],
            "otherOperation": ["REVERSAL_REFUND_ONLY"],
            "@comment": "报价无超时处理, 买家可主动退款"
        },
        "customerConfirm": {
            "need": true,
            "timeoutInSec": 36000,
            "name": "买家确认尾款",
            "statusName": "待买家确认尾款",
            "timeoutAction": "NEXT",
            "otherOperation": ["REVERSAL_REFUND_ONLY"],
            "@comment": "全局超时处理"
        }
    },
    {
        "stepNo": 2,
        "stepName": "尾款",
        "customerPay": {
            "need": true,
            "name": "支付尾款",
            "statusName": "待支付尾款",
            "otherOperation": [],
            "@comment": "尾款支付超时由业务扩展实现, 设置在商品上"
        },
        "sellerOperate": {
            "need": false
        },
        "customerConfirm": {
            "need": false
        },
        "nextOrderStatus": 12
    }
]');
INSERT INTO `tc_step_template` (`id`,`name`,`content`) VALUES (3,'preSale','[
    {
        "stepNo": 1,
        "stepName": "定金",
        "customerPay": {
            "need": true,
            "name": "支付定金",
            "statusName": "待支付定金"
        },
        "sellerOperate": {
            "need": false
        },
        "customerConfirm": {
            "need": false
        }
    },
    {
        "stepNo": 2,
        "stepName": "尾款",
        "customerPay": {
            "need": true,
            "name": "支付尾款",
            "statusName": "待支付尾款",
            "otherOperation": [],
            "@comment": "尾款支付时间及超时由业务扩展实现, 设置在营销活动上"
        },
        "sellerOperate": {
            "need": false
        },
        "customerConfirm": {
            "need": false
        },
        "nextOrderStatus": 12
    }
]');