package com.aliyun.gts.gmall.platform.trade.app.example.ext;

import com.aliyun.gts.gmall.platform.trade.core.extension.pay.CheckPayExt;
import com.aliyun.gts.gmall.platform.trade.core.input.pay.ToPayInput;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

@Slf4j
@Extension(points = {CheckPayExt.class})
public class ExampleCheckPayExt implements CheckPayExt {
    @Override
    public TradeBizResult<Boolean> customToPayCheck(MainOrder mainOrder, ToPayInput toPayInput) {

        log.warn("ExampleCheckPayExt@customToPayCheck occurred!");
        return TradeBizResult.ok(Boolean.TRUE);
    }
}
