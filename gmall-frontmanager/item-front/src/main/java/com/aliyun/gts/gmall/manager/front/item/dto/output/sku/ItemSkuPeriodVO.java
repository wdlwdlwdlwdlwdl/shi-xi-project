package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemSkuPeriodVO {
    @ApiModelProperty(value = "信用卡周期")
    private List<Integer> CREDIT;
    @ApiModelProperty(value = "LOAN周期")
    private List<Integer> LOAN;
}
