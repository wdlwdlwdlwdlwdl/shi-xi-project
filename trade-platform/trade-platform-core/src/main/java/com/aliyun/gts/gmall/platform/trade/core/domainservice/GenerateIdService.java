package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;

import java.util.List;

public interface GenerateIdService {

    /**
     * 订单ID, 返回count+1个, 其中 [0]是主订单ID, 1~count是子订单ID
     * 可从子订单ID中 获取主订单ID
     *
     * @see OrderIdUtils
     */
    List<Long> nextOrderIds(Long custId, int count);

    // 生成规则: [seq]
    String nextDisplayOrderIds();

    /**
     * 售后单ID, 返回count+1个, 其中 [0]是主单ID, 1~count是子单ID
     */
    List<Long> nextReversalIds(Long custId, int count);

    /**
     * 退款单ID
     */
    Long nextRefundId(Long custId);

    /**
     * 退款明细ID
     */
    Long nextRefundDetailId(Long custId);
}
