package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后订单列表信息
 *
 * @author tiansong
 */
@Data
@ApiModel("售后订单列表信息")
public class ReversalOrderVO {
    @NotNull
    @ApiModelProperty("售后主单ID")
    private Long                primaryReversalId;
    @ApiModelProperty("主订单ID")
    @NotNull
    private Long                primaryOrderId;
    @ApiModelProperty("退款总金额")
    private Long                cancelAmt;
    @ApiModelProperty("退换总件数")
    private Integer             cancelQty;
    @ApiModelProperty("售后类型, ReversalTypeEnum")
    private Integer             reversalType;
    @ApiModelProperty("ReversalStatusEnum")
    private Integer             reversalStatus;
    @ApiModelProperty("是否收到货物")
    private Boolean             itemReceived;
    @ApiModelProperty("售后子单列表")
    private List<ReversalSubVO> subReversals;
    @ApiModelProperty("卖家ID")
    private Long                sellerId;
    @ApiModelProperty("卖家名称")
    private String              sellerName;

    public String getCancelAmt() {
        return String.valueOf(this.cancelAmt);
    }

    public String getReversalTypeName() {
        ReversalTypeEnum reversalTypeEnum = ReversalTypeEnum.codeOf(this.reversalType);
        return reversalTypeEnum == null ? null : reversalTypeEnum.getName();
    }

    public String getReversalStatusName() {
        ReversalStatusEnum reversalStatusEnum = ReversalStatusEnum.codeOf(this.reversalStatus);
        return reversalStatusEnum == null ? null : reversalStatusEnum.getName();
    }

    public Boolean getShowCancel() {
        return ReversalStatusEnum.WAIT_SELLER_AGREE.getCode().equals(this.reversalStatus)
            || ReversalStatusEnum.WAIT_DELIVERY.getCode().equals(this.reversalStatus);
    }
}