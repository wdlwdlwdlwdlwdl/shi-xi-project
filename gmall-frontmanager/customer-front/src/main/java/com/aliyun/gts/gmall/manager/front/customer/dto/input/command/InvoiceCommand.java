package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.InvoiceTitleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发票新增/更新
 *
 * @author tiansong
 */
@Data
@ApiModel("发票新增/更新")
public class InvoiceCommand extends LoginRestCommand {
    @ApiModelProperty("自增ID")
    private Long    id;
    @ApiModelProperty("发票类型")
    private String  invoiceType;
    @ApiModelProperty("抬头类型")
    private String  titleType;
    @ApiModelProperty("发票名字")
    private String  name;
    @ApiModelProperty("发票抬头")
    private String  title;
    @ApiModelProperty("发票税号")
    private String  taxNo;
    @ApiModelProperty("开户行")
    private String  bankName;
    @ApiModelProperty("银行账号")
    private String  bankAccount;
    @ApiModelProperty("注册地址")
    private String  regAddress;
    @ApiModelProperty("电话")
    private String  tel;
    @ApiModelProperty("公司照片")
    private String  companyPhoto;
    @ApiModelProperty("备注")
    private String  remark;
    @ApiModelProperty("邮箱")
    private String  email;
    @ApiModelProperty("是否默认发票")
    private Boolean defaultYn;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(titleType, I18NMessageUtils.getMessage("invoice.type.required"));  //# "发票类型不能为空"
        ParamUtil.nonNull(title, I18NMessageUtils.getMessage("invoice.title.required"));  //# "发票抬头不能为空"
        ParamUtil.expectFalse(isRight(title),I18NMessageUtils.getMessage("invoice.header.name.invalid")+"，"+I18NMessageUtils.getMessage("please.reenter"));  //# "发票抬头名称不符合要求，请重新输入"

        if (InvoiceTitleTypeEnum.ENTERPRISE.getCode().equals(titleType)) {
            // 企业发票校验
            ParamUtil.nonNull(taxNo, I18NMessageUtils.getMessage("invoice.tax.code.required"));  //# "企业发票税号不能为空"
        }
        // reset
        if (StringUtils.isBlank(name)) {
            this.setName(title);
        }
        if (defaultYn == null) {
            this.setDefaultYn(Boolean.FALSE);
        }
    }

    /**
     * 是否存在特殊字符
     * @param string
     * @return
     */
    public static Boolean isRight(String string) {
        String regEx =  ".*[\\s`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Pattern pattern = Pattern.compile(regEx);
        Matcher isNum = pattern.matcher(string);
        return isNum.matches();
    }
}