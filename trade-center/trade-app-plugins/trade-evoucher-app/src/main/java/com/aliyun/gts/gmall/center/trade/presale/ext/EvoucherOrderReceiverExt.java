package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderReceiverExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderReceiverExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 电子凭证不校验收件人信息
 */
@Slf4j
@Extension(points = {OrderReceiverExt.class})
public class EvoucherOrderReceiverExt extends DefaultOrderReceiverExt {

    @Override
    public TradeBizResult<ReceiveAddr> checkOnConfirmOrder(Long custId, ReceiveAddr addr) {
        return super.checkOnConfirmOrder(custId, addr);
    }

    @Override
    public TradeBizResult<ReceiveAddr> checkOnCreateOrder(Long custId, ReceiveAddr addr) {
        return super.checkOnCreateOrder(custId,  addr);
    }

    @Override
    public void fillLogisticsInfo(CreatingOrder order) {
        for (MainOrder mainOrder : order.getMainOrders()) {
            mainOrder.orderAttr().setLogisticsType(LogisticsTypeEnum.VIRTUAL.getCode());
        }
    }
}
