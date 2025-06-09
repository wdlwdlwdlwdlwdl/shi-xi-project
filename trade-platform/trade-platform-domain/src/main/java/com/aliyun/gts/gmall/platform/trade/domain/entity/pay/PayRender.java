package com.aliyun.gts.gmall.platform.trade.domain.entity.pay;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PayRender extends AbstractBusinessEntity {

    private String custId;

    private PayChannel defaultPayChannel;

    private String primaryOrderId;

    private Integer payTimeout;

    private long totalOrderFee;

    private long realPayFee;

    private long orderUsedPointAmount;

    private Long payDiscount = 0L;

    private boolean isJoinPayOnlineDiscnt;

    private List<PayChannel> supportPayChannel;

    private MainOrder mainOrder;

    private Integer stepNo;
}
