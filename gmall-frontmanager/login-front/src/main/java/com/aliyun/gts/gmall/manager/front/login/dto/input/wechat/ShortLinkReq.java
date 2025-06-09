package com.aliyun.gts.gmall.manager.front.login.dto.input.wechat;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("微信短链 请求参数")
public class ShortLinkReq extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "通过 Short Link 进入的小程序页面路径，必须是已经发布的小程序存在的页面，可携带 query，最大 1024 个字符")
    private String pageUrl;

    @ApiModelProperty(value = "页面标题，不能包含违法信息，超过 20 字符会用... 截断代替")
    private String pageTitle;

    @ApiModelProperty(value = "默认值 false。生成的 Short Link 类型，短期有效：false，永久有效：true")
    private Boolean isPermanent;

}
