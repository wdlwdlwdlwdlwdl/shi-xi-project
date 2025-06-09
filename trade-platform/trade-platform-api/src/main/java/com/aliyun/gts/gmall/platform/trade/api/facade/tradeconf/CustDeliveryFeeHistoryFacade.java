package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeHistoryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 说明： 客户物流费用配置记录
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "客户物流费用配置记录", tags = "客户物流费用配置记录")
public interface CustDeliveryFeeHistoryFacade {

    @ApiOperation("查询客户物流费用配置记录list")
    RpcResponse<List<CustDeliveryFeeHistoryDTO>> queryCustDeliveryFee(CustDeliveryFeeHistoryRpcReq req);

    @ApiOperation("保存客户物流费用配置记录")
    RpcResponse<CustDeliveryFeeHistoryDTO> saveCustDeliveryFeeHistory(CustDeliveryFeeHistoryRpcReq req);
}
