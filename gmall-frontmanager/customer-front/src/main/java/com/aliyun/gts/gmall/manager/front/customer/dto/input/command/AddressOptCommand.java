package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 地址删除/设置默认
 *
 * @author tiansong
 */
@ApiModel(description = "地址删除/设置默认")
@Data
public class AddressOptCommand extends LoginRestCommand {
    @ApiModelProperty(value = "地址ID", required = true)
    private Long addressId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(addressId, I18NMessageUtils.getMessage("address")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "地址ID不能为空"
        ParamUtil.expectTrue(addressId > 0L, I18NMessageUtils.getMessage("address")+"ID"+I18NMessageUtils.getMessage("must.be.pos.int"));  //# "地址ID必须为正整数"
    }
}