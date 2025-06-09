package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.center.user.api.enums.ThirdCustomerAccountTypeEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 15:22:00
 */
@Data
public class BindCustomerReq extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "jwt加密用户信息", required = true)
    private String authorizationToken;

    @ApiModelProperty(value = "类型")
    private String type;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(authorizationToken, "authorizationToken"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        if (StringUtils.isBlank(type)) {
            type = ThirdCustomerAccountTypeEnum.TMALL_GENIE.getCode();
        }
    }

}
