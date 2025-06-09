package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 优惠信息
 *
 * @author tiansong
 */
@Data
@ApiModel("优惠信息")
public class PromotionOptionVO {
    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @NotNull
    @ApiModelProperty("优惠选项ID")
    private String  optionId;

    @ApiModelProperty("优惠选项名称")
    private String  promotionName;

    @NotNull
    @ApiModelProperty("优惠扣减金额")
    private Long  reduceFee;

    @ApiModelProperty("是否可选择的")
    private Boolean selectable;

    @ApiModelProperty("是否选中的")
    private Boolean selected;

    @ApiModelProperty("是否是券")
    private Boolean isCoupon;

    @ApiModelProperty("优惠工具")
    private String toolCode;

    @ApiModelProperty("优惠类型")
    private Integer assetType;

    @ApiModelProperty("优惠扩展")
    private Map<String, Object> extras;

    public String getReduceFeeYuan() {
        return String.valueOf(this.getReduceFee());
    }
}
