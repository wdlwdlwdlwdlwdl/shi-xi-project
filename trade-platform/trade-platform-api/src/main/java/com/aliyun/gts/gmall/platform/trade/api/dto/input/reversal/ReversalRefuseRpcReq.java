package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("卖家拒绝售后参数")
public class ReversalRefuseRpcReq extends ReversalModifyRpcReq {

    @ApiModelProperty("卖家备注")
    private String sellerMemo;

    @ApiModelProperty("卖家举证图片")
    private List<String> sellerMedias;
}
