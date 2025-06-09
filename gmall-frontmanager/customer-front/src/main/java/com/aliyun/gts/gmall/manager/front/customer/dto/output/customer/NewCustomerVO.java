package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("新用户信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCustomerVO {

    @ApiModelProperty("是否可见")
    Boolean visible;

    @ApiModelProperty("是否登录")
    Boolean logged;

    @ApiModelProperty("用户id")
    Long custId;

    @ApiModelProperty("手机号码")
    String phone;

    @ApiModelProperty("用户唯一标识符")
    String custPrimary;

}
