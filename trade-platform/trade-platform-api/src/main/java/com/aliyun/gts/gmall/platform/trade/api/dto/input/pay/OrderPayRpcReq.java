package com.aliyun.gts.gmall.platform.trade.api.dto.input.pay;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import com.aliyun.gts.gmall.platform.trade.common.domain.pay.CurrencyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 支付请求入参
 *
 * @author xinchen
 */
@Getter
@Setter
@ApiModel("支付请求入参")
public class OrderPayRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "主订单号", required = true)
    private Long primaryOrderId;
    @ApiModelProperty(value = "买家id", required = true)
    private Long custId;
    @ApiModelProperty(value = "客户名称", required = true)
    private String buyerName;
    @ApiModelProperty(value = "下单渠道", required = true)
    private String orderChannel;
    @ApiModelProperty(value = "订单类型", required = true)
    private String orderType;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;
    @ApiModelProperty(value = "订单总金额", required = true)
    private Long totalOrderFee;
    @ApiModelProperty(value = "实付金额", required = true)
    private Long realPayFee;
    @ApiModelProperty(value = "积分数量")
    private Long pointCount;
    @ApiModelProperty(value = "条形码或人脸识别Code", notes = "非扫码或人脸识别支付不用传")
    private String recogCode;
    @ApiModelProperty(value = "appId", notes = "微信支付时必传")
    private String appId;
    @ApiModelProperty(value = "openId", notes = "微信支付时必传")
    private String openId;
    @ApiModelProperty(value = "payMethod", notes = "支付方法")
    private String payMethod;
    @ApiModelProperty(value = "币种类型", notes = "不传默认是人民币、预留扩展")
    private CurrencyType currencyType;
    @ApiModelProperty(value = "阶段序号, 多阶段交易时可传入作校验, 不传则自动获取")
    private Integer stepNo;

    @ApiModelProperty("其他扩展支付信息, 可传递到支付渠道对接处")
    private Map<String, Object> extraPayInfo;

    @ApiModelProperty("银行卡号")
    private String bankCardNbr;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(custId != null, I18NMessageUtils.getMessage("buyer")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "买家ID不能为空"
        ParamUtil.expectTrue(primaryOrderId > 0, I18NMessageUtils.getMessage("main.order.empty"));  //# "主订单号不能为空"
        ParamUtil.notBlank(payChannel, I18NMessageUtils.getMessage("pay.channel.empty"));  //# "支付渠道不能为空"
        ParamUtil.notBlank(orderChannel, I18NMessageUtils.getMessage("order.channel.empty"));
        //I18NMessageUtils.getMessage("order.channel.empty")

    }
}
