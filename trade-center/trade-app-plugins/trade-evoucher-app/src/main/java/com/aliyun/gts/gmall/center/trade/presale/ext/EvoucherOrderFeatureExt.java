package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherCreateService;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherTemplate;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 电子凭证下单打feature标: 电子凭证模版
 */
@Slf4j
@Extension(points = {OrderFeatureExt.class})
public class EvoucherOrderFeatureExt extends DefaultOrderFeatureExt {

    @Autowired
    private EvoucherCreateService evoucherCreateService;

    @Override
    public Map<String, String> getSubFeatureOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder) {
        Map<String, String> map = new HashMap<>();
//        EvoucherTemplate template = evoucherCreateService.getEvTemplate(subOrder.getItemSku());
//        evoucherCreateService.addEvFeature(map, template);
        return map;
    }
}
