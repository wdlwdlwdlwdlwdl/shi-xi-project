package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.item.api.dto.output.DepositConfigDTO;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.center.item.common.enums.ItemType;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.core.constants.DepositErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.DepositDomainService;
import com.aliyun.gts.gmall.center.trade.core.util.ItemUtils;
import com.aliyun.gts.gmall.center.trade.domain.entity.deposit.ItemDepositInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DepositDomainServiceImpl implements DepositDomainService {

    @Override
    public boolean isDepositItem(ItemSku itemSku) {
        return ItemType.DEPOSIT.getType().equals(itemSku.getItemType());
    }

    @Override
    public ItemDepositInfo getDepositFromItems(MainOrder mainOrder) {
        long sumAmt = 0L;
        int minDays = Integer.MAX_VALUE;
        Long[] subOrderPrice = new Long[mainOrder.getSubOrders().size()];
        int idx = 0;
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            DepositConfigDTO d = getItemDepositInfo(subOrder.getItemSku());
            if (d == null || d.getType() == null || d.getDay() == null) {
                throw new GmallException(DepositErrorCode.ITEM_INFO_ERROR);
            }
            // 比例
            if (d.getType().intValue() == DepositConfigDTO.TYPE_1) {
                long amt = BigDecimal.valueOf(subOrder.getOrderPrice().getOrderTotalAmt())
                        .subtract(BigDecimal.valueOf(subOrder.getOrderPrice().getFreightAmt()))
                        .multiply(BigDecimal.valueOf(d.getRatio())).longValue();
                sumAmt += amt;
                subOrderPrice[idx] = amt;
            }
            // 固定金额
            else if (d.getType().intValue() == DepositConfigDTO.TYPE_2) {
                long amt = d.getAmount() * subOrder.getOrderQty().intValue();
                sumAmt += amt;
                subOrderPrice[idx] = amt;
            }
            // 异常
            else {
                throw new GmallException(DepositErrorCode.ITEM_INFO_ERROR);
            }
            minDays = Math.min(minDays, d.getDay());
            idx++;
        }

        // 固定定金 > 订单总价, 则取订单总价为定金
        long orderTotal = mainOrder.getOrderPrice().getTotalAmt();
        if (sumAmt > orderTotal) {
            sumAmt = orderTotal;
        }
        return ItemDepositInfo.builder()
                .depositAmt(sumAmt)
                .subOrderDepositAmt(subOrderPrice)
                .tailDays(minDays)
                .build();
    }

    @Override
    public boolean isDepositOrder(MainOrder mainOrder) {
        return mainOrder.getBizCodes() != null &&
                mainOrder.getBizCodes().contains(ExtBizCode.DEPOSIT);
    }

    private static DepositConfigDTO getItemDepositInfo(ItemSku itemSku) {
        return ItemUtils.getExtendObject(itemSku, ItemExtendConstant.DEPOSIT_CONFIG, DepositConfigDTO.class);
    }
}
