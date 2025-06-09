package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CheckOutCreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;


public class TOrderCheckOutCreate extends AbstractContextEntity<CreateCheckOutOrderRpcReq, CheckOutCreatingOrder, CheckOutOrderResultDTO> {

}