package com.aliyun.gts.gmall.platform.trade.domain.entity.pay;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToPayData {

    private List<PayInfo> payInfos;

    @ApiModelProperty(value = "合并支付表单信息")
    private String payData;

    @ApiModelProperty(value = "统一购物车标识")
    private String cartId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PayInfo {
        private String primaryOrderId;
        private String payId;
        private String payFlowId;
    }
}
