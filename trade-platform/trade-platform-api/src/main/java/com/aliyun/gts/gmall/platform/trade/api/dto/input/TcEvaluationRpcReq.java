package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import lombok.Data;

import java.util.List;

/**
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@Data
public class TcEvaluationRpcReq extends AbstractCommandRpcRequest {

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
    private String primaryOrderId;

    /**
     * 订单ID、订单ID可能是子订单也可能是主订单ID
     */
    private String orderId;

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

    /**
     * 评价视频
     */
    private List<String> rateVideo;

    @Override
    public String toString() {
        return "TcEvaluationDO{" +
        "id=" + id +
        ", replyId=" + replyId +
        ", primaryOrderId=" + primaryOrderId +
        ", orderId=" + orderId +
        ", itemId=" + itemId +
        ", custId=" + custId +
        ", sellerId=" + sellerId +
        ", rateScore=" + rateScore +
        ", rateDesc=" + rateDesc +
        ", ratePic=" + ratePic +
        ", rateVideo=" + rateVideo +
        "}";
    }
}
