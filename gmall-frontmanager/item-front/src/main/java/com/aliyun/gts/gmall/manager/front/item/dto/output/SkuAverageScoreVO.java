package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "sku所有订单评价平均评分")
public class SkuAverageScoreVO  extends AbstractOutputInfo {

    /**
     * 1～5分，各分值人数，评分统计图（1～5分，分组数量计算）
     */
    private Integer oneTotal;
    private Integer twoTotal;
    private Integer threeTotal;
    private Integer fourTotal;
    private Integer fiveTotal;
    /**
     * SKU平均分
     */
    private Double averageScore;
    /**
     * 评论数量
     */
    private Integer evaluateTotal;



}
