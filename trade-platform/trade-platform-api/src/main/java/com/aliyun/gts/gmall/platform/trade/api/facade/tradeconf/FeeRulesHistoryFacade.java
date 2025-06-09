package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesHistoryDTO;
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
@Api(value = "包邮配置记录", tags = "包邮配置记录")
public interface FeeRulesHistoryFacade {

    @ApiOperation("查询包邮配置记录list")
    RpcResponse<List<FeeRulesHistoryDTO>> queryFeeRules(FeeRulesHistoryRpcReq req);

    @ApiOperation("保存包邮配置记录")
    RpcResponse<FeeRulesHistoryDTO> saveFeeRulesHistory(FeeRulesHistoryRpcReq req);
}
