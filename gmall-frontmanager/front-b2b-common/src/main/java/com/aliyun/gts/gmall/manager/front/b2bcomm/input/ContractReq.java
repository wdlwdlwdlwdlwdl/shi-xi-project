package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ContractReq extends CommonPageReq{

    @ApiModelProperty("合同名称")
    String name;

    @ApiModelProperty("合同编码")
    String code;

    @ApiModelProperty("供应商名称")
    String supplierName;

    @ApiModelProperty("采购员名称")
    String purchaserName;

    @ApiModelProperty("寻源响应id，对应报价、竞价、投标")
    Long sourcingId;

    @ApiModelProperty("创建时间 起止")
    Date[] createTime;

    @ApiModelProperty("签署方式 1电子签章 2线下签署")
    Integer signType;

    @ApiModelProperty("是否模板 1是 2不是")
    Integer mediumType;

    Integer status;

}
