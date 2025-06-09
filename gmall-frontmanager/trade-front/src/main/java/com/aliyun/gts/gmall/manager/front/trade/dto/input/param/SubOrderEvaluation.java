package com.aliyun.gts.gmall.manager.front.trade.dto.input.param;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 子订单的评价信息
 *
 * @author tiansong
 */
@Data
@ApiModel("子订单的评价信息")
public class SubOrderEvaluation extends AbstractInputParam {
    @ApiModelProperty(value = "子订单ID", required = true)
    private Long         orderId;
    @ApiModelProperty("商品ID")
    private Long         itemId;
    private Long          skuId;
    @ApiModelProperty("评价分数")
    private Integer      rateScore;
    @ApiModelProperty("评价描述")
    private String       rateDesc;
    @ApiModelProperty("评价图片URL")
    private List<String> ratePic;
    @ApiModelProperty("评价视频URL")
    private List<String> rateVideo;

    private Long custId;
    private Long primaryOrderId;
    private Long sellerId;
    private Long replyId;
    private String custName;
    private String itemTitle;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(orderId, I18NMessageUtils.getMessage("sub.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "子订单ID不能为空"
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
        if (CollectionUtils.isNotEmpty(ratePic)) {
            // 图片上传过程中提交，会出现特殊URL
            ratePic.forEach(v -> ParamUtil.expectFalse(StringUtils.isEmpty(v) || v.contains("null.null"), I18NMessageUtils.getMessage("image.url.invalid")));  //# "图片地址格式不正确"
        }
        if (CollectionUtils.isNotEmpty(rateVideo)) {
            // 文件上传过程中提交，会出现特殊URL
            rateVideo.forEach(v -> ParamUtil.expectFalse(StringUtils.isEmpty(v) || v.contains("null.null"), I18NMessageUtils.getMessage("video.url.invalid")));  //# "视频地址格式不正确"
        }
        this.setReplyId(0L);
    }

    public void checkNotAdditional() {
        ParamUtil.nonNull(rateScore, I18NMessageUtils.getMessage("rating.required"));  //# "评价分数不能为空"
        ParamUtil.expectInRange(rateScore, 1, 5, I18NMessageUtils.getMessage("rating.score.range"));
    }
}