package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 5:29 PM
 */
@Getter
@Setter
@ToString
public class RegionAllChildrenQuery extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "上一级的地址id", required = true)
    private Long parentId;

    @ApiModelProperty(value = "查询下级地址的深度，不传则只查询下一级")
    private Integer depth;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(parentId, "[parentId] "+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
    }
}
