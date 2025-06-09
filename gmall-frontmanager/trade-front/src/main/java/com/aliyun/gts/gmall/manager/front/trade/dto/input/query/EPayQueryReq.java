package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EPayQueryReq extends CommonPageReq {

    private String id;

    @ApiModelProperty("订单号ID")
    private String invoiceID;

    @ApiModelProperty("金额")
    private long amount;

    @ApiModelProperty("范围")
    private String scope;

    @ApiModelProperty("用户的唯一ID")
    private String accountId;

    @ApiModelProperty("是否添加币种")
    private Integer isCurrency;

    @ApiModelProperty("是否添加 terminal")
    private Integer isTerminal;

    @ApiModelProperty("是否添加 postLink")
    private Integer isPostLink;

    @ApiModelProperty("是否添加 postLink")
    private Integer isFailurePostLink;

    @ApiModelProperty("是否是取消 isCancel")
    private Integer isCancel;

}
