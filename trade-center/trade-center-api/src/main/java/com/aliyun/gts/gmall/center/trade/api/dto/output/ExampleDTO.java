package com.aliyun.gts.gmall.center.trade.api.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "返回对象例子")
public class ExampleDTO extends AbstractOutputInfo {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("随机数")
    private Long random;
}
