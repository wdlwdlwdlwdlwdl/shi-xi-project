package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CheckReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalCheckDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import lombok.Data;

@Data
public class TReversalCheck extends AbstractContextEntity<CheckReversalRpcReq, MainReversal, ReversalCheckDTO> {

}
