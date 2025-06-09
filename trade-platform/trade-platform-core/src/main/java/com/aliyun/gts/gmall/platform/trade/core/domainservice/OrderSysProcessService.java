package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;

public interface OrderSysProcessService {

    /**
     * 结束交易并退款
     */
    MainReversal systemRefund(SystemRefund refund);

    /**
     * 结束交易并打款
     */
    void systemConfirm(MainOrder mainOrder);
}
