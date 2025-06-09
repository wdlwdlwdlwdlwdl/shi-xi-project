package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.FeeRulesFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.FeeRulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明：包邮配置实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class FeeRulesFacadeImpl implements FeeRulesFacade {

    @Autowired
    private FeeRulesService feeRulesService;

    @Override
    public RpcResponse<PageInfo<FeeRulesDTO>> queryFeeRules(FeeRulesQueryRpcReq req) {
        return RpcResponse.ok(feeRulesService.queryFeeRules(req));
    }

    @Override
    public RpcResponse<FeeRulesDTO> saveFeeRules(FeeRulesRpcReq req) {
        FeeRulesDTO dto = feeRulesService.saveFeeRules(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<FeeRulesDTO> updateFeeRules(FeeRulesRpcReq req) {
        FeeRulesDTO dto = feeRulesService.updateFeeRules(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<FeeRulesDTO> feeRulesDetail(FeeRulesRpcReq req) {
        return RpcResponse.ok(feeRulesService.feeRulesDetail(req));
    }

    @Override
    public RpcResponse<FeeRulesDTO> queryfeeRules() {
        return RpcResponse.ok(feeRulesService.queryFeeRules());
    }
}
