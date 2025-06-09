package com.aliyun.gts.gmall.platform.trade.core.extension.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalAgreeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalDeliverRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalModifyRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalRefuseRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import io.swagger.annotations.ApiOperation;

/**
 * 售后单状态推进处理
 */
public interface ReversalProcessExt extends IExtensionPoints {

    @ApiOperation("卖家同意售后")
    void sellerAgree(ReversalAgreeRpcReq req, MainReversal reversal) ;

    @ApiOperation("卖家不同意售后,并关闭")
    void sellerRefuse(ReversalRefuseRpcReq req, MainReversal reversal);

    @ApiOperation("顾客取消售后")
    void customerCancel(ReversalModifyRpcReq req, MainReversal reversal);

    @ApiOperation("顾客发货")
    void customerSendDeliver(ReversalDeliverRpcReq req, MainReversal reversal);

    @ApiOperation("卖家确认收货")
    void sellerConfirmDeliver(ReversalModifyRpcReq req, MainReversal reversal);

    @ApiOperation("卖家拒绝收货,并关闭")
    void sellerRefuseDeliver(ReversalRefuseRpcReq req, MainReversal reversal);

    @ApiOperation("退款成功后，处理售后单、订单的状态")
    void refundSuccess(MainReversal reversal, Integer stepNo);

    @ApiOperation("买家确认收到退款")
    void buyerConfirmRefund(MainReversal reversal);

    @ApiOperation("运营同意售后")
    void agreeByOperation(ReversalAgreeRpcReq req, MainReversal reversal);

    @ApiOperation("系统自动取消售后")
    void cancelBySystem(ReversalModifyRpcReq req, MainReversal reversal);

}
