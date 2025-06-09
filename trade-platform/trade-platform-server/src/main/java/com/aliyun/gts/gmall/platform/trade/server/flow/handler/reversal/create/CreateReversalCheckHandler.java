package com.aliyun.gts.gmall.platform.trade.server.flow.handler.reversal.create;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalCheckResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * step2
 * 退单流程 -- 业务校验
 * @anthor shifeng
 * @version 1.0.1
 * 2025-3-17 14:08:25
 */
@Component
public class CreateReversalCheckHandler extends AdapterHandler<TReversalCreate> {

    @Autowired
    private ReversalCreateService reversalCreateService;

    @Override
    public void handle(TReversalCreate inbound) {
        MainReversal reversal = inbound.getDomain();
        // 用户校验
        reversalCreateService.checkCustomer(reversal);
        // 没有可退子订单
        if (CollectionUtils.isEmpty(reversal.getSubReversals())) {
            throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_ILLEGAL_ORDER_STATUS);
        }
        //校验申请售后商品可退款是否一致 可退与不可退不能同时申请
        if(checkItemCanRefunds(reversal.getSubReversals())){
            throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_ILLEGAL_ITEM_STATUS);
        }
        // 订单状态校验
        assertEmpty(reversalCreateService.checkStatus(reversal));
        // 有效时间校验
        assertEmpty(reversalCreateService.checkTime(reversal));
        // 商品数量校验
        //reversalCreateService.checkCancelQty(reversal);
        // 取消金额校验 TODO 全是旧的逻辑 要重构
        reversalCreateService.checkCancelAmt(reversal);
    }

    private static void assertEmpty(List<ReversalCheckResult> fails) {
        if (fails != null && !fails.isEmpty()) {
            throw new GmallException(fails.get(0).getErrCode());
        }
    }

    public static boolean checkItemCanRefunds(List<SubReversal> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        Integer canRefunds = list.get(0).getSubOrder().getOrderAttr().getCanRefunds();
        for (SubReversal subReversal : list) {
            if (!canRefunds.equals(subReversal.getSubOrder().getOrderAttr().getCanRefunds())) {
                return true;
            }
        }
        return false;
    }

}
