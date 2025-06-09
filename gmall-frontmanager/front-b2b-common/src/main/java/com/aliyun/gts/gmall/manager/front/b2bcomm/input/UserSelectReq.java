package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("选择评标人员的请求")
public class UserSelectReq extends CommonReq{

    @ApiModelProperty("用户名称")
    String userName;
    @ApiModelProperty("1专家 2主持人 3观众 ")
    Integer groupType;


}
