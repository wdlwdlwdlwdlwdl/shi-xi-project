package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.util.Date;

/**
 * 商品物流对象信息
 */
@Data
public class ItemDeliveryDTO extends AbstractOutputInfo {

    // 物流方式
    private Integer deliveryType;

    // 物流方式
    private String deliveryTypeName;

    // 配送时间
    private Integer deliverHours;

    // 配送时间
    private Date deliverTime;

    // 运费
    private Long freightAmt;

    // 发货城市code
    private String fromCityCode;

    // 收货城市
    private String receiverCityCode;

}