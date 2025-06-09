package com.aliyun.gts.gmall.manager.front.b2bcomm.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
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
public class CategoryQueryRestReq extends AbstractQueryRestRequest {

    @ApiModelProperty("类目搜索关键词")
    private String name;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(name, I18NMessageUtils.getMessage("search.keywords.required"));  //# "搜索关键词必填"
    }
}
