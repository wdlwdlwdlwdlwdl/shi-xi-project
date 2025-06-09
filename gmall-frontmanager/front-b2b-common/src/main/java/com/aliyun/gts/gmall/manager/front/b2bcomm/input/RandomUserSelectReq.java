package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("随机选择人员")
public class RandomUserSelectReq extends CommonReq{
    @ApiModelProperty("随机数")
    int randomSize;
}
