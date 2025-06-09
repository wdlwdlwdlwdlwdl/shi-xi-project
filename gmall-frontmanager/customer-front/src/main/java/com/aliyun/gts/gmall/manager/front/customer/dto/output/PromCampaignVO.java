package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PromCampaignVO extends PromBaseVO {

    /**
     * 卖家id
     */
    private Long sellerId;
    /**
     * 备注2（长版），例如用于活动C端说明
     */
    private String remark2;
    /**
     * 展示的状态
     */
    private Integer viewStatus;

    /**
     * 优惠活动详情
     */
    private List<PromDetailVO> details;

    /**
     * 玩法名称
     */
    private String playName;
    /**
     * 券id
     */
    private String couponCode;
    /**
     * 分类
     */
    private Integer campaignCategory;
    /**
     * 活动时间类型1是固定时间;2是领取后多少天有效
     */
    private Integer campaignTimeType = 1;

    private Map<String,Object> extendProps;



    //积分商品优惠券名称
    /**
     * 优惠券图片
     */
    private String picture;
    /**
     * 优惠券使用说明
     */
    private String itemDesc;

}
