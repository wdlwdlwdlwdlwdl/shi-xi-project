package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ItemPrice extends AbstractBusinessEntity {

    @ApiModelProperty(value = "单品原价(单价)")
    @SearchMapping("sale_price")
    private Long originPrice;

    @ApiModelProperty(value = "营销一口价(单价)")
    private Long itemPrice;

    /**
     * 如果订单为代客下单，且代客下单页面自定义价格时，该字段不为空
     * 在center层计算订单价格时会用该价格替换一口价
     */
    @ApiModelProperty(value="代客下单价格(单价)")
    private Long helpOrderPrice;
}
