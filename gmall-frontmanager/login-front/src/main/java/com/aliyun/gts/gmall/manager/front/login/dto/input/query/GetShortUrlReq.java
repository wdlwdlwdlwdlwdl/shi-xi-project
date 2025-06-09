package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获取短链请求
 *
 * @author liguotai
 * @date 2022/11/15
 */
@ApiModel(description = "获取短链请求")
@Data
public class GetShortUrlReq extends LoginRestCommand {
    @ApiModelProperty(value = "原长链接url", required = true)
    private String url;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(url, "url"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
    }
}
