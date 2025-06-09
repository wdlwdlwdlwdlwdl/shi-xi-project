package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.common.constants.BizCodeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultOrderBizCodeExt implements OrderBizCodeExt {

    @Override
    public TradeBizResult<OrderBizCode> getBizCodesFromItem(OrderBizCodeReq req) {
        return orElse(req, List.of(BizCodeEnum.NORMAL_TRADE), OrderTypeEnum.PHYSICAL_GOODS);
    }

    @Override
    public TradeBizResult<OrderBizCode> getBizCodesFromPromotion(OrderBizCodeReq req) {
        return orElse(req, List.of(BizCodeEnum.NORMAL_TRADE), OrderTypeEnum.PHYSICAL_GOODS);
    }

    protected TradeBizResult<OrderBizCode> orElse(OrderBizCodeReq req,
                                               List<BizCodeEnum> bizCode, OrderTypeEnum orderType) {
        OrderBizCode result = OrderBizCode.builder().build();

        if (CollectionUtils.isNotEmpty(req.getRequestBizCodes())) {
            result.setBizCodes(req.getRequestBizCodes());
        } else {
            result.setBizCodes(bizCode.stream().map(BizCodeEnum::getCode).collect(Collectors.toList()));
        }

        if (req.getRequestOrderType() != null) {
            result.setOrderType(req.getRequestOrderType());
        } else {
            result.setOrderType(orderType.getCode());
        }

        return TradeBizResult.ok(result);
    }
}
