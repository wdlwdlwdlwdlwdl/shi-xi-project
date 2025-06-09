package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("代客下单信息")
public class HelpOrderVO {
    public HelpOrderVO(String operatorName) {
        this.operatorName = operatorName;
    }

    @ApiModelProperty("代客下单操作员")
    private String operatorName;
}
