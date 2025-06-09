package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@ApiModel("客户成长值记录")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerGrowthRecordVO {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("用户ID")
    private Long custId;
    @ApiModelProperty("业务类型 CustomerGrowthTypeEnum 3:当日首次登录,2:订单次数,1:订单金额")
    private String type;
    @ApiModelProperty("业务ID")
    private String bizId;
    @ApiModelProperty("业务时间")
    private Date bizTime;
    @ApiModelProperty("成长值")
    private Integer growthDelta;
    @ApiModelProperty("拓展信息")
    private Map<String, String> feature;
    @ApiModelProperty("备注")
    private String memo;
    @ApiModelProperty("创建时间")
    private Date gmtCreate;
    @ApiModelProperty("更新时间")
    private Date gmtModified;


}
