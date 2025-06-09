package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 微信小程序授权登录返回结果
 *
 * @author tiansong
 */
@Data
@Builder
public class WxMiniLoginResult extends AbstractOutputInfo {
    @ApiModelProperty(value = "小程序账号唯一标识openId", required = true)
    private String openId;
}
