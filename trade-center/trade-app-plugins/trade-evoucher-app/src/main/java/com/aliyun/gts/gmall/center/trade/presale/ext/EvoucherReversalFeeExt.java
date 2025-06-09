package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.constants.EvoucherErrorCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubRefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 电子凭证售后金额 & 数量 扩展逻辑
 * 1. 电子凭证全额退款,退货件数必须全退
 */
@Slf4j
@Extension(points = {ReversalFeeExt.class})
public class EvoucherReversalFeeExt extends DefaultReversalFeeExt {

    @Override
    protected void bizCheckAfterDivide(RefundCalc subRefund,
                                       RefundCalc subRemain,
                                       SubRefundFee subReversal,
                                       MainReversal mainReversal) {
        super.bizCheckAfterDivide(subRefund, subRemain, subReversal, mainReversal);

        if (subRefund.total.getTotalAmt() == subRemain.total.getTotalAmt()
                && subRefund.cancelQty != subRemain.cancelQty) {
            throw new GmallException(EvoucherErrorCode.EV_ILLEGAL_QTY_ON_REFUND_ALL);
        }
    }

}
