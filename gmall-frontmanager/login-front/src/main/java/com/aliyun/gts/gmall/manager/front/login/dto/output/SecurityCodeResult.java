package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发送验证码结果
 *
 * @author tiansong
 */
@ApiModel(description = "发送验证码结果")
@Data
public class SecurityCodeResult extends AbstractOutputInfo {
    @ApiModelProperty(value = "发送是否成功；成功：true，失败：false", required = true)
    private Boolean success;

    @ApiModelProperty(value = "发送返回的信息")
    private String message;

    public SecurityCodeResult (Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
