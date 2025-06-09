package com.aliyun.gts.gmall.manager.front.trade.dto.output.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO 组合商品
 * @date 2021/10/22 19:20
 */
@Data
@ApiModel("组合商品")
public class CombineItemVO {
    @ApiModelProperty("skuID")
    private Long skuId;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("组合数量")
    private Integer joinQty;

    @ApiModelProperty("商品主图")
    private String itemPic;

    @ApiModelProperty("价格信息")
    private Long itemPrice;

    @ApiModelProperty("sku描述, 例如 '颜色红色,尺码27' ")
    private String skuDesc;

    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty("退换数量")
    private Integer cancelQty;
}
