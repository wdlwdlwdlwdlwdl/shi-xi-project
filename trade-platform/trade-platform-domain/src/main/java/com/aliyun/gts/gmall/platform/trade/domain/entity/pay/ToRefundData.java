package com.aliyun.gts.gmall.platform.trade.domain.entity.pay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToRefundData {

    private String refundId;
}
