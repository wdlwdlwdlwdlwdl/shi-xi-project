package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookFailureDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AcBookRecordQuery;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AccountBookQuery;
import org.springframework.stereotype.Component;

@Component
public class MockAccountBookReadFacade implements AccountBookReadFacade {
    @Override
    public RpcResponse<Long> queryTotalAssets(AccountBookQuery accountBookQuery) {
        return RpcResponse.ok(1000000L);
    }

    @Override
    public RpcResponse<AcBookFailureDTO> queryLastFailed(AccountBookQuery accountBookQuery) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<AcBookRecordDTO>> queryRecords(AcBookRecordQuery acBookRecordQuery) {
        return null;
    }

    @Override
    public RpcResponse<AcBookRecordDTO> queryRecord(Long id) {
        return null;
    }

}
