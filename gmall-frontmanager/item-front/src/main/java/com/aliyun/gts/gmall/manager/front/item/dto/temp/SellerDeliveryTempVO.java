package com.aliyun.gts.gmall.manager.front.item.dto.temp;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: SellerDeliveryTempVO.java
 * @Description: 物流信息-emp
 * @author zhao.qi
 * @date 2024年11月13日 12:27:03
 * @version V1.0
 */
@Getter
@Setter
public class SellerDeliveryTempVO {
    /** 是否支持网点自提 */
    private boolean hasSelfPickupBranch;
    /** 是否有自提柜 */
    private boolean hasSelfPickupCabinet;
    /** 是否有自提点 */
    private boolean hasSelfPickupPoint;
}
