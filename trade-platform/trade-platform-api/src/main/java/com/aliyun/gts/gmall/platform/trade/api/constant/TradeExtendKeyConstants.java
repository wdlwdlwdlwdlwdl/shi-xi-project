package com.aliyun.gts.gmall.platform.trade.api.constant;

public class TradeExtendKeyConstants {

    // ========= 订单 =========

    // 商家账号
    public static final String ORDER_SELLER_ACCOUNT_INFO = OrderFeatureKeyConstants.SELLER_ACCOUNT_INFO;

    // 确认订单最大可用积分
    public static final String MAX_AVAILABLE_POINT = "maxAvailablePoint";


    // ========= 评价 =========

    // 物流评分, int
    public static final String EVALUATION_LOGISTICS_RATE = "logisticsRate";
    public static final String EVALUATION_LOGISTICS_DESC = "logisticsDesc";
    // 评价类型, int enum
    public static final String EVALUATION_TYPE = "evType";
    public static final int EVALUATION_TYPE_MAIN = 1;       // 主评价记录
    public static final int EVALUATION_TYPE_ADDITION = 2;    // 追加评价
    public static final int EVALUATION_TYPE_REPLY = 3;       // 卖家回复

    // 用户名称, String
    public static final String EVALUATION_CUST_NAME = "custName";

    // 商品名称, String
    public static final String EVALUATION_ITEM_TITLE = "itemTitle";

    // 是否系统评价, boolean
    public static final String EVALUATION_IS_SYSTEM = "system";

    // 是否存在追评,主评价记录上, boolean
    public static final String EVALUATION_HAS_ADDITION = "hasAddition";

    // 评价审批状态
    public final static String EVALUATION_APPROVE_STATUS="evaluationApproveStatus";
    // 评价审核状态的 搜索字段名
    public final static String EVALUATION_APPROVE_STATUS_SEARCH_FIELD = "evaluation_approve_status";

    // 用户的ip
    public final static String CUSTOMER_IP="customerIP";
    // 评价的审批内容
    public final static String EVALUATION_APPROVE_CONTENT="evaluationApproveContent";
    // 评价的审批人
    public final static String EVALUATION_APPROVE_USER_NAME="evaluationApproveUserName";
    // 评价的审批人id
    public final static String EVALUATION_APPROVE_USER_ID="evaluationApproveUserId";
    // 评价的审批时间
    public final static String EVALUATION_APPROVE_TIME="evaluationApproveTime";

    //取消运费承担
    public static final int FREIGHT_FEE_BELONG_CUST = 1;//退运费
    public static final int FREIGHT_FEE_BELONG = 2;//不退费
}
