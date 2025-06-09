package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/4 17:52
 */
@Data
public class CommonPageReq extends LoginRestCommand {
    private PageParam page;

    @Override
    public boolean isWrite() {
        return false;
    }
}
