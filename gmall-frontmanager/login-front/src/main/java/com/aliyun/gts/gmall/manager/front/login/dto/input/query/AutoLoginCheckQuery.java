package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 手机登录请求
 *
 * @author tiansong
 */
@ApiModel(description = "cas登录")
@Data
public class AutoLoginCheckQuery extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "custId", required = true)
    private Long custId;

    @ApiModelProperty(value = "iin", required = true)
    private String iin;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(iin, I18NMessageUtils.getMessage("iin.required"));  //# "手机号码不能为空"
    }
}
