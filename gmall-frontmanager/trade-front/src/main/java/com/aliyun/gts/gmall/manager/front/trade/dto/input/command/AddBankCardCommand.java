package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 添加银行卡
 * @author liang.ww(305643)
 * @date 2024/11/6 17:01
 */
@ApiModel(description = "添加银行卡")
@Data
public class AddBankCardCommand extends LoginRestCommand {
    @ApiModelProperty("邮件")
    private String email;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("卡号")
    private String cardNo;
    @ApiModelProperty("持卡人")
    private String cardPersonName;
    @ApiModelProperty("过期时间")
    private String expDate;
    @Override
    public void checkInput() {
        super.checkInput();

    }
}