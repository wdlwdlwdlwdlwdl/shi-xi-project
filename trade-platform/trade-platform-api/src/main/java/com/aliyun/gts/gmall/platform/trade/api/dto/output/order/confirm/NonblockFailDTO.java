package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NonblockFailDTO extends AbstractOutputInfo {

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
