package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonOrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 预售下单校验
 */
@Slf4j
@Extension(points = {OrderBizCheckExt.class})
public class PresaleOrderBizCheckExt extends CommonOrderBizCheckExt {

    @Override
    public TradeBizResult checkOnConfirmOrder(CreatingOrder order) {
        return super.checkOnConfirmOrder(order);
    }

    @Override
    public TradeBizResult checkOnCreateOrder(CreatingOrder order) {
        return super.checkOnCreateOrder(order);
    }
}
