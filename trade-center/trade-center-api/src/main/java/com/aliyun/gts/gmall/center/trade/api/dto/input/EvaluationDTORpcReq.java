package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractRpcRequest;
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
public class EvaluationDTORpcReq extends AbstractQueryRpcRequest {
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

    /**
     * 评价视频
     */
    private List<String> rateVideo;

    private Date gmtCreate;

    private Date gmtModified;

    private Map extend;
}
