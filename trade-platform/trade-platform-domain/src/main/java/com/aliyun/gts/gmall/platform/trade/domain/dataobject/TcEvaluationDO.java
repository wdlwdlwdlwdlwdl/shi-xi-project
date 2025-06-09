package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.domain.typehandler.MybatisArrayListTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@TableName("tc_evaluation")
@Data
public class TcEvaluationDO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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

    private Long skuId;


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
    @TableField(typeHandler = MybatisArrayListTypeHandler.class)
    private List<String> ratePic;

    /**
     * 评价视频
     */
    @TableField(typeHandler = MybatisArrayListTypeHandler.class)
    private List<String> rateVideo;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @ApiModelProperty("BIN或者IN")
    private String binOrIin;

    private Map extend;

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
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
            ", extend=" + extend +
        "}";
    }
}
