package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
* @Title: CountOrderStatisticsQueryRpcReq.java 
* @Description: 订单状态统计订单数量-查询入参
* @author zhao.qi
* @date 2024年8月12日 13:56:50 
* @version V1.0
 */
@Getter
@Setter
public class OrderStatisticsQueryRpcReq  extends AbstractQueryRpcRequest {
    private static final long serialVersionUID = -6495275386801651945L;
    /** 商家ids */
    private List<Long> sellerIds;
    /** 订单状态 */
    private int orderStatus;
}
