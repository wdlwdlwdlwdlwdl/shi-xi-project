package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.PresaleConstants;
import com.aliyun.gts.gmall.center.trade.common.util.DateUtils;
import com.aliyun.gts.gmall.center.trade.core.constants.PresaleErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.pay.DefaultCheckPayExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.pay.CheckPayExt;
import com.aliyun.gts.gmall.platform.trade.core.input.pay.ToPayInput;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import java.util.Date;
import java.util.Map;

/**
 * 预售支付检查
 */
@Slf4j
@Extension(points = {CheckPayExt.class})
public class PresaleCheckPayExt extends DefaultCheckPayExt {

    @Override
    public TradeBizResult<Boolean> customToPayCheck(MainOrder mainOrder, ToPayInput toPayInput) {

        if (toPayInput.getStepNo() != null && toPayInput.getStepNo().intValue() == 2) {
            // 校验尾款支付时间

            Map<String, String> map = mainOrder.orderAttr().stepContextProps();
            String tailStart = map.get(PresaleConstants.CONTEXT_TAIL_START);
            String tailEnd = map.get(PresaleConstants.CONTEXT_TAIL_END);
            long now = System.currentTimeMillis();

            if (StringUtils.isNotBlank(tailStart)) {
                Date startDate = DateUtils.parseDateTime(tailStart);
                if (startDate.getTime() > now) {
                    return TradeBizResult.fail(PresaleErrorCode.TAIL_TIME_NOT_UP);
                }
            }

            if (StringUtils.isNotBlank(tailEnd)) {
                Date endDate = DateUtils.parseDateTime(tailEnd);
                if (endDate.getTime() < now) {
                    return TradeBizResult.fail(PresaleErrorCode.TAIL_TIME_OUT);
                }
            }
        }

        return super.customToPayCheck(mainOrder, toPayInput);
    }
}
