package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.center.trade.matchall.util.SelfSellerUtils;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderSeparateFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderSeparateFeeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 自营卖家分账规则覆盖
 */
@Slf4j
@Extension(points = {OrderSeparateFeeExt.class})
public class MatchAllOrderSeparateFeeExt extends DefaultOrderSeparateFeeExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }

    @Override
    public void storeSeparateRule(MainOrder mainOrder) {
        if (SelfSellerUtils.isSelfSeller(mainOrder.getSeller())) {
            // 自营卖家, 不分账
            return;
        }

        super.storeSeparateRule(mainOrder);
    }
}
