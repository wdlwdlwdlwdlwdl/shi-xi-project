package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepRefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import lombok.Data;

import java.util.Map;

@Data
public class SubRefundFee {
    private static final Integer TYPE_SUB_ORD = 1;
    private static final Integer TYPE_FREIGHT = 2;

    private Long subReversalId;
    private Long subOrderId;
    private Integer type;
    private Long cancelAmt;
    private Integer cancelQty;
    private StepRefundFee refundFee;
    private OrderPrice orderPrice;
    private Integer orderQty;
    private SubOrder revSubOrder;  // isSubReversal=true 才有

    public boolean isSubReversal() {
        return TYPE_SUB_ORD.equals(type);
    }

    public boolean isFreight() {
        return TYPE_FREIGHT.equals(type);
    }

    protected void fillBack() {

    }

    public void setCancelAmt(Long cancelAmt) {
        this.cancelAmt = cancelAmt;
        fillBack();
    }

    public void setCancelQty(Integer cancelQty) {
        this.cancelQty = cancelQty;
        fillBack();
    }

    public static SubRefundFee subReversal(SubReversal o) {
        SubRefundFee t = new SubRefundFee() {
            @Override
            protected void fillBack() {
                o.setCancelAmt(getCancelAmt());
                o.setCancelQty(getCancelQty());
            }
        };
        t.type = TYPE_SUB_ORD;
        t.subReversalId = o.getReversalId();
        t.subOrderId = o.getSubOrder().getOrderId();
        t.cancelAmt = o.getCancelAmt();
        t.cancelQty = o.getCancelQty();
        t.refundFee = o.reversalFeatures();
        t.orderPrice = o.getSubOrder().getOrderPrice();
        t.orderQty = o.getSubOrder().getOrderQty();
        t.revSubOrder = o.getSubOrder();
        return t;
    }

    public static SubRefundFee freight(MainReversal o) {
        if (o.reversalFeatures().getCancelFreightFee() == null) {
            o.reversalFeatures().setCancelFreightFee(new StepRefundFee());  // 兼容旧数据
        }
        SubRefundFee t = new SubRefundFee() {
            @Override
            protected void fillBack() {
                o.reversalFeatures().setCancelFreightAmt(getCancelAmt());
            }
        };
        t.type = TYPE_FREIGHT;
        if (o.getPrimaryReversalId() != null) {
            t.subReversalId = OrderIdUtils.getFreightOrderId(o.getPrimaryReversalId());
        }
        t.subOrderId = OrderIdUtils.getFreightOrderId(o.getMainOrder().getPrimaryOrderId());
        t.cancelAmt = o.reversalFeatures().getCancelFreightAmt();
        t.cancelQty = 0;
        t.refundFee = o.reversalFeatures().getCancelFreightFee();
        t.orderPrice = o.getMainOrder().orderAttr().getFreightPrice();
        t.orderQty = 1;
        return t;
    }
}
