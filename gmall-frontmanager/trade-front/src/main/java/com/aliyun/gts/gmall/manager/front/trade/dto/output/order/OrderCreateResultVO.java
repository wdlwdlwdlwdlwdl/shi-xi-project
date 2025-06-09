package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 订单创建结果
 *
 * @author tiansong
 */
@Data
@Builder
@ApiModel("订单创建结果")
public class OrderCreateResultVO {
    @ApiModelProperty("主订单ID")
    private Long primaryOrderId;
    @ApiModelProperty("支付流水号")
    private String payFlowId;
    @ApiModelProperty("支付结果表单信息")
    private String payData;
    @ApiModelProperty("创建的主订单ID-平台版, 前端透传, 格式由 PrimaryOrderVO.toMergeParam 生成")
    private List<String> primaryOrderList;
}
