package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillOrder;

public interface B2bSourcingRepository {

    /**
     * 寻源决标单
     */
    SourcingBillInfo querySourcingBill(String billId);

    /**
     * 更新报价单、决标单下单状态
     */
    void saveOrder(SourcingBillOrder order);
}
