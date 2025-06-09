package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Data;

import java.util.Map;

@Data
public class OrderStatus {

    Long primaryOrderId;

    /**
     * OrderStatusEnum
     */
    GenericEnum status;

    /**
     * OrderStatusEnum
     */
    GenericEnum checkStatus;
    /**
     * 取消之前状态
     */
    GenericEnum cancelFromStatus;

    /**
     * 订单阶段 OrderStageEnum
     */
    Integer orderStage;

    Long custId;    // 校验越权

    Long sellerId;   // 校验越权

    String reasonCode;

    Boolean refund = false;
    /**
     * 拓展信息
     */
    Map<String, String> extra;
}
