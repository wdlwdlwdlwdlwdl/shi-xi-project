package com.aliyun.gts.gmall.center.trade.core.extension.common;

import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGiftService;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderSaveExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
public class CommonOrderSaveExt extends DefaultOrderSaveExt {

    @Autowired
    private ManzengGiftService manzengGiftService;

    @Override
    public Map beforeSaveOrder(CreatingOrder order) {
        // 满赠钩子
        manzengGiftService.beforeSave(order);
        return super.beforeSaveOrder(order);
    }
}
