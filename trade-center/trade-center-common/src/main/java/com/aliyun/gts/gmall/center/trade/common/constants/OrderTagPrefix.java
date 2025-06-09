package com.aliyun.gts.gmall.center.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

public class OrderTagPrefix {

    public static final String CAMP_ID = "CAMP_";  // 活动ID, 子订单上
    public static final String CAMP_TOOL = "CAMP_TOOL_";  // 营销工具code, 主&子订单上

    public static final String MANZENG_GIFT = "MZ_GIFT";    // 满赠赠品订单
    public static final String MANZENG_ORDER = "MZ_ORDER";    // 满赠主商品订单

    public static final String HELP_ORDER = "HELP_ORDER"; //I18NMessageUtils.getMessage("order.on.behalf")  //# 代客下单

    // 销售模式, @see com.aliyun.gts.gmall.center.trade.core.enums.SaleTypeEnum
    public static final String SALE_TYPE = "SALE_TYPE_";

    // 积分商城固定积分兑换
    public static final String FIXED_POINT_ORD = "POINT_EXCHANGE";

    // 寻源订单
    public static final String SOURCING_ORD = "SOURCING";
}
