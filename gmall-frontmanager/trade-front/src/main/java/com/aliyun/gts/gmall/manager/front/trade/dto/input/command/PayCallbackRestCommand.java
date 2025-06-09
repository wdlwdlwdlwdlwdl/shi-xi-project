package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.testng.collections.Maps;

import java.util.Map;

/**
 * Callback for pay
 *
 * @author tiansong
 */
@ApiModel("callback for pay, just for test")
@Data
public class PayCallbackRestCommand extends LoginRestCommand {
    @ApiModelProperty(value = "支付回调参数")
    private Map<String, Object> callBackParams;
    @ApiModelProperty(value = "主订单id", required = true)
    private Long                primaryOrderId;
    @ApiModelProperty(value = "支付流水号", required = true)
    private String              payFlowId;
    @ApiModelProperty(value = "订单总金额（单位：分）", required = true)
    private Long                totalOrderFee;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String              payChannel;
    @ApiModelProperty(value = "下单渠道", required = true)
    private String              orderChannel;
    @ApiModelProperty(value = "合并支付：主订单IDs")
    private String              primaryOrderIds;
    @ApiModelProperty(value = "合并支付：支付流水号")
    private String              payFlowIds;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "主订单ID不能为空"
        ParamUtil.nonNull(totalOrderFee, I18NMessageUtils.getMessage("order.total.amount.required"));  //# "订单总金额不能为空"
        ParamUtil.nonNull(payFlowId, I18NMessageUtils.getMessage("payment.serial.number.required"));  //# "支付流水号不能为空"
        callBackParams = Maps.newHashMap();
        callBackParams.put("mock", Boolean.TRUE);
        callBackParams.put("trade_no", primaryOrderId);
        callBackParams.put("total_fee", totalOrderFee);
        callBackParams.put("out_trade_no", payFlowId);
        callBackParams.put("cust_id", this.getCustId());
        this.setOrderChannel(this.getChannel());
    }
}