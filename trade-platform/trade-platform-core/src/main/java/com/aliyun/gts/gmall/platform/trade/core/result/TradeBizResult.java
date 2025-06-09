package com.aliyun.gts.gmall.platform.trade.core.result;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class TradeBizResult<T> {
    @ApiModelProperty(value = "是否业务成功", required = true)
    /**
     * 是否业务成功
     */
    private boolean success;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 业务失败详情
     */
    private FailInfo fail;

    /**
     * 额外扩展信息
     */
    private Map<String, String> extra;

    public boolean isFailed() {
        return !success;
    }

    public static <T> TradeBizResult<T> fail(String code, String message, T data) {
        TradeBizResult<T> resp = new TradeBizResult<>();
        resp.success = false;
        resp.fail = FailInfo.builder()
                .code(code)
                .message(message)
                .build();
        resp.data = data;
        return resp;
    }

    public static <T> TradeBizResult<T> ok(T data) {
        TradeBizResult<T> resp = new TradeBizResult<>();
        resp.success = true;
        resp.data = data;
        return resp;
    }

    public static <T> TradeBizResult<T> ok() {
        TradeBizResult<T> resp = new TradeBizResult<>();
        resp.success = true;
        return resp;
    }

    public static <T> TradeBizResult<T> fail(String code, String message) {
        TradeBizResult resp = new TradeBizResult();
        resp.success = false;
        resp.fail = FailInfo.builder()
                .code(code)
                .message(message)
                .build();
        return resp;
    }

    public static <T> TradeBizResult<T> fail(FailInfo failInfo) {
        TradeBizResult response = new TradeBizResult();
        response.success = false;
        response.fail = failInfo;
        return response;
    }

    public static <T> TradeBizResult<T> fail(ResponseCode code, Object... args) {
        TradeBizResult response = new TradeBizResult();
        response.success = false;
        response.fail = ErrorUtils.getFailInfo(code, args);
        return response;
    }

}
