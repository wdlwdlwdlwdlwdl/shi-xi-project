package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@ApiModel("客户订阅")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCampSubscribeVO {

    private Long id;
    /**
     * 买家id
     */
    private Long custId;

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
     * 活动类型 （预售，固定价，秒杀，折扣 等）
     */
    private String campType;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    /**
     * 通知开始时间
     */
    private Date preStartTime;

    /**
     * 通知结束时间
     */
    private Date preEndTime;

    /**
     * 订阅通知内容
     */
    private String subNoticeContext;

    /**
     * 订阅类型
     */
    private String subType;

    /**
     * 订阅状态
     */
    private String subStatus;

    /**
     * 通知状态
     */
    private String subNoticeStatus;



}
