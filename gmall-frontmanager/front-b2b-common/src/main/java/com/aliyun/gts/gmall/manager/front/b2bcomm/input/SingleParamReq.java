package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

@Data
public class SingleParamReq<T> extends AbstractQueryRestRequest {
    T param;

    @Override
    public boolean isWrite() {
        return false;
    }
}
