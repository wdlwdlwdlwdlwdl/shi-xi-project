package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultStepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderCreateExt.DivideStepPriceOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 多步骤下单处理扩展点
 *
 */
@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderCreateAbility",
    fallback = DefaultStepOrderCreateExt.class,
    description = "多阶段创建"
)
public class StepOrderCreateAbility extends BaseAbility<BizCodeEntity, StepOrderCreateExt> {

    /**
     * 查询配置模板功能
     * @param mainOrder
     */
    public void fillStepTemplate(MainOrder mainOrder) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                /**
                 * 处理类 DefaultStepOrderCreateExt
                 */
                extension.fillStepTemplate(mainOrder);
                return null;
            },
            StepOrderCreateExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 填多多步骤订单计算
     * @param mainOrder
     */
    public void fillStepOrders(MainOrder mainOrder) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                /**
                 *  默认计算类 DefaultStepOrderCreateExt
                 */
                extension.fillStepOrders(mainOrder);
                return null;
            },
            StepOrderCreateExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 分步订单计算金额
     * @param creatingOrder
     * @return
     */
    public ConfirmStepExtendDTO calcStepExtend(CreatingOrder creatingOrder) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(creatingOrder.getMainOrders().get(0));
        return this.executeExt(
            bizCode,
            extension -> extension.calcStepExtend(creatingOrder),
            StepOrderCreateExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    public void divideStepPriceToSubOrder(StepOrder step, MainOrder mainOrder, DivideStepPriceOption opt) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(bizCode,
            extension -> {
                extension.divideStepPriceToSubOrder(step, mainOrder, opt);
                return null;
            },
            StepOrderCreateExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
