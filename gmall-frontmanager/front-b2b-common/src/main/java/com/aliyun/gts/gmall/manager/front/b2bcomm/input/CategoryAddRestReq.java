package com.aliyun.gts.gmall.manager.front.b2bcomm.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 11:34 AM
 */
@Getter
@Setter
@ToString
public class CategoryAddRestReq extends AbstractCommandRestRequest {

    @ApiModelProperty(value = "类目名称", required = true)
    private String name;

    @ApiModelProperty(value = "父类目ID", required = true)
    private Long parentId;

    @ApiModelProperty(value = "是否叶子类目", required = true)
    private Boolean leafYn;

    @ApiModelProperty(value = "操作者, 当前登录用户，自动取")
    private String operator;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectInRange(name, 2, 20, I18NMessageUtils.getMessage("category.name.len")+"2-20"+I18NMessageUtils.getMessage("characters"));  //# "类目名称长度2-20字符"
        ParamUtil.nonNull(parentId, I18NMessageUtils.getMessage("parent.category")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "父类目ID不能为空"
        ParamUtil.nonNull(leafYn, I18NMessageUtils.getMessage("leaf.category.required"));  //# "是否叶子类目不能为空"
    }
}
