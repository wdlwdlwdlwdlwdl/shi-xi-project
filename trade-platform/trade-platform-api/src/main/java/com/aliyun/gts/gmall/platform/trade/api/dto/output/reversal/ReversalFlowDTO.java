package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "售后单操作流水")
@Data
public class ReversalFlowDTO extends AbstractOutputInfo {

    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("售后单ID")
    private Long primaryReversalId;

    @ApiModelProperty("ReversalStatusEnum")
    private Integer fromReversalStatus;

    @ApiModelProperty("ReversalStatusEnum")
    private Integer toReversalStatus;

    @ApiModelProperty("1-买家, 0-卖家")
    private Integer custOrSeller;

    @ApiModelProperty("操作名称")
    private String opName;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("扩展字段")
    private String features;
}
