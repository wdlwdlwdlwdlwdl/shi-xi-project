package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import lombok.Data;

@Data
public class PricingToOrderVO extends ConfirmOrderSplitDTO {

    private FailInfo failInfo;
}
