package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "售后单查询结果")
public class SubReversalDTO extends AbstractOutputInfo {

    @ApiModelProperty("售后主单ID")
    private Long primaryReversalId;

    @ApiModelProperty("售后子单ID")
    private Long reversalId;

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("退换商品数量")
    private Integer cancelQty;

    @ApiModelProperty("退款金额")
    private Long cancelAmt;
    
    @ApiModelProperty("订单信息")
    private SubOrderDTO orderInfo;

    @ApiModelProperty("扩展字段")
    private ReversalFeatureDTO reversalFeatures;
}
