package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonOrderSaveExt;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderSaveExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

import java.util.List;
import java.util.Map;

@Slf4j
@Extension(points = {OrderSaveExt.class})
public class MatchAllOrderSaveExt extends CommonOrderSaveExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }

    @Override
    public void convertOrder(MainOrder main, CreatingOrder order, Map context) {
        super.convertOrder(main, order, context);
        String payChannel = main.orderAttr().getPayChannel();
        if (PayChannelEnum.CAT.getCode().equals(payChannel)
            || PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(payChannel)) {
            processCatOrAccountPeriod(main, order, context);
        }
    }

    private void processCatOrAccountPeriod(MainOrder main, CreatingOrder order, Map context) {
        String payChannel = main.orderAttr().getPayChannel();
        SaveList saveList = (SaveList) context.get("saveList");
        String memo = order.getExtra(CreatingOrderParamConstants.ACCOUNT_PERIOD_MEMO) + "";

        if (PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(payChannel)) {
            List<TcOrderDO> orderList = saveList.orderList;
            orderList.forEach(o -> {
                if (o.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode())) {
                    o.getOrderAttr().setAccountPeriodMemo(memo);
                }
            });
        }
    }
}
