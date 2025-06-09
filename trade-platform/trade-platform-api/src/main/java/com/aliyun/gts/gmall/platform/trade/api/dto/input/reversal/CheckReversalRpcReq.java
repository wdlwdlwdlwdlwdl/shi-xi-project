package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "售后单确认入参")
public class CheckReversalRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "客户编号", required = true)
    private Long custId;

    @ApiModelProperty(value = "售后申请渠道（参见OrderChannelEnum）", required = true)
    private String reversalChannel;

    @ApiModelProperty(value = "主订单编号", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "需要退款的子单, 不传查全部", required = false)
    private List<Long> subOrderIds;


    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("customer.id.empty"));  //# "客户编号不能为空"
        ParamUtil.nonNull(reversalChannel, I18NMessageUtils.getMessage("aftersales.channel.empty"));  //# "售后申请渠道不能为空"
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order.num.empty"));  //# "主订单编号不能为空"
    }
}
