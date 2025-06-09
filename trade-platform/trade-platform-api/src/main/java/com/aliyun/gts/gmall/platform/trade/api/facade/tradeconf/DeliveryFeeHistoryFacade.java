package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 说明： 商家物流费用配置记录
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "商家物流费用配置记录", tags = "商家物流费用配置记录")
public interface DeliveryFeeHistoryFacade {

    @ApiOperation("查询商家物流费用配置记录list")
    RpcResponse<List<DeliveryFeeHistoryDTO>> queryDeliveryFee(DeliveryFeeHistoryRpcReq req);

    @ApiOperation("保存商家物流费用配置记录")
    RpcResponse<DeliveryFeeHistoryDTO> saveDeliveryFeeHistory(DeliveryFeeHistoryRpcReq req);
}
