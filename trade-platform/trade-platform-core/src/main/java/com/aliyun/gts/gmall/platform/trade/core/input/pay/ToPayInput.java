package com.aliyun.gts.gmall.platform.trade.core.input.pay;

import com.aliyun.gts.gmall.platform.trade.common.domain.pay.CurrencyType;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ToPayInput implements Serializable {

    private String primaryOrderId;

    private String custId;

    private String buyerName;

    private String orderChannel;

    private String payChannel;

    private Long totalOrderFee;

    private Long realPayFee;

    private String orderType;

    private Long pointAmount = 0L;

    private String recogCode;

    private String appId;

    private String openId;

    private Integer payType;

    private String payMethod;

    private MainOrder mainOrder;

    private CurrencyType currencyType;

    private Map<String, String> extra;

    private String returnPayFlowId;

    private String returnPayData;

    /**
     * 是否立即回调,默认为false，表示需要异步回调
     */
    private boolean isRedirectCallBack;

    @ApiModelProperty(value = "阶段序号, 多阶段交易必传")
    private Integer stepNo;
}
