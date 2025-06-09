package com.aliyun.gts.gmall.platform.trade.core.util;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.BizScope;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServerUtils {

    public static <T> RpcResponse<T> invoke(ThrowSupplier<RpcResponse<T>> invoker, String name) {
        try {
            return invoker.get();
        } catch (GmallException e) {
            log.error("{} occurred exceptions!", name, e);
            return RpcResponse.fail(e.getFrontendCare().getCode().getCode(),
                    e.getFrontendCare().detailMessage());
        } catch (Exception e) {
            log.error("{} occurred exceptions!", name, e);
            return RpcResponse.fail(CommonErrorCode.SERVER_ERROR.getCode(),
                    CommonErrorCode.SERVER_ERROR.getMessage() + ":" + e.getMessage());
        }
    }

    public static <T> RpcResponse<T> invoke(ThrowSupplier<RpcResponse<T>> invoker, String name, BizCodeEntity bizCode) {
        return new BizScope<RpcResponse<T>>(bizCode) {
            @Override
            protected RpcResponse<T> execute() {
                try {
                    return RpcServerUtils.invoke(invoker, name);
                } catch (Exception e) {
                    return ErrorUtils.throwUndeclared(e);
                }
            }
        }.invoke();
    }


    public interface ThrowSupplier<T> {
        T get() throws Exception;
    }
}
