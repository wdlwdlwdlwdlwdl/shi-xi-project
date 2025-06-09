package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ItemSkuRatingVO {
    @ApiModelProperty(value = "平均等级")
    private Double averageRating;
    @ApiModelProperty(value = "复查次数")
    private Integer numberOfReviews;
}
