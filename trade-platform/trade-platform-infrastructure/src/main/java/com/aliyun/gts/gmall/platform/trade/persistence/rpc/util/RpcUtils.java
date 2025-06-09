package com.aliyun.gts.gmall.platform.trade.persistence.rpc.util;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class RpcUtils {

    public static <T> RpcResponse<T> invokeRpc(Supplier<RpcResponse<T>> invoker,
                                               String logApiName, String logCnName, Object logParam) {
        return invokeRpc(invoker, logApiName, logCnName, logParam, null);
    }

    public static <T> RpcResponse<T> invokeRpc(Supplier<RpcResponse<T>> invoker,
                                               String logApiName, String logCnName, Object logParam,
                                               Function<FailInfo, Boolean> notThrows) {
        RpcResponse<T> resp;
        try {
            resp = invoker.get();
        } catch (Exception e) {
            log.error("{} exception, in={}", logApiName, JSON.toJSONString(logParam), e);
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, logCnName + I18NMessageUtils.getMessage("exception"));  //# "异常"
        }
        if (!resp.isSuccess() && (notThrows == null || !notThrows.apply(resp.getFail()))) {
            log.error("{} failed, in={}, out={}", logApiName, JSON.toJSONString(logParam), JSON.toJSONString(resp));
            if (resp.getFail() != null && !CommonResponseCode.ServerError.getCode().equals(resp.getFail().getCode())) {
                throw new GmallException(map(resp.getFail(), logCnName));
            } else {
                throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, logCnName + I18NMessageUtils.getMessage("failure"));  //# "失败"
            }
        }
        log.info("{} ok, in={}, out={}", logApiName, JSON.toJSONString(logParam), JSON.toJSONString(resp));
        return resp;
    }

    public static <T> RpcResponse<T> invokeRpcNotThrow(Supplier<RpcResponse<T>> invoker,
                                                       String logApiName, String logCnName, Object logParam) {
        RpcResponse<T> resp;
        try {
            resp = invoker.get();
        } catch (Exception e) {
            log.error("{} exception, in={}", logApiName, JSON.toJSONString(logParam), e);
            return null;
        }
        if (!resp.isSuccess()) {
            log.error("{} failed, in={}, out={}", logApiName, JSON.toJSONString(logParam), JSON.toJSONString(resp));
            return resp;
        }
        log.info("{} ok, in={}, out={}", logApiName, JSON.toJSONString(logParam), JSON.toJSONString(resp));
        return resp;
    }

    private static ResponseCode map(FailInfo fail, String name) {
        return new ResponseCode() {
            @Override
            public String name() {
                return fail.getCode();
            }

            @Override
            public String getCode() {
                return fail.getCode();
            }

            @Override
            public String getScript() {
                return "";
            }

            @Override
            public String getMessage() {
                return name + I18NMessageUtils.getMessage("failure")+"," + fail.getMessage();  //# "失败
            }

            @Override
            public int getArgs() {
                return 0;
            }
        };
    }
}
