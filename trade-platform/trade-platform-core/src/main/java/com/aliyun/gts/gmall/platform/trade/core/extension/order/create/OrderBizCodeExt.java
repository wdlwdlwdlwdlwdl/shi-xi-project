package com.aliyun.gts.gmall.platform.trade.core.extension.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 订单业务身份识别
 */
public interface OrderBizCodeExt extends IExtensionPoints {

    /**
     * 商品业务身份
     */
    TradeBizResult<OrderBizCode> getBizCodesFromItem(OrderBizCodeReq req);

    /**
     * 营销业务身份
     */
    TradeBizResult<OrderBizCode> getBizCodesFromPromotion(OrderBizCodeReq req);

    @Data
    @Builder
    class OrderBizCode {
        private List<String> bizCodes;
        private Integer orderType;
    }

    @Data
    @Builder
    class OrderBizCodeReq {
        private List<String> requestBizCodes;
        private Integer requestOrderType;
        private CreatingOrder order;
    }
}
