package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.pay;

import com.aliyun.gts.gmall.platform.trade.core.extension.order.AfterPayExt;
import com.aliyun.gts.gmall.platform.trade.core.input.pay.ToPayInput;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultAfterPayExt implements AfterPayExt {
    @Override
    public TradeBizResult afterToPaySuccess(ToPayInput toPayInput) {
        return TradeBizResult.ok();
    }

    @Override
    public TradeBizResult afterToPayFail(ToPayInput toPayInput) {
        return TradeBizResult.ok();
    }
}
