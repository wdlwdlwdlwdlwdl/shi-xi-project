package com.aliyun.gts.gmall.center.trade.core.ext.orderConfirm;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderSaveAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.OrderCreateServiceImpl;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Primary
@Component
public class OrderCreateServiceImplExt extends OrderCreateServiceImpl {

    private static final String EXT_IDEMPOTENT_KEY = "__gmall_createOrderKey";
    private static final String PRESSURE_TEST_TOKEN = "PRESSURE_ORDER_1c979a83-55ff-47ea-a351-8ae2a201cad1";

    private static final int LOCK_TIME_MINUTE = 30;
    private static final long LOCK_STATUS_1 = 1L;   // 未使用
    private static final long LOCK_STATUS_2 = 2L;   // 锁定中
    private static final long LOCK_STATUS_3 = 3L;   // 已使用

    @Autowired
    private CacheRepository cacheRepository;

    @Value("${trade.createOrder.useShortToken:true}")
    private Boolean useShortToken;

    @Override
    public String getOrderToken(CreatingOrder order, String token) {
        // 幂等ID
        String cacheKey = (StringUtils.isEmpty(token) ?  UUID.randomUUID().toString() : token);
        String key = "CREATE_ORDER_" + cacheKey;
        cacheRepository.atomSetLong(key, LOCK_STATUS_1, LOCK_TIME_MINUTE, TimeUnit.MINUTES);
        order.putExtra(EXT_IDEMPOTENT_KEY, key);
        String fullToken = JSON.toJSONString(order);
        if (Boolean.TRUE.equals(useShortToken)) {
            cacheRepository.put(cacheKey, fullToken, 1, TimeUnit.HOURS);
            return cacheKey;
        }
        return fullToken;
    }
}
