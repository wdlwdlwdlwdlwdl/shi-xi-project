package com.aliyun.gts.gmall.platform.trade.core.task.param;

import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReversalTaskParam extends TaskParam {

    private Long primaryReversalId;

    public ReversalTaskParam(Long sellerId, Long primaryOrderId, Long primaryReversalId, List<BizCodeEntity> bizCodes) {
        super(sellerId, primaryOrderId, bizCodes);
        this.primaryReversalId = primaryReversalId;
    }
}
