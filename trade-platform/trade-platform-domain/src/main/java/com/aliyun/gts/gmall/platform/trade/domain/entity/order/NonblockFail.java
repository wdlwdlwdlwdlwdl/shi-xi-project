package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NonblockFail {

    @ApiModelProperty("错误码")
    private String code;
    @ApiModelProperty("错误内容")
    private String message;

    @ApiModelProperty("出现错误的 卖家ID")
    private Long sellerId;
    @ApiModelProperty("出现错误的 商品ID")
    private Long itemId;
    @ApiModelProperty("出现错误的 SKU ID")
    private Long skuId;
}
