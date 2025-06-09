package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CartPrice extends AbstractBusinessEntity {

    @ApiModelProperty(value = "商品原价总金额, 不含运费")
    private Long itemOriginAmt;

    @ApiModelProperty(value = "优惠后总金额, 不含运费")
    private Long promotionAmt;
}
