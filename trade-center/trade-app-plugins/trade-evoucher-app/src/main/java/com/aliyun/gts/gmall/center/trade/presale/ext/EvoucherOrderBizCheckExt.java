package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.constants.EvoucherErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherJudgementService;
import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonOrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 电子凭证下单校验
 * 1. 不允许与实物商品合并下单
 * 2. 子订单件数 < {limitQty}
 */
@Slf4j
@Extension(points = {OrderBizCheckExt.class})
public class EvoucherOrderBizCheckExt extends CommonOrderBizCheckExt {

    @Autowired
    private EvoucherJudgementService evoucherJudgementService;

    @Value("${trade.center.evoucher.limitQty:10}")
    private Integer limitQty;

    /**
     * 订单确认check
     * @param order
     * @return
     */
    @Override
    public TradeBizResult checkOnConfirmOrder(CreatingOrder order) {
        TradeBizResult tradeBizResult = super.checkOnConfirmOrder(order);
        if (!tradeBizResult.isSuccess()) {
            return tradeBizResult;
        }
        return checkItems(order);
    }

    /**
     * 订单创建check
     * @param order
     * @return
     */
    @Override
    public TradeBizResult checkOnCreateOrder(CreatingOrder order) {
        TradeBizResult tradeBizResult = super.checkOnCreateOrder(order);
        if (!tradeBizResult.isSuccess()) {
            return tradeBizResult;
        }
        return checkItems(order);
    }

    /**
     * 电子商品校验
     *    电子商品和普通商品不能一起下单
     *    电子商品购买数量有上线
     * @param order
     * @return
     */
    private TradeBizResult checkItems(CreatingOrder order) {
        for (MainOrder mainOrder : order.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                ItemSku itemSku = subOrder.getItemSku();
                if (Boolean.FALSE.equals(evoucherJudgementService.isEvItem(itemSku))) {
                    return TradeBizResult.fail(EvoucherErrorCode.ORDER_WITH_MIX_ITEM);
                }
                //if (subOrder.getOrderQty().intValue() > limitQty) {
                //    return TradeBizResult.fail(EvoucherErrorCode.ORDER_QTY_OUT_LIMIT);
                //}
            }
        }
        return TradeBizResult.ok();
    }
}
