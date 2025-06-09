package com.aliyun.gts.gmall.manager.front.item.dto.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 定金尾款信息
 *
 * @author linyi
 */
@Data
public class DepositConfigVO {

    public static final int TYPE_1 = 1;

    public static final int TYPE_2 = 2;

    @ApiModelProperty("设置类型，1:比例,2:固定金额")
    private Integer type;

    @ApiModelProperty("比例值,0.30 代表30%")
    private Double ratio;

    @ApiModelProperty("固定金额,单位分")
    private Long amount;

    @ApiModelProperty("尾款天数")
    private Integer day;

}


