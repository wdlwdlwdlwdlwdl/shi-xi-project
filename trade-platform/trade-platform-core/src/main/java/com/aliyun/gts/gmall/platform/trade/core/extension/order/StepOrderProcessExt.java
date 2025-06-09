package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;

public interface StepOrderProcessExt extends IExtensionPoints {

    void onPaySuccess(MainOrder mainOrder, OrderPay orderPay);

    void onSellerHandle(MainOrder mainOrder, StepOrderHandleRpcReq req);

    void onCustomerConfirm(MainOrder mainOrder, StepOrderHandleRpcReq req);
}
