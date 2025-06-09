package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.platform.trade.common.constants.PromotionTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class PromotionOption extends AbstractBusinessEntity {

    @ApiModelProperty("优惠选项ID")
    private String optionId;

    @ApiModelProperty("优惠选项名称")
    private String promotionName;

    @ApiModelProperty("优惠扣减金额")
    private Long reduceFee;

    @ApiModelProperty("优惠扩展")
    private Map<String, Object> extras;

    @ApiModelProperty("是否可选择的")
    private Boolean selectable;

    @ApiModelProperty("是否选中的")
    private Boolean selected;

    @ApiModelProperty("营销类型, PromotionTypeEnum: 券、活动等")
    private Integer promotionType;

    @ApiModelProperty("优惠工具类型")
    private String toolCode;

    @ApiModelProperty("是否是券")
    @JSONField(serialize = false)
    public boolean isCoupon() {
        return PromotionTypeEnum.codeOf(promotionType) == PromotionTypeEnum.COUPON;
    }

    @ApiModelProperty("是否普通活动")
    @JSONField(serialize = false)
    public boolean isCampaign() {
        return PromotionTypeEnum.codeOf(promotionType) == PromotionTypeEnum.NORMAL;
    }
}
