package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

public interface OrderChangeOperate extends I18NEnum {
    int SELLER = 0;
    int CUSTOMER = 1;
    int SYSTEM = 2;


    /**
     * 操作人类别
     */
    int getOprType();

    /**
     * 操作名称
     */
    String getOpName();

    /**
     * 是否订单完结, 即进行打款、发放积分的步骤
     */
    boolean isOrderSuccess();
}
