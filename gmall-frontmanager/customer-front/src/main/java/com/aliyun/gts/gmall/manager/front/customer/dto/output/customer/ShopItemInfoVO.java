package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShopItemInfoVO extends AbstractOutputInfo {

    @ApiModelProperty("商品ID")
    private Long   id;
    @ApiModelProperty("商品名称")
    private String   title;
    @ApiModelProperty("商品图片")
    private String   picture;
    @ApiModelProperty("商品价格")
    private String   origin;
    @ApiModelProperty("商品营销价格")
    private String   price;
    @ApiModelProperty("商品全称")
    private String   fullTitle;
    @ApiModelProperty("业务ID")
    private String  bizTag;
}
