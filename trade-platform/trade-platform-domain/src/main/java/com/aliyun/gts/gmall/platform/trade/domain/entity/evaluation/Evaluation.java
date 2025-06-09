package com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@Data
public class Evaluation extends AbstractBusinessEntity {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 被回复的评论id
     */
    private Long replyId;

    /**
     * 主订单ID
     */
    private Long primaryOrderId;

    /**
     * 订单ID、订单ID可能是子订单也可能是主订单ID
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 买家id
     */
    private Long custId;

    /**
     * 买家名称
     */
    private String custName;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 卖家名称
     */
    private String sellerName;

    /**
     * 卖家bin/iin
     */
    private String binOrIin;

    /**
     * 评论分数1-5
     */
    private Integer rateScore;

    /**
     * 评价内容
     */
    private String rateDesc;

    /**
     * 评价图片
     */
    private List<String> ratePic;

    /**
     * 评价视频
     */
    private List<String> rateVideo;

    private Date gmtCreate;

    private Date gmtModified;

    private Map extend;
}
