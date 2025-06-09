package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalCheckResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;

import java.util.List;

public interface ReversalCreateService {

    /**
     * 填充订单信息
     */
    void fillOrderInfo(MainReversal reversal);

    /**
     * 发起售后用户校验
     */
    void checkCustomer(MainReversal reversal);

    /**
     * 发起售后时间校验
     */
    List<ReversalCheckResult> checkTime(MainReversal reversal);

    /**
     * 发起售后状态校验
     */
    List<ReversalCheckResult> checkStatus(MainReversal reversal);

    /**
     * 发起售后金额校验
     */
    void checkCancelAmt(MainReversal reversal);

    /**
     * 发起售后数量校验
     */
    void checkCancelQty(MainReversal reversal);

    /**
     * 发起售后原因校验
     */
    void checkReversalReason(MainReversal reversal);

    /**
     * 生成ID
     */
    void generateReversalIds(MainReversal reversal);

    /**
     * 保存售后单（含退款单）信息
     */
    void saveReversal(MainReversal reversal);

    /**
     * 系统自动生成全额退款逆向单（该方法已落db并发送退款消息）
     */
    MainReversal createSystemRefund(SystemRefund sys);
}
