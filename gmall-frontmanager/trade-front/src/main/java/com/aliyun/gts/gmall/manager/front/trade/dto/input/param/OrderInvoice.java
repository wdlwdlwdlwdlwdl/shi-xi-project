package com.aliyun.gts.gmall.manager.front.trade.dto.input.param;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.platform.user.api.dto.input.InvoiceTitleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发票信息
 *
 * @author tiansong
 */
@Data
@ApiModel("发票信息")
public class OrderInvoice extends AbstractInputParam {
    @ApiModelProperty("自增ID")
    private Long   id;
    @ApiModelProperty("发票名字")
    private String name;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("发票类型")
    private String titleType;
    @ApiModelProperty("发票抬头")
    private String title;
    @ApiModelProperty("发票税号")
    private String taxNo;
    @ApiModelProperty("开户行")
    private String bankName;
    @ApiModelProperty("银行账号")
    private String bankAccount;
    @ApiModelProperty("注册地址")
    private String regAddress;
    @ApiModelProperty("电话")
    private String tel;
    @ApiModelProperty("公司照片")
    private String companyPhoto;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("邮箱")
    private String email;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(id, I18NMessageUtils.getMessage("invoice")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "发票ID不能为空"
        ParamUtil.nonNull(titleType, I18NMessageUtils.getMessage("invoice.type.required"));  //# "发票类型不能为空"
        ParamUtil.nonNull(title, I18NMessageUtils.getMessage("invoice.title.required"));  //# "发票抬头不能为空"
        // 企业发票校验
        if (InvoiceTitleTypeEnum.ENTERPRISE.getCode().equals(titleType)) {
            ParamUtil.nonNull(taxNo, I18NMessageUtils.getMessage("invoice.tax.code.required"));  //# "企业发票税号不能为空"
        }
    }
}
