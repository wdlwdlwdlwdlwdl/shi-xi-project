package com.aliyun.gts.gmall.platform.trade.core.util;

import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;
import com.aliyun.gts.gmall.platform.item.common.enums.SkuQuoteMapEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.SellerStatusEnum;
import org.apache.commons.lang.StringUtils;

/**
 * 状态判断
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-3 11:14:49
 */
public class StatusUtils {

    /**
     * 卖家SKU状态判断
     * @param skuStatus
     * @return Boolean
     * 2024年12月3日11:15:30
     */
    public static Boolean checkSkuStatus(Integer skuStatus) {
        return !SkuQuoteMapEnum.MAP.getStatus().equals(skuStatus);
    }


    /**
     * 卖家销售状态判断
     * @param sellerStatus
     * @return Boolean
     * 2024年12月3日11:15:30
     */
    public static Boolean checkSellerStatus(String sellerStatus) {
        return !String.valueOf(SellerStatusEnum.NORMAL.getCode()).equals(sellerStatus);
    }

    /**
     * 商品状态
     * @param itemStatus
     * @return Boolean
     * 2024年12月3日11:15:30
     */
    public static Boolean checkItemStatus(Integer itemStatus) {
        return !ItemStatus.ENABLE.getStatus().equals(itemStatus);
    }


    /**
     * 校验状态
     * @param skuStatus
     * @param sellerStatus
     * @param itemStatus
     * @return
     */
    public static Boolean checkStatus(Integer skuStatus, String sellerStatus, Integer itemStatus) {
        return checkSkuStatus(skuStatus) && checkSellerStatus(sellerStatus) && checkItemStatus(itemStatus);
    }

}
