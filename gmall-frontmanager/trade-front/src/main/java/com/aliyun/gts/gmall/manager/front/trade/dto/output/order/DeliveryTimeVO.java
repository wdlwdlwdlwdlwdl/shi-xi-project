package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;


import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.MerchantDeliveryResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: yangl
 * @Date: 20224/8/9 10:37
 * @Desc: 物流时效Resp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTimeVO implements Serializable {

    /**
     * categoryID
     */
    private int categoryID;

    /**
     * weight
     */
    private BigDecimal weight;

    private List<MerchantDeliveryResp> merchants;

}
