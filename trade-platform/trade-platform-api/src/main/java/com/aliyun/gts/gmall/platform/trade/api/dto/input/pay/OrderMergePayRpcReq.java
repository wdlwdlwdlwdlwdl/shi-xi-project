package com.aliyun.gts.gmall.platform.trade.api.dto.input.pay;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import com.aliyun.gts.gmall.platform.trade.common.domain.pay.CurrencyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel("合并支付请求入参、适用多商家多主订单合并下单")
public class OrderMergePayRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "主订单号列表", required = true)
    private List<Long> primaryOrderIds;
    @ApiModelProperty(value = "买家id", required = true)
    private Long custId;
    @ApiModelProperty(value = "买家名称", required = true)
    private String custName;
    @ApiModelProperty(value = "下单渠道", required = true)
    private String orderChannel;
    @ApiModelProperty(value = "订单类型", required = true)
    private String orderType;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;
    @ApiModelProperty(value = "支付总金额、所有合并支付订单总金额", required = true)
    private Long totalOrderFee;
    @ApiModelProperty(value = "实付金额、所有合并支付订单实付总金额", required = true)
    private Long realPayFee;
    @ApiModelProperty(value = "积分、用户支付时总使用积分")
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

    @ApiModelProperty("其他扩展支付信息, 可传递到支付渠道对接处")
    private Map<String, Object> extraPayInfo;

    @ApiModelProperty("银行卡号")
    private String bankCardNbr;

}
