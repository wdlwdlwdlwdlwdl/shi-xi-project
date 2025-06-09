package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderType;
import com.aliyun.gts.gmall.center.trade.core.constants.ExtErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherJudgementService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.MixOrderSplitService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PresaleDomainService;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderBizCodeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 识别业务身份
 */
@Slf4j
@Extension(points = {OrderBizCodeExt.class})
public class MatchAllOrderBizCodeExt extends DefaultOrderBizCodeExt {

    @Autowired
    private EvoucherJudgementService evoucherJudgementService;

    @Autowired
    private PresaleDomainService presaleDomainService;

    @Autowired
    private MixOrderSplitService mixOrderSplitService;

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }

    /**
     * 商品计算后的扩展方法
     * @param req
     * @return
     */
    @Override
    public TradeBizResult<OrderBizCode> getBizCodesFromItem(OrderBizCodeReq req) {
        Set<Integer> uniqueCheck = new HashSet<>();
        int uniqueOrderNum = 0;
        TradeBizResult result = null;
        CreatingOrder ord = req.getOrder();
        for (MainOrder mainOrder : ord.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                ItemSku itemSku = subOrder.getItemSku();
                // 电子凭证
                if (evoucherJudgementService.isEvItem(itemSku)) {
                    if (mixOrderSplitService.isCheckSplit(ord)) {
                        mixOrderSplitService.markAlone(subOrder, ExtBizCode.EVOUCHER);
                    }
                    if (result == null) {
                        result = TradeBizResult.ok(OrderBizCode.builder()
                            .bizCodes(Arrays.asList(ExtBizCode.EVOUCHER))
                            .orderType(ExtOrderType.EVOUCHER.getCode())
                            .build());
                    }
                    uniqueCheck.add(100);
                    uniqueOrderNum++;
                }
                // 其他情况
                else {
                    uniqueCheck.add(1);
                }
            }
        }
        if ((uniqueCheck.size() > 1 || uniqueOrderNum > 1)
            && !mixOrderSplitService.isCheckSplit(ord)) {
            return TradeBizResult.fail(ExtErrorCode.ORDER_WITH_MIX_ITEM);
        }
        if (result != null) {
            return result;
        }
        // 普通订单
        return super.getBizCodesFromItem(req);
    }

    /***
     * 营销计算后的扩展方法
     * @param req
     * @return
     */
    @Override
    public TradeBizResult<OrderBizCode> getBizCodesFromPromotion(OrderBizCodeReq req) {
        Set<Integer> uniqueCheck = new HashSet<>();
        int uniqueOrderNum = 0;
        TradeBizResult result = null;
        CreatingOrder ord = req.getOrder();
        for (MainOrder mainOrder : ord.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                // 预售
                if (presaleDomainService.isPresaleItem(subOrder.getPromotions())) {
                    if (mixOrderSplitService.isCheckSplit(ord)) {
                        mixOrderSplitService.markAlone(subOrder, ExtBizCode.PRE_SALE);
                    }
                    if (result == null) {
                        result = TradeBizResult.ok(OrderBizCode.builder()
                            .bizCodes(Arrays.asList(ExtBizCode.PRE_SALE))
                            .orderType(OrderTypeEnum.MULTI_STEP_ORDER.getCode())
                            .build());
                    }
                    uniqueCheck.add(100);
                    uniqueOrderNum++;
                }
                // 其他情况
                else {
                    uniqueCheck.add(1);
                }
            }
        }
        if ((uniqueCheck.size() > 1 || uniqueOrderNum > 1) &&
            !mixOrderSplitService.isCheckSplit(ord)) {
            return TradeBizResult.fail(ExtErrorCode.ORDER_WITH_MIX_ITEM);
        }
        if (result != null) {
            return result;
        }
        // 取商品身份
        return super.getBizCodesFromItem(req);
    }
}
