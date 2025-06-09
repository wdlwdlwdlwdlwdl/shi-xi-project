package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
* @Description: 订单销量统计
* @author yangl
* @date 2025年1月24日 11:26:50
* @version V1.0
 */
@Getter
@Setter
public class OrderSalesStatisticsQueryRpcReq extends AbstractQueryRpcRequest {
    private static final long serialVersionUID = -6495275386801651945L;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "SKU ID", required = true)
    private Long skuId;

}
