package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ByIdLoginRestReq extends LoginRestQuery {

    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(id, "id"+ I18NMessageUtils.getMessage("required"));  //# 必填"
    }

    public CommonByIdQuery convert() {
        CommonByIdQuery q = new CommonByIdQuery();
        q.setId(id);
        return q;
    }
}
