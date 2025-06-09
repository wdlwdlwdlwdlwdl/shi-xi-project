package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundDetailUpdateWrapper {

    private List<Long> refundDetailIds;

    private Long custId;

    private String refundDetailStatus;
}
