package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

/**
 * 订单列表TAB
 *
 * @author tiansong
 */
public enum OrderTabEnum  implements I18NEnum {
    ALL("all", "|all|"),   //# "全部"
    PENDING_PAYMENT("payment", "|pending.payment|"),   //# "待支付"
    PENDING_DELIVERY("delivery", "|pending.shipment|"),   //# "待发货"
    PENDING_RECEIPT("receipt", "|pending.receipt|"),   //# "待收货"
    PENDING_EVALUATION("evaluation", "|pending.evaluation|"),//# 待评价
    COMPLETED("complete", "已完成"),
    CLOSED("closed", "已关闭"),
    REVERSING("reversing", "售后中"),
            ;

    /**
     * 获取订单TAB（默认：全部）
     *
     * @param code
     * @return
     */
    public static OrderTabEnum get(String code) {
        for (OrderTabEnum orderTabEnum : values()) {
            if (orderTabEnum.code.equals(code)) {
                return orderTabEnum;
            }
        }
        return OrderTabEnum.ALL;
    }

    /**
     * 订单分类code
     */
    private String code;
    /**
     * 订单分类名称
     */

    private String script;


    OrderTabEnum(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    public String getCode() {
        return this.code;
    }

    public String getScript() {
        return script;
    }

    public String getName() {
        return getMessage();
    }
}
