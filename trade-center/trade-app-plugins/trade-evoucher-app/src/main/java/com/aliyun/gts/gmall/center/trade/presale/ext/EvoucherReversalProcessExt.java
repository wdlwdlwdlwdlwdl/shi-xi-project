package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.common.constants.EvoucherStatusEnum;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherProcessService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherQueryService;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 电子凭证售后处理
 * 1. 退货完成时销毁券码
 * 2. 退货后可用凭证数量为0 才完结订单
 */
@Slf4j
@Extension(points = {ReversalProcessExt.class})
public class EvoucherReversalProcessExt extends DefaultReversalProcessExt {

    @Autowired
    private EvoucherProcessService evoucherProcessService;
    @Autowired
    private EvoucherQueryService evoucherQueryService;

    @Override
    public void refundSuccess(MainReversal reversal, Integer stepNo) {
        // 退货的, 销毁券码
        if (ReversalTypeEnum.codeOf(reversal.getReversalType()) == ReversalTypeEnum.REFUND_ITEM) {
            disableEv(reversal);
        }

        super.refundSuccess(reversal, stepNo);
    }

    @Override
    protected Set<Long> getFinishOrdersOnSuccess(MainReversal re) {
        if (ReversalTypeEnum.codeOf(re.getReversalType()) != ReversalTypeEnum.REFUND_ITEM) {
            return super.getFinishOrdersOnSuccess(re);
        }

        // 退货的情况, 判断可用码是否为0
        Set<Long> finishOrders = new HashSet<>();
        for (SubReversal sub : re.getSubReversals()) {
            long subOrderId = sub.getSubOrder().getOrderId();
            List<Long> notUsed = queryNotUsedCode(subOrderId);
            if (notUsed.isEmpty()) {
                finishOrders.add(subOrderId);
            }
        }
        return finishOrders;
    }

    private void disableEv(MainReversal reversal) {
        for (SubReversal sub : reversal.getSubReversals()) {
            List<Long> notUsedCode = queryNotUsedCode(sub.getSubOrder().getOrderId());
            int count = sub.getCancelQty();
            for (Long code : notUsedCode) {
                if (count-- > 0) {
                    evoucherProcessService.makeDisabled(code);
                }
            }
        }
    }

    private List<Long> queryNotUsedCode(Long orderId) {
        EvoucherQueryRpcReq req = new EvoucherQueryRpcReq();
        req.setOrderId(orderId);
        List<EvoucherInstance> list = evoucherQueryService.queryEvouchers(req);

        List<Long> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (EvoucherInstance ev : list) {
            if (EvoucherStatusEnum.codeOf(ev.getStatus()) == EvoucherStatusEnum.NOT_USED) {
                result.add(ev.getEvCode());
            }
        }
        return result;
    }
}
