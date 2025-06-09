package com.aliyun.gts.gmall.platform.trade.server.flow.handler;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public interface TradeFlowHandler<IN extends AbstractContextEntity> {

    /**
     * 主逻辑
     */
    void handle(IN inbound);

    /**
     * 当流程异常时执行的逻辑 （ AbstractContextEntity.isError =true )
     */
    default void handleError(IN inbound) {

    }

    default String getExceptionErrCode() {
        return CommonErrorCode.SERVER_ERROR.getCode();
    }


    /**
     * 将TradeFlowHandler 适配到流程引擎框架
     */
    @Slf4j
    abstract class AdapterHandler<IN extends AbstractContextEntity>
            implements ProcessFlowNodeHandler<Object, Object>, TradeFlowHandler<IN> {
        private static final boolean PRINT_TIME = false;

        @Override
        public Object handleBiz(Map<String, Object> map, Object o) {
            if (PRINT_TIME) {
                return CommUtils.logTime(() -> run(map, o), getClass().getSimpleName());
            } else {
                return run(map, o);
            }
        }

        private Object run(Map<String, Object> map, Object o) {
            IN in = (IN) map.get(AbstractContextEntity.CONTEXT_KEY);
            if (in.isError()) {
                try {
                    handleError(in);
                } catch (Exception e) {
                    log.error("", e);   // 继续后面的 handleError 节点
                }
                return null;
            }

            try {
                handle(in);
            } catch (Exception e) {
                log.error("", e);
                String msg = e.getMessage();
                if (StringUtils.isBlank(msg)) {
                    msg = e.toString();
                }
                String code;
                if (e instanceof GmallException) {
                    code = ((GmallException) e).getFrontendCare().getCode().getCode();
                } else {
                    code = getExceptionErrCode();
                }
                in.setError(code, msg);
                try {
                    handleError(in);
                } catch (Exception e2) {
                    log.error("", e2);   // 继续后面的 handleError 节点
                }
            }
            return null;
        }
    }
}
