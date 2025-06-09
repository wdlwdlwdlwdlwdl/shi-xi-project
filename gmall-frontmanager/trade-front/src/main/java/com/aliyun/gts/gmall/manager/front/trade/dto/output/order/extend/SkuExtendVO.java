package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("sku相关展现")
@Data
public class SkuExtendVO extends OrderExtendVO {

    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long weight;

    public String getWeightUnit() {return ItemUtils.showWeight(this.weight, true);}

    @ApiModelProperty("sku描述")
    String  skuDesc;
    @ApiModelProperty("商品扩展")
    String  itemFeature;
    @ApiModelProperty("skuid")
    Long    skuId;

}
