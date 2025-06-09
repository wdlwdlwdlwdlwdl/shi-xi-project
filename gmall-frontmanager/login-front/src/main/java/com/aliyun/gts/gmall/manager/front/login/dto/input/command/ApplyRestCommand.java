package com.aliyun.gts.gmall.manager.front.login.dto.input.command;


import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplyRestCommand extends LoginRestCommand {

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("公司地址")
    private String companyAddress;

    @ApiModelProperty("公司电话")
    private String companyPhone;

    @ApiModelProperty("联系人电话")
    private String phone;

    @ApiModelProperty("电子邮箱")
    private String email;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("联系人身份证-正面")
    private String contactIdCardFront;

    @ApiModelProperty("联系人身份证-反面")
    private String contactIdCardBack;

    @ApiModelProperty("营业执照信息")
    private BusinessInfo businessInfo;

    @ApiModelProperty("资金账户信息")
    private FundInfo fundInfo;

    @ApiModelProperty("验证码")
    private String verifyCode;


    @Data
    public static class BusinessInfo extends AbstractInputParam {
        @ApiModelProperty("法人证件-正面")
        private String legalIdCardFront;
        @ApiModelProperty("法人证件-反面")
        private String legalIdCardBack;
        @ApiModelProperty("营业执照")
        private String businessLicense;
        @ApiModelProperty("法人姓名")
        private String legalName;
        @ApiModelProperty("法人证件号码")
        private String legalIdCardNo;
        @ApiModelProperty("法人证件类型")
        private String certificateType;
        @ApiModelProperty("统一社会信用代码")
        private String socialCreditCode;
        @ApiModelProperty("法定经营范围")
        private String legalBusinessScope;
        @ApiModelProperty("营业执照详细地址")
        private String businessLicenseAddress;
        @ApiModelProperty("成立日期")
        private String establishedTime;
        @ApiModelProperty("营业执照有效期-开始")
        private String timeRangeStart;
        @ApiModelProperty("营业执照有效期-结束")
        private String timeRangeEnd;

        public void setTimeRange(String[] value) {
            if (value != null && value.length == 2) {
                timeRangeStart = value[0];
                timeRangeEnd = value[1];
            }
        }
    }

    @Data
    public static class FundInfo extends AbstractInputParam {
        @ApiModelProperty("支付宝账号")
        private String alipayAccountNo;
        @ApiModelProperty("微信商户号")
        private String wechatAccountNo;
        @ApiModelProperty("开户行, 字典:bankName")
        private String bankAccountName;
        @ApiModelProperty("银行账户")
        private String bankAccountNum;
    }
}
