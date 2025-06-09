package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 8:06 PM
 */
@Getter
@Setter
@ToString
public class ByIdQueryRestReq extends AbstractQueryRestRequest {

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
