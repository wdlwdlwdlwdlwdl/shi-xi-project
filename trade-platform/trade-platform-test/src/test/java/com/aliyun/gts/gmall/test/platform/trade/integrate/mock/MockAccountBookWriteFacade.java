package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookDeductDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockAccountBookWriteFacade implements AccountBookWriteFacade {
    @Override
    public RpcResponse<Boolean> deductAssets(AcBookDeductDTO acBookDeductDTO) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> deductReturn(AcBookDeductDTO acBookDeductDTO) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> freezeAssets(AcBookDeductDTO acBookDeductDTO) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> batchFreeze(List<AcBookDeductDTO> list) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> unFreeze(AcBookDeductDTO acBookDeductDTO) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> freezeDeduct(AcBookDeductDTO acBookDeductDTO) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> reviseFreeze(AcBookDeductDTO acBookDeductDTO) {
        return null;
    }

    @Override
    public RpcResponse<Boolean> grantAssets(AcBookRecordDTO acBookRecordDTO) {
        return null;
    }

    @Override
    public RpcResponse<Boolean> grantReturn(AcBookDeductDTO acBookDeductDTO) {
        return null;
    }

    @Override
    public RpcResponse<Long> grantReturnPositive(AcBookDeductDTO acBookDeductDTO) {
        return null;
    }

    @Override
    public RpcResponse<Boolean> updateGrantRecordReserveState(Long id, Integer oldReserveState, Integer newReserveState) {
        return null;
    }
}
