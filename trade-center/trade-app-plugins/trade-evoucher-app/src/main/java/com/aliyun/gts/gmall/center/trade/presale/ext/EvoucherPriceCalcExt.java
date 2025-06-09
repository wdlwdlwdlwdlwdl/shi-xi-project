package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonPriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 电子凭证价格计算, 跳过运费查询并设为0,
 */
@Slf4j
@Extension(points = {PriceCalcExt.class})
public class EvoucherPriceCalcExt extends CommonPriceCalcExt {

    @Override
    protected void processFreight(CreatingOrder order, QueryFrom from) {
        order.getOrderPrice().setFreightAmt(0L);
        for (MainOrder main : order.getMainOrders()) {
            main.getOrderPrice().setFreightAmt(0L);
            for (SubOrder sub : main.getSubOrders()) {
                sub.getOrderPrice().setFreightAmt(0L);
            }
        }
    }
}
