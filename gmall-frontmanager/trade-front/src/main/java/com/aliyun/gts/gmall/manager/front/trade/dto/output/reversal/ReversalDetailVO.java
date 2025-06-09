package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import java.util.Date;
import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderMainVO;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后单详情
 *
 * @author tiansong
 */
@Data
@ApiModel("售后单详情")
public class ReversalDetailVO extends ReversalOrderVO {
    @ApiModelProperty("订单信息")
    private OrderMainVO             orderInfo;
    @ApiModelProperty("客户ID")
    private Long                    custId;
    @ApiModelProperty("客户名称")
    private String                  custName;
    @ApiModelProperty("售后原因code")
    private Integer                 reversalReasonCode;
    @ApiModelProperty("售后原因内容")
    private String                  reversalReasonContent;
    @ApiModelProperty("买家备注")
    private String                  custMemo;
    @ApiModelProperty("买家举证图片")
    private List<String>            custMedias;
    @ApiModelProperty("卖家备注")
    private String                  sellerMemo;
    @ApiModelProperty("卖家举证图片")
    private List<String>            sellerMedias;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date                    gmtCreate;
    @ApiModelProperty("扩展字段")
    private ReversalFeatureVO       reversalFeatures;
    @ApiModelProperty("历史是否展示")
    private Boolean                 showHistory;
    @ApiModelProperty("协商历史")
    private List<ReversalHistoryVO> historyList;

    public Boolean getShowSendDelivery() {
        return ReversalTypeEnum.REFUND_ITEM.getCode().equals(this.getReversalType())
            && ReversalStatusEnum.WAIT_DELIVERY.getCode().equals(this.getReversalStatus());
    }

    public String getCancelRealAmtYuan() {
        return String.valueOf(reversalFeatures.getCancelRealAmt());
    }

    public String getCancelPointAmtYuan() {
        return String.valueOf(reversalFeatures.getCancelPointAmt());
    }

    public String getCancelPointCount() {
        return ItemUtils.pointDisplay(reversalFeatures.getCancelPointCount());
    }
}