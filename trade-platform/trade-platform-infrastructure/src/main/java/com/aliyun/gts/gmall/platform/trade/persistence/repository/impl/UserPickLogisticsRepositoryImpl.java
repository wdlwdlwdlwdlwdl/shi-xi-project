package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcUserPickLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserPickLogisticsRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.UserPickLogisticsMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserPickLogisticsRepositoryImpl implements UserPickLogisticsRepository {

    @Autowired
    private UserPickLogisticsMapper userPickLogisticsMapper;

    /**
     * 保存用户地点信息
     * @param userPickLogisticsDO
     */
    public void insert(TcUserPickLogisticsDO userPickLogisticsDO) {
        userPickLogisticsMapper.insert(userPickLogisticsDO);
    }

    /**
     * 查询用户接受的地点信息
     * @param userPickLogisticsDO
     */
    public List<TcUserPickLogisticsDO> query(TcUserPickLogisticsDO userPickLogisticsDO) {
        LambdaQueryWrapper<TcUserPickLogisticsDO> userPickLogisticsDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        userPickLogisticsDOLambdaQueryWrapper.eq(TcUserPickLogisticsDO::getCustId, userPickLogisticsDO.getCustId());
        userPickLogisticsDOLambdaQueryWrapper.eq(TcUserPickLogisticsDO::getDeliveryType, userPickLogisticsDO.getDeliveryType());
        return userPickLogisticsMapper.selectList(userPickLogisticsDOLambdaQueryWrapper);
    }

}
