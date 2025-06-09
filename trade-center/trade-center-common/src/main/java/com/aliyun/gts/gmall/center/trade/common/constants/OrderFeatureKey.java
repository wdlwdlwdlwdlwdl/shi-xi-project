package com.aliyun.gts.gmall.center.trade.common.constants;

public class OrderFeatureKey {

        public static final String IS_SELF_SELLER = "isSelfSeller";  // 是否自营商家

        public static final String BATCH_ID = "batchId";  // 跨店合并下单的唯一ID

        public static final String MANZENG_ORDER = "mzOrd"; // 满赠订单, JSON: List<ManzengGift>

        public static final String MANZENG_GIFT = "mzGift"; // 满赠赠品订单, JSON: ManzengGift

        // 代客下单 代下单操作员id和name
        public static final String HELP_ORDER_ID = "hooId";
        public static final String HELP_ORDER_NAME = "hooName";

        /**
         * 订单销售类型  直销/代销
         */
        public static final String SALE_TYPE = "sale_type";

        /**
         * 税编
         */
        public static final String TAX_CODE = "tax_code";

        /**
         * 税率
         */
        public static final String TAX_RATE = "tax_rate";

        // 采购寻源下单
        public static final String B2B_SOURCING_BILL_ID = "b2bSrcBillId";
        public static final String B2B_SOURCING_DETAIL_ID = "b2bSrcDetailId";

}
