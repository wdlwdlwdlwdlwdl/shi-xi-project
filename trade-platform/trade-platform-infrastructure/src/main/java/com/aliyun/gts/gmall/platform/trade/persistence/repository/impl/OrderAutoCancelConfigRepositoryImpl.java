package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderAutoCancelConfigDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderAutoCancelConfigRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.OrderAutoCancelConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "irderAutoCancelConfigRespository", havingValue = "default", matchIfMissing = true)
public class OrderAutoCancelConfigRepositoryImpl implements OrderAutoCancelConfigRepository {

    @Autowired
    private OrderAutoCancelConfigMapper orderAutoCancelConfigMapper;

    /**
     * 通过订单状态获取原因
     * @param orderStatus
     * @return
     */
    public TcOrderAutoCancelConfigDO getCancelCodeByOrderStatus(Integer orderStatus) {
        LambdaQueryWrapper<TcOrderAutoCancelConfigDO> tcOrderAutoCancelConfigDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        tcOrderAutoCancelConfigDOLambdaQueryWrapper.eq(TcOrderAutoCancelConfigDO::getDeleted, 0);
        tcOrderAutoCancelConfigDOLambdaQueryWrapper.eq(TcOrderAutoCancelConfigDO::getOrderStatus, orderStatus);
        List<TcOrderAutoCancelConfigDO> tcOrderAutoCancelConfigDOS = orderAutoCancelConfigMapper.selectList(tcOrderAutoCancelConfigDOLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(tcOrderAutoCancelConfigDOS)) {
            return tcOrderAutoCancelConfigDOS.get(0);
        }
        return null;
    }
}
