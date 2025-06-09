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
 * @since 2/24/21 4:26 PM
 */
@Getter
@Setter
@ToString
public class ByPidQueryRestReq extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "父节点ID", required = true)
    private Long parentId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(parentId, I18NMessageUtils.getMessage("parent.node")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "父节点ID不能为空"
    }
}
