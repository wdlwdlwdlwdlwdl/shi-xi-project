package com.aliyun.gts.gmall.platform.trade.server.service.wrapper;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.Data;

@Data
public class TradeMessageWrapper implements Transferable {

    private MainOrder mainOrder;
}
