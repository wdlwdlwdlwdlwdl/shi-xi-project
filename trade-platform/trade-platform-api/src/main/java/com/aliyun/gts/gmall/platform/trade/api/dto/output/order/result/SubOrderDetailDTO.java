package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SubOrderDetailDTO extends SubOrderDTO{

    @ApiModelProperty("售后状态")
    Integer reversalStatus;

    @ApiModelProperty(value = "Merchant SKU Code商家sku码")
    private String merchantSkuCode;

}
