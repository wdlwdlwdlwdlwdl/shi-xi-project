package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 说明： 包邮配置
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = " 包邮配置", tags = " 包邮配置")
public interface FeeRulesFacade {

    @ApiOperation("查询 包邮配置list")
    RpcResponse<PageInfo<FeeRulesDTO>> queryFeeRules(FeeRulesQueryRpcReq req);

    @ApiOperation("保存 包邮配置")
    RpcResponse<FeeRulesDTO> saveFeeRules(FeeRulesRpcReq req);

    @ApiOperation("更新 包邮配置")
    RpcResponse<FeeRulesDTO> updateFeeRules(FeeRulesRpcReq req);

    @ApiOperation("查看 包邮配置")
    RpcResponse<FeeRulesDTO> feeRulesDetail(FeeRulesRpcReq req);

    RpcResponse<FeeRulesDTO> queryfeeRules();
}
