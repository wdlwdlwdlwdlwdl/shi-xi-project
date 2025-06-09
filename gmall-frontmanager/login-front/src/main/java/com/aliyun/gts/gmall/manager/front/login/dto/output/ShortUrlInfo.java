package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 短链接信息
 * @author liguotai
 * @date 2022/11/15
 */
@Data
public class ShortUrlInfo extends AbstractOutputInfo {

    @ApiModelProperty("短链接")
    private String shortUrl;

    @ApiModelProperty("原有url")
    private String originalUrl;
}
