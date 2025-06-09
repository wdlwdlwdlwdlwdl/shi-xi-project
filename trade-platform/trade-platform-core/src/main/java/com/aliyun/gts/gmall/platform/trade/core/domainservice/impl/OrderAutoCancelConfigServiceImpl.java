package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderAutoCancelConfigService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderAutoCancelConfigDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderAutoCancelConfigRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class OrderAutoCancelConfigServiceImpl implements OrderAutoCancelConfigService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CancelReasonRepository cancelReasonRepository;

    @Autowired
    private OrderAutoCancelConfigRepository orderAutoCancelConfigRepository;

    /**
     * 根据订单状态读取取消原因
     * @param orderStatus
     * @return
     */
    public String getCancelCodeByOrderStatus(Integer orderStatus) {
        String cancelCode = cacheManager.get(String.format(CommonConstant.ORDER_AUTO_CANCEL, orderStatus));
        if (StringUtils.isNotEmpty(cancelCode)) {
            return cancelCode;
        }
        TcOrderAutoCancelConfigDO tcOrderAutoCancelConfigDO = orderAutoCancelConfigRepository.getCancelCodeByOrderStatus(orderStatus);
        if (Objects.nonNull(tcOrderAutoCancelConfigDO) &&
            StringUtils.isNotEmpty(tcOrderAutoCancelConfigDO.getCancelReasonCode())) {
            String cancelReasonCode  = cacheManager.get(String.format(CommonConstant.CANCEL_CODE, tcOrderAutoCancelConfigDO.getCancelReasonCode()));
            if (StringUtils.isNotEmpty(cancelReasonCode)) {
                cacheManager.set(String.format(CommonConstant.ORDER_AUTO_CANCEL, orderStatus), cancelReasonCode, 5, TimeUnit.MINUTES);
                return cancelReasonCode;
            }
            TcCancelReasonDO tcCancelReasonDO = cancelReasonRepository.queryTcCancelReasonByCode(tcOrderAutoCancelConfigDO.getCancelReasonCode());
            if (Objects.nonNull(tcCancelReasonDO)) {
                cacheManager.set(String.format(CommonConstant.ORDER_AUTO_CANCEL, orderStatus), tcCancelReasonDO.getCancelReasonCode(), 5, TimeUnit.MINUTES);
                return tcCancelReasonDO.getCancelReasonCode();
            }
        }
        return "";
    }
}
