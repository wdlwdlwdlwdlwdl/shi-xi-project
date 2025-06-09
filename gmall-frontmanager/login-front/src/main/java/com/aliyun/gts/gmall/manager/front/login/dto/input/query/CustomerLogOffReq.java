package com.aliyun.gts.gmall.manager.front.login.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月23日 14:25:00
 */
@Data
public class CustomerLogOffReq extends LoginRestQuery {

    @ApiModelProperty(value = "注销原因", required = true)
    private String reason;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(reason, I18NMessageUtils.getMessage("logout.reason.required"));  //# "注销原因不能为空"
    }
}
