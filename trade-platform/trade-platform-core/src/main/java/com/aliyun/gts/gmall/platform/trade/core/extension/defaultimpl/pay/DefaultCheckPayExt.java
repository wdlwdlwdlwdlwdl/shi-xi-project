package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.pay;

import com.aliyun.gts.gmall.platform.trade.core.extension.pay.CheckPayExt;
import com.aliyun.gts.gmall.platform.trade.core.input.pay.ToPayInput;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultCheckPayExt implements CheckPayExt {
    @Override
    public TradeBizResult<Boolean> customToPayCheck(MainOrder mainOrder, ToPayInput toPayInput) {

        log.info("go default extension point DefaultCheckPayExt.customToPayCheck");
        return TradeBizResult.ok(Boolean.TRUE);
    }
}
