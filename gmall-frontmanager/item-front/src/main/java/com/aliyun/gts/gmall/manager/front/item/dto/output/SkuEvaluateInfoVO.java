package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "评价信息")
public class SkuEvaluateInfoVO extends AbstractOutputInfo {
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
     * 买家id
     */
    private Long custId;

    /**
     * 卖家id
     */
    private Long sellerId;

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
}
