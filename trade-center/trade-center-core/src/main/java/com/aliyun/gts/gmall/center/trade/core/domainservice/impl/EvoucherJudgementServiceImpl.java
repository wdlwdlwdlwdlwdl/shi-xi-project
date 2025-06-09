package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.item.common.enums.ItemType;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherJudgementService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import org.springframework.stereotype.Service;

@Service
public class EvoucherJudgementServiceImpl implements EvoucherJudgementService {

    @Override
    public boolean isEvItem(ItemSku itemSku) {
        return itemSku.getItemType() != null &&
            itemSku.getItemType() == ItemType.EVOUCHER.getType().intValue();
    }

    @Override
    public boolean isEvOrder(MainOrder mainOrder) {
        return mainOrder.getBizCodes() != null &&
                mainOrder.getBizCodes().contains(ExtBizCode.EVOUCHER);
    }
}
