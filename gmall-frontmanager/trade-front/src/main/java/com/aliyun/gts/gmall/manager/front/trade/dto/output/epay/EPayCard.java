package com.aliyun.gts.gmall.manager.front.trade.dto.output.epay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EPayCard extends AbstractOutputInfo {

    @ApiModelProperty("请求epay的 cardId")
    private String id;

    @ApiModelProperty("epayCardID")
    private String epayCardID;

    @ApiModelProperty("openwayID")
    private String openwayID;

    private String merchantID;

    @ApiModelProperty("card 的Hash值")
    private String cardHash;

    @ApiModelProperty("card的token")
    private String token;

    @ApiModelProperty("标记")
    private String cardMask;

    @ApiModelProperty("支付人名称")
    private String payerName;

    @ApiModelProperty("卡名称")
    private String cardName;

    @ApiModelProperty("foreign")
    private Boolean foreign;

    @ApiModelProperty("reference")
    private String reference;

    @ApiModelProperty("intReference")
    private String intReference;

    @ApiModelProperty("terminal")
    private String terminal;

    @ApiModelProperty("transactionId")
    private String transactionId;

    @ApiModelProperty("是否可支付")
    private String paymentAvailable;

    @ApiModelProperty("账号")
    private String accountID;

    @ApiModelProperty("是否活跃")
    private Boolean active;

    @ApiModelProperty("账号")
    private String createdDate;

    @ApiModelProperty("type")
    private String type;

    @ApiModelProperty("bankType")
    private String bankType;
}
