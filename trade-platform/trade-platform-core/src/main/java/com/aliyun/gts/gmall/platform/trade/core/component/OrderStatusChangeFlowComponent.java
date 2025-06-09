package com.aliyun.gts.gmall.platform.trade.core.component;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;

public interface OrderStatusChangeFlowComponent {

    /**
     * 修改订单状态接口
     * @param flowName
     * @param bizCodeEntity
     * @param orderStatus
     * @param op
     * @return
     */
    FlowNodeResult changeOrderStatus(
        String flowName,
        BizCodeEntity bizCodeEntity,
        OrderStatus orderStatus,
        OrderChangeOperate op
    );
}
