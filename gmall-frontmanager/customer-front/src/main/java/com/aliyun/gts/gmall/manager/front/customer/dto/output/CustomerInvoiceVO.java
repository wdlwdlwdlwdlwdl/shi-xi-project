package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
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
public class CustomerInvoiceVO extends AbstractOutputInfo {
    @ApiModelProperty("自增ID")
    private Long    id;
    @ApiModelProperty("发票名字")
    private String  name;
    @ApiModelProperty("抬头类型")
    private String  titleType;
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
}
