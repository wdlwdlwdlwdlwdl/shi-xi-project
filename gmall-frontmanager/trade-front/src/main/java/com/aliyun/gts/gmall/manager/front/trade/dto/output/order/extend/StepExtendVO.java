package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PayPriceVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StepExtendVO extends OrderExtendVO {

    private OrderExtendVO normal;

    /**
     * 首笔支付
     */
    private PayPriceVO firstPay;

    /**
     * 剩余支付（大于2阶段的，剩余求和）
     */
    private PayPriceVO remainPay;

    private Integer stepNo;

    private Integer stepStatus;

    private Map<String, String> contextProps;

    /**
     * 确认订单时返回, 每个主订单的 contextProps
     */
    private List<Map<String, String>> confirmContextProps;
}
