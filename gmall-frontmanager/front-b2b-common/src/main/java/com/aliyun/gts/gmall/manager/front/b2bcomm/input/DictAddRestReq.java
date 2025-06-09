package com.aliyun.gts.gmall.manager.front.b2bcomm.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gshine
 * @since 3/8/21 11:53 AM
 */
@ApiModel("新增字典信息")
@Data
public class DictAddRestReq extends AbstractCommandRpcRequest {

    @ApiModelProperty(value = "字典key", required = true)
    private String dictKey;

    @ApiModelProperty(value = "字典key的备注", required = true)
    private String remark;

    @ApiModelProperty(value = "字典的value, json字符串", required = true)
    private String dictValue;

    @ApiModelProperty(value = "类型，1 前端显示用，2 通用")
    private Integer type;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(dictKey, I18NMessageUtils.getMessage("dictionary")+"key"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "字典key不能为空"
        ParamUtil.notBlank(remark, I18NMessageUtils.getMessage("dictionary")+"key"+I18NMessageUtils.getMessage("remark.required"));  //# "字典key的备注不能为空"
        ParamUtil.notBlank(dictValue, I18NMessageUtils.getMessage("dictionary")+"value"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "字典value不能为空"
    }
}
