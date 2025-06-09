package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import lombok.Data;


@Data
public class CustomerCampSubscribeQuery extends LoginRestQuery {

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * sku_id
     */
    private String skuId;

    /**
     * 活动id
     */
    private Long campId;


    /**
     * 卖家ID
     */
    private Long sellerId;



    private PageParam page = new PageParam();


}
