package com.aliyun.gts.gmall.manager.front.promotion.facade;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.PointExchangeCouponQuery;

public interface PointExchangeCouponFacade {

     RestResponse<Boolean> pointExchangeCoupon(PointExchangeCouponQuery pointExchangeCouponQuery);

}
