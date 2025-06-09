package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.SellerConfirmWriteRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

public interface OrderSynService {

    /**
     * 支付成功同步halyk
     */
    void synCreateOrder(MainOrder mainOrder, SellerConfirmWriteRpcReq req);

    /**
     * 订单确认同步halyk
     */
    void synConfirmOrder(TcOrderDO mainOrder, SellerConfirmWriteRpcReq req);

    /**
     * 物流确认订单
     */
    void deliveryConfirm(MainOrder mainOrder, SellerConfirmWriteRpcReq req);
}
