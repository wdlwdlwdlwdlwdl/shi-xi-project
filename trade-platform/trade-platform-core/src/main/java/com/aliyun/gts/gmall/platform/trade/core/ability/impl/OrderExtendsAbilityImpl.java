package com.aliyun.gts.gmall.platform.trade.core.ability.impl;

import com.aliyun.gts.gmall.framework.domain.extend.service.DomainExtendService;
import com.aliyun.gts.gmall.framework.domain.extend.utils.DomainExtendUtil;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderExtendsAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderExtendsExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderExtendsExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Ability(code = OrderExtendsAbilityImpl.ABILITY_NAME,
        fallback = DefaultOrderExtendsExt.class,
        description = "订单新增扩展表数据")
public class OrderExtendsAbilityImpl extends BaseAbility<BizCodeEntity, OrderExtendsExt>
        implements OrderExtendsAbility {

    public static final String ABILITY_NAME = "com.aliyun.gts.gmall.platform.trade.core.ability.impl.OrderExtendsAbilityImpl";

    @Autowired
    private DomainExtendService domainExtendService;

    @Override
    public void addExtendOnCrete(CreatingOrder order) {
        for (MainOrder main : order.getMainOrders()) {
            List<BizCodeEntity> bizCodes = BizCodeEntity.getOrderBizCode(main);
            for (BizCodeEntity bizCode : bizCodes) {
                executeExt(
                    bizCode,
                    extension -> extension.addExtendOnCrete(main, order),
                    OrderExtendsExt.class,
                    Reducers.firstOf(Objects::nonNull)
                );
            }
            if (CollectionUtils.isNotEmpty(main.getOrderExtendList())) {
                checkExtend(main.getOrderExtendList());
            }
            main.getSubOrders().stream().forEach(subOrder -> {
                checkExtend(subOrder.getOrderExtendList());
            });
        }
    }

    /**
     * 校验备注
     * @param list
     */
    private void checkExtend(List<TcOrderExtendDO> list) {
        if (list == null) {
            return;
        }
        for (TcOrderExtendDO ext : list) {
            domainExtendService.checkExtend(
                DomainExtendUtil.buildExtendKeyWithTcOrderExtend(ext.getExtendKey()),
                ext.getExtendValue()
            );
        }
    }
}
