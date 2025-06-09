package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.UserPickLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.UserPickLogisticsService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcUserPickLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserPickLogisticsRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserPickLogisticsServiceImpl implements UserPickLogisticsService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserPickLogisticsRepository userPickLogisticsRepository;

    /**
     * 保存用户地点信息
     * @param userPickLogisticsRpcReq
     */
    @Override
    public void insert(UserPickLogisticsRpcReq userPickLogisticsRpcReq) {
        if (Boolean.FALSE.equals(existUserPick(userPickLogisticsRpcReq.getCustId(), userPickLogisticsRpcReq.getDeliveryType()))) {
            return;
        }
        TcUserPickLogisticsDO userPickLogisticsDO = new TcUserPickLogisticsDO();
        userPickLogisticsDO.setCustId(userPickLogisticsRpcReq.getCustId());
        userPickLogisticsDO.setDeliveryType(userPickLogisticsRpcReq.getDeliveryType());
        userPickLogisticsDO.setGmtCreate(new Date());
        userPickLogisticsDO.setGmtModified(new Date());
        userPickLogisticsRepository.insert(userPickLogisticsDO);
        cacheManager.set(
            String.format(CommonConstant.USER_PICK_LOGISTICS, userPickLogisticsRpcReq.getCustId(), userPickLogisticsRpcReq.getDeliveryType()),
            JSON.toJSONString(Boolean.TRUE)
        );
    }


    @Override
    public Boolean existUserPick(Long custId, String deliveryType) {
        TcUserPickLogisticsDO userPickLogisticsDO = new TcUserPickLogisticsDO();
        userPickLogisticsDO.setCustId(custId);
        userPickLogisticsDO.setDeliveryType(deliveryType);
        String cacheValue = cacheManager.get(String.format(CommonConstant.USER_PICK_LOGISTICS, custId, deliveryType));
        if (StringUtils.isNotEmpty(cacheValue)) {
            return Boolean.FALSE;
        }
        List<TcUserPickLogisticsDO> userPickLogisticsDOS = userPickLogisticsRepository.query(userPickLogisticsDO);
        if (CollectionUtils.isNotEmpty(userPickLogisticsDOS)) {
            cacheManager.set(
                String.format(CommonConstant.USER_PICK_LOGISTICS, custId, deliveryType),
                JSON.toJSONString(Boolean.TRUE)
            );
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
