package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;

import java.util.List;

public interface OrderExtraService {

    /**
     * 查扩展结构
     */
    List<OrderExtendDTO> queryOrderExtend(OrderExtendQueryRpcReq req);

    /**
     * 更新订单扩展属性，包括feature与扩展结构
     */
    void saveOrderExtras(OrderExtraSaveRpcReq req);
}
