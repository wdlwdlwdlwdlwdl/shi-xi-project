package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.core.domainservice.B2bSourcingDomainService;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderCreateEdgeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderCreateEdgeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Extension(points = {OrderCreateEdgeExt.class})
public class MatchAllOrderCreateEdgeExt extends DefaultOrderCreateEdgeExt {

    @Autowired
    private B2bSourcingDomainService b2bSourcingDomainService;

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }

    @Override
    public void beginCreate(CreatingOrder order) {
        super.beginCreate(order);
//        b2bSourcingDomainService.beginCreateOrder(order);
    }

    @Override
    public void orderSaved(CreatingOrder order) {
        super.orderSaved(order);
        b2bSourcingDomainService.endCreateOrder(order, true);
    }

    @Override
    public void failedWithoutSave(CreatingOrder order) {
        super.failedWithoutSave(order);
        b2bSourcingDomainService.endCreateOrder(order, false);
    }
}
