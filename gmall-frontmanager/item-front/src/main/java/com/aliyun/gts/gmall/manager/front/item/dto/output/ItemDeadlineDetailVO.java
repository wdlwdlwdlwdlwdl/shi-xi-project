package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "商品详情返回对象")
public class ItemDeadlineDetailVO extends AbstractOutputInfo {


    @ApiModelProperty(value = "")
    private String spal;
    @ApiModelProperty(value = "评分")
    private Double star;
    @ApiModelProperty(value = "反馈")
    private Integer feedback;
    @ApiModelProperty(value = "是否展示3小时免费送")
    private Boolean isThreeFee;
    @ApiModelProperty(value = "是否在引渡点")
    private Boolean isExtradite;
    @ApiModelProperty(value = "是否展示提货")
    private Boolean isBill;
    @ApiModelProperty(value = "是否展示储存柜")
    private Boolean isStockpile;
    @ApiModelProperty(value = "原价")
    private String originPrice;
    @ApiModelProperty(value = "现价")
    private String price;
    @ApiModelProperty(value = "分期价格")
    private String amortizePrice;
    @ApiModelProperty(value = "多少分期")
    private Integer amortizeDate;
}
