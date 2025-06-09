package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/4 17:47
 */
@Data
public class CommonReq extends AbstractQueryRestRequest {
    /**
     * 登陆账户
     */
    private OperatorDO loginAccount;

    @Override
    public boolean isWrite() {
        return false;
    }
}
