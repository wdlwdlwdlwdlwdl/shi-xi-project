package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 修改用户信息
 *
 * @author tiansong
 */
@ApiModel(description = "修改用户信息")
@Data
public class EditCustomerInfoCommand extends LoginRestCommand {


    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("固话区号")
    private String telArea;
    @ApiModelProperty("固话号码")
    private String tel;
    @ApiModelProperty("用户签名")
    private String custSign;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("手机")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectFalse(StringUtils.isAllBlank(nickname, tel, telArea, custSign, remark, phone, email), I18NMessageUtils.getMessage("update.user.info.not.all.empty"));  //# "更新用户信息不能全空"
        if (StringUtils.isNotBlank(phone)) {
            ParamUtil.expectPhone(phone, I18NMessageUtils.getMessage("update.user.info.phone.illegal")); //手机号不合法
        }
        if (StringUtils.isNotBlank(email)) {
            ParamUtil.expectEmail(email, I18NMessageUtils.getMessage("update.user.info.email.illegal")); //邮箱不合法
        }
    }
}