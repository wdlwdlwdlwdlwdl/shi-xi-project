package com.aliyun.gts.gmall.manager.front.item.dto.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("积分商品和优惠券对象")
public class PointItemDetailVO extends ItemDetailVO {

    private String pointPrice;

    private String pointNum;


    @ApiModelProperty("积分商城详情线上商品最小原价")
    private String originalPrice;

}