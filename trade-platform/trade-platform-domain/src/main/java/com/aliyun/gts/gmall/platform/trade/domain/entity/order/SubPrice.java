package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.SeparateRule;
import lombok.Data;

@Data
public class SubPrice {
    private static final Integer TYPE_SUB_ORD = 1;
    private static final Integer TYPE_FREIGHT = 2;

    private Long subOrderId;
    private Integer type;
    private OrderPrice orderPrice;
    private SeparateRule separateRule;

    public boolean isSubOrder() {
        return TYPE_SUB_ORD.equals(type);
    }

    public boolean isFreight() {
        return TYPE_FREIGHT.equals(type);
    }

    public static SubPrice subOrder(SubOrder o) {
        SubPrice t = new SubPrice();
        t.type = TYPE_SUB_ORD;
        t.subOrderId = o.getOrderId();
        t.orderPrice = o.getOrderPrice();
        t.separateRule = o.orderAttr().getSeparateRule();
        return t;
    }

    public static SubPrice freight(MainOrder o) {
        if (o.orderAttr().getFreightPrice() == null) {
            o.orderAttr().setFreightPrice(new OrderPrice());  // 兼容旧数据
        }
        SubPrice t = new SubPrice();
        t.type = TYPE_FREIGHT;
        if (o.getPrimaryOrderId() != null) {
            t.subOrderId = OrderIdUtils.getFreightOrderId(o.getPrimaryOrderId());
        }
        t.orderPrice = o.orderAttr().getFreightPrice();
        return t;
    }
}
