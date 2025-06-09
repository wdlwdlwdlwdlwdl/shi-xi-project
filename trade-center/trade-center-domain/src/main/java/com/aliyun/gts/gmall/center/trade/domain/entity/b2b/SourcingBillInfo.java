package com.aliyun.gts.gmall.center.trade.domain.entity.b2b;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 询报价信息
 */
@Data
public class SourcingBillInfo {

    private String billId;
    private Long custId;
    private List<SourcingItem> items;
    private boolean waitOrder;  // 是否待下单
    private Object passBack;    // 更新时透传参数

    @Data
    public static class SourcingItem {
        private String detailId;    // 记录到订单上
        private Long itemId;
        private Long skuId;
        private Long unitPrice;     // 单价
        private Long freightAmt;    // 运费
        private Long totalAmt;      // 总价 (含运费,含所有数量)
        private Integer awardNum;    // 授标数量
        private boolean ordered;      // 是否已下单
        private Date priceStartTime;    // 价格有效期
        private Date priceEndTime;      // 价格有效期
    }
}
