package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 商家详情
 */
@Data
public class SellerDetailInfoVO extends AbstractOutputInfo{

    /**
     * 商家id
     */
    private Long sellerId;

    /**
     * 商家名称
     */
    private String sellerName;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商家电话
     */
    private String phone;

    /**
     * 图片
     */
    private String logoUrl;

    /**
     * 平均分
     * 保留一个小数位
     */
    private BigDecimal avgScore;

    /**
     * 评论总数
     */
    private Integer commentCount;

    /**
     * 订单总数
     */
    private Integer orderCount;

    /**
     * ka标识
     */
    private Integer ka;

    /**
     * op标识
     */
    private Integer op;


    /**
     * 是否是可靠商家
     */
    private Boolean hasReliableSeller;
}
