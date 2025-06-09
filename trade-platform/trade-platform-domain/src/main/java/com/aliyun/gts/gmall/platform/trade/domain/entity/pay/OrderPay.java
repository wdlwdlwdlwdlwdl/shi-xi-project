package com.aliyun.gts.gmall.platform.trade.domain.entity.pay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class OrderPay {

    private String payId;

    private String primaryOrderId;

    private Integer stepNo;

    private String custId;

    private String payChannel;

    private Integer payType;

    private Date payTime;

    private String payStatus;

    private List<String> bizTags;

    private Map<String, String> bizFeature;

    @ApiModelProperty(value = "统一支付标识，用于识别支付")
    private String payCartId;
}
