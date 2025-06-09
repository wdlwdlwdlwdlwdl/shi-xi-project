package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import lombok.Data;

@Data
public class TOrderCreate extends AbstractContextEntity<CreateOrderRpcReq, CreatingOrder, CreateOrderResultDTO> {

}
