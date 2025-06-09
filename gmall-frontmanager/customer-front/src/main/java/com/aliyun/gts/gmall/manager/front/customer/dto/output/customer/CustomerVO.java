package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import com.aliyun.gts.gmall.platform.user.api.dto.contants.UserTagConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 会员信息
 *
 * @author tiansong
 */
@Data
@ApiModel("会员信息")
public class CustomerVO {
    @ApiModelProperty("用户id")
    private Long custId;
    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("头像")
    private String headUrl;
    @ApiModelProperty("手机绑定状态")
    private Boolean phoneIsBind;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("邮箱绑定状态")
    private Boolean emailIsBind;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("用户签名")
    private String custSign;
    @ApiModelProperty("会员等级信息")
    private CustomerLevelVO customerLevelVO;
    @ApiModelProperty("用户标")
    private List<String> tags;
    @ApiModelProperty("用户状态")
    private String status;
    @ApiModelProperty("firstName")
    private String firstName;
    @ApiModelProperty("lastName")
    private String lastName;
    @ApiModelProperty("中间名")
    private String middleName;
    @ApiModelProperty("生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;
    /**  会员所选语言 */
    @ApiModelProperty("会员所选语言")
    private String language;

    @ApiModelProperty("第三方账号")
    private String thirdAccounts;

    @ApiModelProperty("是否新用户")
    private Boolean isNewUser = Boolean.FALSE;

    public boolean isB2b() {
        return tags != null && tags.contains(UserTagConstants.B2B);
    }

    @ApiModelProperty("会员的默认地址")
    private CustomerAddressDTO defaultAddress;

    /**
     * 手机加密
     * @return
     */
    public String getEncryptionPhone() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("+").
            append(StringUtils.substring(phone, 0, 1)).
            append("(").
            append(StringUtils.substring(phone, 1, 4)).
            append(")").
            append(StringUtils.substring(phone, 5, 6)).
            append("****").
            append(StringUtils.substring(phone, 9));
        return stringBuffer.toString();
    }
}
