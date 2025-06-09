package com.aliyun.gts.gmall.manager.front.item.dto.output;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("优惠券中的适用商品对象")
public class PointCouponItemVO {

    private Long id;

    private String price;

    private String title;

    private String picture;


}