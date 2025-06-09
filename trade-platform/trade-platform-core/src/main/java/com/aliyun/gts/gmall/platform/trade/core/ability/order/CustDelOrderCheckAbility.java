package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import java.util.Objects;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.CustomerDeleteOrderPreCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefalutCustomerDeleteOrderPreCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = CustDelOrderCheckAbility.CUST_DEL_ORDER_ABILITY,
    fallback = DefalutCustomerDeleteOrderPreCheckExt.class,
    description = "买家删除订单前置校验"
)
public class CustDelOrderCheckAbility extends BaseAbility<BizCodeEntity, CustomerDeleteOrderPreCheckExt> {

    public static final String CUST_DEL_ORDER_ABILITY = "com.aliyun.gts.gmall.platform.trade.core.ability.order.CustDelOrderCheckAbility";

    public TradeBizResult<Boolean> check(Long primaryId){
        TradeBizResult<Boolean>  result = this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.check(primaryId),
            CustomerDeleteOrderPreCheckExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

    public TradeBizResult<Boolean> check(Long primaryId, Long custId){
        TradeBizResult<Boolean>  result = this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.check(primaryId, custId),
            CustomerDeleteOrderPreCheckExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

}
