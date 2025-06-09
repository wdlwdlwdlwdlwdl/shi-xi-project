package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import lombok.Data;

/**
 * 校验下单
 * @anthor shifent
 * @version 1.0.1
 * 2025-1-6 16:31:32
 */
@Data
public class CheckOutCreatingOrder {

    // 校验成功
    private Boolean checkSuccess;

    // 已经通过
    private Boolean createdSuccess = Boolean.FALSE;

    // 订单对象
    private CreatingOrder creatingOrder;

}
