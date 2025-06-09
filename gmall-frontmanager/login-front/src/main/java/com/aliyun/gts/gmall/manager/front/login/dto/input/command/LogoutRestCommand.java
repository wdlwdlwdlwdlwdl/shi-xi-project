package com.aliyun.gts.gmall.manager.front.login.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;

/**
 * 用户退出
 *
 * @author tiansong
 */
@ApiModel("用户退出")
public class LogoutRestCommand extends LoginRestCommand {

    @Override
    public void checkInput() {
        // 这里不做校验，退出登录请求时，可能已经登录失效了
    }
}
