package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author gshine
 * @since 2/24/21 8:06 PM
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ByIdCommandRestReq extends AbstractCommandRestRequest {
    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty(value = "操作者, 当前登录用户")
    private String operator;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(id, "id"+ I18NMessageUtils.getMessage("required"));  //# 必填"
    }
}
