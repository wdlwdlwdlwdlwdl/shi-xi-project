package com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalCreateExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultReversalCreateExt implements ReversalCreateExt {

    @Override
    public void beforeSave(MainReversal reversal) {
        ReversalTypeEnum type = ReversalTypeEnum.codeOf(reversal.getReversalType());
        if (type == ReversalTypeEnum.REFUND_ITEM) {
            reversal.reversalFeatures().setNeedLogistics(true);
        }
        else if (type == ReversalTypeEnum.REFUND_ONLY ||
            type == ReversalTypeEnum.APPLY_CANCEL_REFUND ||
            type == ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND) {
            reversal.reversalFeatures().setNeedLogistics(false);
        }
    }

    /**
     * 售后单保存之前
     */
    @Override
    public void fillReversalInfo(MainReversal reversal, CreateReversalRpcReq req) {

    }
}
