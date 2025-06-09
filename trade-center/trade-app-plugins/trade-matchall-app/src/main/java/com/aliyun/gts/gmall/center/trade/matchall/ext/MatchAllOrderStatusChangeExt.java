package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 处理对公支付、账期支付 订单状态
 */
@Slf4j
@Extension(points = {OrderStatusChangeExt.class})
public class MatchAllOrderStatusChangeExt extends DefaultOrderStatusChangeExt {

    @Override
    public Integer getOrderStatusOnPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        if(PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(orderPay.getPayChannel())){
            return OrderStatusEnum.WAIT_SELLER_CONFIRM.getCode();
        }
        if(PayChannelEnum.CAT.getCode().equals(orderPay.getPayChannel())){
            return OrderStatusEnum.WAIT_BUYER_TRANSFERED.getCode();
        }
        if(PayChannelEnum.EPAY.getCode().equals(orderPay.getPayChannel())){
            return OrderStatusEnum.PAYMENT_CONFIRMED.getCode();
        }
        return super.getOrderStatusOnPaySuccess(mainOrder, orderPay);
    }
}
