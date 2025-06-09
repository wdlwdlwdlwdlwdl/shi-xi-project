package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.MerchantDeliveryReq;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangl
 * @Date: 20224/8/9 10:37
 * @Desc: 物流时效Request
 */
@Data
public class DeliveryTimeQuery implements Serializable {

    /**
     * categoryID
     */
    private int categoryID;

    /**
     * weight
     */
    private Long weight;


    private List<MerchantDeliveryReq> merchants;
}
