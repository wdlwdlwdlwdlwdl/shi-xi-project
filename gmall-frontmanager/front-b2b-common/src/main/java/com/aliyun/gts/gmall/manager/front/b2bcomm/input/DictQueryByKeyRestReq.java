package com.aliyun.gts.gmall.manager.front.b2bcomm.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author gshine
 * @since 3/8/21 11:00 AM
 */
@Data
public class DictQueryByKeyRestReq extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "字典key", required = true)
    private String dictKey;

    @ApiModelProperty(value = "字典keys", required = true)
    private List<String> dictKeys;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(dictKey != null || dictKeys != null , I18NMessageUtils.getMessage("dictionary")+"key"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "字典key不能为空"
    }
}
