package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderMainVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 售后申请单详情
 *
 * @author tiansong
 */
@Data
@ApiModel("售后申请单详情")
public class ReversalOrderDetailVO {
    @NotNull
    @ApiModelProperty("订单详情信息")
    OrderMainVO orderMainVO;

    @NotEmpty
    @ApiModelProperty("售后原因列表")
    List<ReversalReasonDTO> reasonDTOList;
    @NotEmpty
    @ApiModelProperty("可申请售后数量")
    List<ReversalSubOrderVO> reversalSubOrderDTOS;
}
