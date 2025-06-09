package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gshine
 * @since 3/8/21 11:54 AM
 */
@Data
@ApiModel("字典数据更新")
public class DictUpdateRestReq extends AbstractCommandRpcRequest {

    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty("字典key的备注")
    private String remark;

    @ApiModelProperty("字典的value, json字符串")
    private String dictValue;

    @ApiModelProperty("类型，1 前端显示用，2 通用")
    private Integer type;

    @ApiModelProperty("更新人")
    private String updateId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(id, "id"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
    }
}
