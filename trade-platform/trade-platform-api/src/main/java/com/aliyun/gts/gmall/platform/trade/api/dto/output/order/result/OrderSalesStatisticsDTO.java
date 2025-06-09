package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @Description:
 * @author yangl
 * @date 2025年1月24日 11:11:32
 * @version V1.0
 */
@Getter
@Setter
public class OrderSalesStatisticsDTO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;

    /** item维度订单总数 */
    private Long itemTotalCount;
    /** 过去30天item维度订单总数 */
    private Long itemTotalCountLast30Days;
    /** sku维度订单总取消数 */
    private Long skuTotalCount;
    /** 过去30天sku维度订单总取消数 */
    private Long skuTotalCountLast30Days;
    
    public static OrderSalesStatisticsDTO getDefaultInstance() {
        OrderSalesStatisticsDTO instance = new OrderSalesStatisticsDTO();
        instance.setItemTotalCount(0L);
        instance.setItemTotalCountLast30Days(0L);
        instance.setSkuTotalCount(0L);
        instance.setSkuTotalCountLast30Days(0L);
        return instance;
    }
}
