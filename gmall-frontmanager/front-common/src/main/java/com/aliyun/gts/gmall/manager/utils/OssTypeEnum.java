package com.aliyun.gts.gmall.manager.utils;

/**
 * OSS 业务类型
 *
 * @author tiansong
 */
public enum OssTypeEnum {
    //商品
    CUSTOMER_LOGO("logo"),
    // 评价
    EVALUATION("evaluation"),

    CUSTOMER("customer"),
    // 退单
    REVERSAL("reversal"),
    // 申请
    APPLY("apply"),
    //交易
    VOUCHER("voucher"), //凭证
    ;

    private String type;

    private OssTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
