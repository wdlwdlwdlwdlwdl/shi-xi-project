package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ReversalCheckResult {

    private long orderId;
    private ResponseCode errCode;

    public static List<ReversalCheckResult> allSub(MainReversal main, ResponseCode code) {
        return main.getSubReversals().stream().map(
                sub -> ReversalCheckResult.builder()
                        .orderId(sub.getSubOrder().getOrderId())
                        .errCode(code).build()
        ).collect(Collectors.toList());
    }

    public static ReversalCheckResult of(SubReversal sub, ResponseCode code) {
        return ReversalCheckResult.builder()
                .orderId(sub.getSubOrder().getOrderId())
                .errCode(code).build();
    }
}
