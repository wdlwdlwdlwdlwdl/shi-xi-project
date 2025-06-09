package com.aliyun.gts.gmall.manager.front.item.dto.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 仓库自提点时间
 */
@Data
public class WarehouseBusinessHoursVO implements Serializable {

    @ApiModelProperty("仓库主键")
    private Long warehouseId;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
    
    @ApiModelProperty("星期几,1:Monday,2:Tuesday,3:Wednesday,4:Thursday,5:Friday,6:Saturday,7:Sunday")
    private Integer type;
}
