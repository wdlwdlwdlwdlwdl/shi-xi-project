package com.aliyun.gts.gmall.manager.front.login.dto.output;


import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

@Data
public class CaptchaResponse  extends AbstractOutputInfo {
    private String imageBase64;
    private String imageKey;
}
