package com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@Data
public class EvaluationDTO extends AbstractOutputInfo {
    private static final long serialVersionUID=1L;

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
     * 买家名称
     */
    private String custName;

    private String firstName;

    private String lastName;

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

    // 获取 rateScore
    /**
     * 评论分数1-5
     */
    @Getter
    private Integer rateScore;

    private String rateScoreEx;
    public EvaluationDTO() {}

    // 构造函数
    public EvaluationDTO(Integer rateScore) {
        this.rateScore = rateScore;
        // 调用转换方法
        convertRateScore();
    }

    // 将 rateScore 转换为保留一位小数的字符串并赋值给 rateScoreEx
    private void convertRateScore() {
        if (rateScore != null) {
            DecimalFormat df = new DecimalFormat("#.0");
            this.rateScoreEx = df.format(rateScore.doubleValue());
        }
    }

    // 设置 rateScore
    public void setRateScore(Integer rateScore) {
        this.rateScore = rateScore;
        // 当 rateScore 被修改时，重新转换
        convertRateScore();
    }

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
