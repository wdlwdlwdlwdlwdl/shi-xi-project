package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.ConfirmOrderSplitReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;

/**
 * 订单扩展接口
 */
public interface OrderExtFacade {

    RpcResponse<ConfirmOrderSplitDTO> checkSplit(ConfirmOrderSplitReq req);
}
