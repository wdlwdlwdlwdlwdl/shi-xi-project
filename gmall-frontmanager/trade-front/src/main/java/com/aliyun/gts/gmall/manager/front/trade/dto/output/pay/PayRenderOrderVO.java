package com.aliyun.gts.gmall.manager.front.trade.dto.output.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收银台展示订单信息
 *
 * @author tiansong
 */
@ApiModel("收银台展示订单信息")
@Data
public class PayRenderOrderVO {
    @ApiModelProperty("子订单号")
    private String subOrderId;
    @ApiModelProperty("skuId")
    private String itemSkuId;
    @ApiModelProperty("商品ID")
    private String itemId;
    @ApiModelProperty("商品图片地址")
    private String itemMainImgUrl;
}
