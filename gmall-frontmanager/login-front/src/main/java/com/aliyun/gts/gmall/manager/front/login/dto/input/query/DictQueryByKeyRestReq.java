package com.aliyun.gts.gmall.manager.front.login.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gshine
 * @since 3/8/21 11:00 AM
 */
@Data
public class DictQueryByKeyRestReq extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "字典key", required = true)
    private String dictKey;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(dictKey, I18NMessageUtils.getMessage("dictionary")+"key"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "字典key不能为空"
    }
}
