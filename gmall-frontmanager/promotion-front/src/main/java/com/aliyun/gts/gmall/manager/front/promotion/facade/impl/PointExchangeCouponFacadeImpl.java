package com.aliyun.gts.gmall.manager.front.promotion.facade.impl;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.promotion.adaptor.PointExchangeCouponAdaptor;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.PointExchangeCouponQuery;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PointExchangeCouponFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PointExchangeCouponFacadeImpl implements PointExchangeCouponFacade {
    @Autowired
    private PointExchangeCouponAdaptor pointExchangeCouponAdaptor;

    @Override
    public RestResponse<Boolean> pointExchangeCoupon(PointExchangeCouponQuery pointExchangeCouponQuery) {
        return pointExchangeCouponAdaptor.freezePoint(pointExchangeCouponQuery);
    }
}
