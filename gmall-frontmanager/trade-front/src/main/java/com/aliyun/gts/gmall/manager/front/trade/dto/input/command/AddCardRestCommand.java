package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 添加银行卡
 * @author tiansong
 */
@ApiModel("添加银行卡")
@Data
public class AddCardRestCommand extends LoginRestCommand {
    @ApiModelProperty("请求epay的 cardId")
    private String cardId;

    @ApiModelProperty("epayCardID")
    private String id;

    @ApiModelProperty("标记")
    private String cardMask;

    @ApiModelProperty("卡名称")
    private String name;

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

    @ApiModelProperty("账号")
    private String accountId;

    @ApiModelProperty("是否活跃")
    private Boolean active;

    @ApiModelProperty("账号")
    private String createdDate;

    @ApiModelProperty("cardType")
    private String cardType;
    @ApiModelProperty("cardBankType")
    private String issuer;
    @ApiModelProperty("currency")
    private String currency;
    @ApiModelProperty("code")
    private String code;
    @ApiModelProperty("reason")
    private String reason;



}
