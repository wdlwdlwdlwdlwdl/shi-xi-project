package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.Response;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "商品商家评分总结返回对象")
public class ItemMerchantRatingSummaryVO implements Response {


    @ApiModelProperty(value = "批准的审查总数")
    private Integer  totalApprovedReviews;
    @ApiModelProperty(value = "点赞数")
    private Integer  positiveReviews;
    @ApiModelProperty(value = "踩数")
    private Integer  negativeReviews;
    @ApiModelProperty(value = "平均分")
    private Double  averageRating;
    @ApiModelProperty(value = "评论数")
    private Integer  rateCount;
    @ApiModelProperty(value = "评价数键值对")
    private Map<String,Integer> rateCountMap;
    @ApiModelProperty(value = "成功的订单百分比")
    private Integer  successfulOrdersPercentage;
    @ApiModelProperty(value = "评论详细数")
    private Integer  reviewDetailsCount;
}
