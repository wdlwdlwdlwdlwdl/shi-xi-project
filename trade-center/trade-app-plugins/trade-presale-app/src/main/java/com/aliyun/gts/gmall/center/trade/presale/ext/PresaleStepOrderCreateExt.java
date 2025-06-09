package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.PresaleConstants;
import com.aliyun.gts.gmall.center.trade.common.util.DateUtils;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PresaleDomainService;
import com.aliyun.gts.gmall.center.trade.domain.entity.deposit.ItemDepositInfo;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultStepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预售下单
 */
@Slf4j
@Extension(points = {StepOrderCreateExt.class})
public class PresaleStepOrderCreateExt extends DefaultStepOrderCreateExt {

    @Autowired
    private PresaleDomainService presaleDomainService;

    @Override
    protected List<Long> getBaseStepPrices(MainOrder mainOrder) {
        ItemDepositInfo d = presaleDomainService.getDepositFromItems(mainOrder);

        // 尾款日期
        Map<String, String> map = mainOrder.orderAttr().stepContextProps();
        map.put(PresaleConstants.CONTEXT_TAIL_START, DateUtils.toDateString(d.getTailStart()));
        map.put(PresaleConstants.CONTEXT_TAIL_END, DateUtils.toDateString(d.getTailEnd()));

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
        return PresaleConstants.TEMPLATE_NAME;
    }
}
