package com.aliyun.gts.gmall.test.trade.evoucher;

import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalSubOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.test.trade.base.BaseOrderTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class SimpleTest extends BaseOrderTest {

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;

    @Autowired
    private ReversalWriteFacade reversalWriteFacade;

    @Test
    public void tt() {
        tcOrderRepository.queryOrdersByPrimaryId(123L);
        tcEvoucherRepository.queryByOrderId(123L);
    }

    @Test
    public void sss() {
        long primaryOrderId = 2050020000001L;
        long subOrderId = 2050020010001L;
        long custId = 100001L;

        ReversalSubOrderInfo sub = new ReversalSubOrderInfo();
        sub.setOrderId(subOrderId);
        sub.setCancelAmt(1L);
        sub.setCancelQty(1);

        CreateReversalRpcReq req = new CreateReversalRpcReq();
        req.setCustId(custId);
        req.setPrimaryOrderId(primaryOrderId);
        req.setSubOrders(Arrays.asList(sub));
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        req.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        req.setItemReceived(false);
        req.setReversalReasonCode(201);

        RpcResponse<Long> resp = reversalWriteFacade.createReversal(req);
        printPretty(resp);

    }
}
