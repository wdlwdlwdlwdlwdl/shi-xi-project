package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("收银台展示订单信息")
@Data
public class PayRenderOrderInfo extends AbstractOutputInfo {

    @ApiModelProperty(value = "子订单号")
    private String subOrderId;

    @ApiModelProperty(value = "skuId")
    private String itemSkuId;

    @ApiModelProperty(value = "商品ID")
    private String itemId;

    @ApiModelProperty(value = "商品图片地址")
    private String itemMainImgUrl;

}
