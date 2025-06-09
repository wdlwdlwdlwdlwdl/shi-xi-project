package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcLogisticsDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.logistics.Logistics;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import lombok.Data;

@Data
public class TLogistics extends AbstractContextEntity<TcLogisticsRpcReq, Logistics, TcLogisticsDTO> {

}
