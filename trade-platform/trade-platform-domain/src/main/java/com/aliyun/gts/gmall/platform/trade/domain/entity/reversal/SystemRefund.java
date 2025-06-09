package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.SystemReversalReason;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class SystemRefund {

    private MainOrder mainOrder;

    private int reasonCode = SystemReversalReason.SYSTEM_REFUND.getCode();

    private String reasonContent = SystemReversalReason.SYSTEM_REFUND.getName();

    private Integer reversalType = ReversalTypeEnum.REFUND_ONLY.getCode();

    private String channel = OrderChannelEnum.H5.getCode();

    private Consumer<MainReversal> beforeSetAmt;

    private Consumer<MainReversal> afterSetAmt;

    private Consumer<MainReversal> beforeSave;
}
