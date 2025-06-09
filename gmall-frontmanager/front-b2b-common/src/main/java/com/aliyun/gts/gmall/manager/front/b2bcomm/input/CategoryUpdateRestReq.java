package com.aliyun.gts.gmall.manager.front.b2bcomm.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.anns.HeaderValue;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 8:04 PM
 */
@Getter
@Setter
@ToString
public class CategoryUpdateRestReq extends AbstractCommandRestRequest {

    @ApiModelProperty(value = "类目ID", required = true)
    private Long id;
    @ApiModelProperty(value = "类目名称", required = true)
    private String name;

    @ApiModelProperty(value = "操作者, 当前登录用户")
    @HeaderValue("operator")
    private String operator;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(id, "id"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        ParamUtil.expectInRange(name, 2, 20, I18NMessageUtils.getMessage("category.name.len")+"2-20"+I18NMessageUtils.getMessage("characters"));  //# "类目名称长度2-20字符"
    }
}
