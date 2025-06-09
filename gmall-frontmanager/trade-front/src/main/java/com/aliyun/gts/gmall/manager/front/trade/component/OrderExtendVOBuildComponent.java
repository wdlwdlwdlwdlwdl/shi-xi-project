package com.aliyun.gts.gmall.manager.front.trade.component;

import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.Result;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;

import java.util.Collection;
import java.util.Map;

public interface OrderExtendVOBuildComponent {

    /**
     * 订单详情扩展
     */
    OrderExtendVO buildExtend(MainOrderDetailDTO mainOrderDetailDTO);

    /**
     * 订单详情扩展 -- 子订单
     */
    OrderExtendVO buildExtend(SubOrderDTO subOrderDTO , MainOrderDetailDTO mainOrderDetailDTO);

    /**
     * 确认订单扩展
     */
    Result<OrderExtendVO> buildExtend(ConfirmOrderRestQuery query, ConfirmOrderDTO confirmOrderDTO);

    /**
     * 订单列表扩展
     */
    Map<Long, OrderExtendVO> buildExtendList(Collection<MainOrderDTO> orderList);
}
