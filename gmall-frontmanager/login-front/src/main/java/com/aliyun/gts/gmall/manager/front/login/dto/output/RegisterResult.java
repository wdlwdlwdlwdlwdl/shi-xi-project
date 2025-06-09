package com.aliyun.gts.gmall.manager.front.login.dto.output;

import lombok.Data;

@Data
public class RegisterResult extends PhoneLoginResult {
    private  boolean result;

    private String message;
}
