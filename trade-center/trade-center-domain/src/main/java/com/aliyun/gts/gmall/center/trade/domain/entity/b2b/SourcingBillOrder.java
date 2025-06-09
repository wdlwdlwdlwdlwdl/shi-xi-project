package com.aliyun.gts.gmall.center.trade.domain.entity.b2b;

import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo.SourcingItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.Data;

import java.util.List;

@Data
public class SourcingBillOrder {

    private SourcingBillInfo bill;
    private List<OrderInfo> orders; // 下单的订单

    @Data
    public static class OrderInfo {
        private SourcingItem item;
        private SubOrder subOrder;
    }
}
