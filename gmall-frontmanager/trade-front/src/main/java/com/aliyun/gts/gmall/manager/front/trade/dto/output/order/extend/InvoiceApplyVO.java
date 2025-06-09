package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import lombok.Data;

@Data
public class InvoiceApplyVO {
    Long result;

    String requestNo;

    String errorMsg;

    boolean success = true;
}
