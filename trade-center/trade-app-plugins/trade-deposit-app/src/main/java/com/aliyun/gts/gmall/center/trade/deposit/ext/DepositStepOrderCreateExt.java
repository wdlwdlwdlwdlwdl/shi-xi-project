package com.aliyun.gts.gmall.center.trade.deposit.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.DepositConstants;
import com.aliyun.gts.gmall.center.trade.core.domainservice.DepositDomainService;
import com.aliyun.gts.gmall.center.trade.domain.entity.deposit.ItemDepositInfo;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultStepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * 定金尾款下单
 */
@Slf4j
@Extension(points = {StepOrderCreateExt.class})
public class DepositStepOrderCreateExt extends DefaultStepOrderCreateExt {

    @Autowired
    private DepositDomainService depositDomainService;

    @Override
    protected List<Long> getBaseStepPrices(MainOrder mainOrder) {
        ItemDepositInfo d = depositDomainService.getDepositFromItems(mainOrder);

        // 尾款日期
        mainOrder.orderAttr().stepContextProps()
                .put(DepositConstants.CONTEXT_TAIL_DAYS, String.valueOf(d.getTailDays()));

        // 子订单定金金额
        int idx = 0;
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            Long basePrice = d.getSubOrderDepositAmt()[idx++];
            setStepBasePrice(subOrder, ItemDepositInfo.DEPOSIT_STEP_NO, basePrice);
        }

        // 运费归到尾款
        if (mainOrder.orderAttr().getFreightPrice() != null) {
            setStepBasePrice(mainOrder.orderAttr().getFreightPrice(),
                    ItemDepositInfo.DEPOSIT_STEP_NO, 0L);
        }

        // 定金金额
        return Arrays.asList(d.getDepositAmt());
    }

    @Override
    protected String getTemplateName(MainOrder mainOrder) {
        return DepositConstants.TEMPLATE_NAME;
    }
}
