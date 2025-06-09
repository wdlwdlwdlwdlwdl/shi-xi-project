package com.aliyun.gts.gmall.platform.trade.api.dto.message;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单消息
 */
@Data
public class OrderMessageDTO implements Transferable {

    Long primaryOrderId;

    Long orderId;

    Integer orderStatus;

    Integer primaryOrderStatus;

    Integer primaryOrderFlag;

    Date gmtCreate;

    Date gmtModified;

    Long sellerId;

    Long custId;

    // 订单自定义feature ( 即 order_attr.extras )
    Map<String, String> orderFeatures;

    // 订单自定义标 ( 即 order_attr.tags )
    List<String> orderTags;

    // 订单版本号
    Long version;

    // 多阶段订单 阶段号
    Integer stepNo;

    // 多阶段订单 阶段状态
    Integer stepOrderStatus;

    // 是否改价
    boolean changePrice;
}
