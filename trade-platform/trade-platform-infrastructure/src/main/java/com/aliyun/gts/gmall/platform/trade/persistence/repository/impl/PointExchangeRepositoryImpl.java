package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcPointConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PointExchangeRepositoryImpl implements PointExchangeRepository {
    private final Cache<Integer, PointExchange> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS).build();

    @Autowired
    private PromotionConfigFacade promotionConfigFacade;

    @Value("${trade.point.maxDeductRate:1000}")
    private Integer maxDeductRate;

    @Override
    public PointExchange getExchangeRate() {
        try {
            return cache.get(0, this::getNoCache);
        } catch (ExecutionException e) {
            return ErrorUtils.throwUndeclared(e);
        }
    }

    private PointExchange getNoCache() {
        RpcResponse<AcPointConfigDTO> resp = RpcUtils.invokeRpc(
                () -> promotionConfigFacade.queryAccountPointConfig(),
                "promotionConfigFacade.queryAccountPointConfig",
                I18NMessageUtils.getMessage("query.points.rate"), null);  //# "查积分汇率"

        // 1积分 = ?多少元
        BigDecimal displayEx = resp.getData().getDeductPointValue();

        // 换算成  1积分 = ?人民币分
        Integer pointEx = null;
        if (displayEx != null) {
            pointEx = displayEx.multiply(BigDecimal.valueOf(100)).intValue();
        }

        return PointExchange.builder()
                .supportPoint(resp.getData().getTradeDeductPoint())
                .exchangeRate(pointEx)
                .maxDeductRate(resp.getData().getDeductPointPercent() * 10)
                .build();
    }
}
