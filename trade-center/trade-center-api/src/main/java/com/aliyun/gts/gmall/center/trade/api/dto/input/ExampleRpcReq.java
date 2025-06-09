package com.aliyun.gts.gmall.center.trade.api.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "入参例子")
public class ExampleRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "名称", required = true)
    private String name;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(name, I18NMessageUtils.getMessage("name.cannot.be.empty"));  //# "名称不能为空"
    }
}
