package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
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
public class GetShortUrlByItemIdReq extends AbstractCommandRestRequest {
    @ApiModelProperty(value = "原长链接url", required = true)
    private String url;

    @ApiModelProperty(value = "商品Id", required = true)
    private String itemId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(url, "url"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        ParamUtil.notBlank(itemId, "url"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
    }
}
