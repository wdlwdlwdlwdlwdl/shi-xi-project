package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;

public class TOrderConfirm extends AbstractContextEntity<ConfirmOrderInfoRpcReq, CreatingOrder, ConfirmOrderDTO> {

}
