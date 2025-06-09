package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 发票信息
 *
 * @author tiansong
 */
@Data
@ApiModel("发票抬头信息")
public class OrderInvoiceTitle extends AbstractCommandRpcRequest {
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
    private Map<String, String> extra=new HashMap<>();

    public String getExtra(String key) {
        return this.extra == null ? null : this.extra.get(key);
    }

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(id, I18NMessageUtils.getMessage("main.order.id.not.null"));
        ParamUtil.notBlank(title,I18NMessageUtils.getMessage("invoice.info.required"));
    }
}
