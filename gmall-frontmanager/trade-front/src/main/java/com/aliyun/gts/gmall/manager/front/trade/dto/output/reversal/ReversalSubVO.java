package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.CombineItemVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderSubVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 售后子订单
 *
 * @author tiansong
 */
@Data
@ApiModel("售后子订单")
public class ReversalSubVO {
    @ApiModelProperty("售后主单ID")
    private Long       primaryReversalId;
    @ApiModelProperty("售后子单ID")
    private Long       reversalId;
    @ApiModelProperty("子订单ID")
    private Long       orderId;
    @ApiModelProperty("退换商品数量")
    private Integer    cancelQty;
    @ApiModelProperty("退款金额")
    private Long       cancelAmt;
    @ApiModelProperty("订单信息")
    private OrderSubVO orderInfo;

    @ApiModelProperty("组合商品,退款信息")
    private List<CombineItemVO> combineItemRev;
    public String getCancelAmt() {
        return String.valueOf(this.cancelAmt);
    }
}
