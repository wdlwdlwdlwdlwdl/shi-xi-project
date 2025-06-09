package com.aliyun.gts.gmall.platform.trade.server.flow.context.base;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractRpcRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class AbstractContextEntity<REQ extends AbstractRpcRequest, DOMAIN, DTO> extends AbstractBusinessEntity {

    @Getter
    @Setter
    private REQ req;
    @Getter
    @Setter
    private DOMAIN domain;
    @Getter
    @Setter
    private DTO result;

    @Getter
    private boolean error;
    private String errCode;
    private String errMessage;


    public void setError(String code, String message) {
        this.errCode = code;
        this.errMessage = message;
        this.error = true;
    }

    public void setError(ResponseCode err) {
        setError(err.getCode(), err.getMessage());
    }

    public void setError(FailInfo fail) {
        setError(fail.getCode(), fail.getMessage());
    }

    public RpcResponse<DTO> getResponse() {
        return error ? RpcResponse.fail(errCode, errMessage) : RpcResponse.ok(result);
    }


    // ========== 适配 ==========

    public static final String CONTEXT_KEY = "MAIN_ENTITY";
    public Map<String, Object> toWorkflowContext() {
        Map<String, Object> c = new HashMap<>();
        c.put(CONTEXT_KEY, this);
        return c;
    }
}
