package com.aliyun.gts.gmall.center.trade.server.flow.handler.confirmPay;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PayCATService;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConfirmPayHandlerCenter implements ProcessFlowNodeHandler<ConfirmPayCheckRpcReq, ConfirmPayCheckRpcResp> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private PayQueryService payQueryService;
    @Autowired
    PayCATService payCATService;
    @Autowired
    private OrderTaskAbility orderTaskAbility;


    @Override
    public ConfirmPayCheckRpcResp handleBiz(Map<String, Object> map, ConfirmPayCheckRpcReq req) {

        boolean result = handleConfirmPay(req);

        //// 更新订单状态、调用远程接口
        //CommUtils.retryOnException(() -> orderStatusService.onPaySuccess(mainOrder, orderPayTrade), 3, 100);

        ConfirmPayCheckRpcResp resp = new ConfirmPayCheckRpcResp();
        resp.setPaySuccess(result);
        return resp;
    }

    public boolean handleConfirmPay(ConfirmPayCheckRpcReq req) {
        boolean result = true;
        log.info("handleConfirmPay:",req.toString());
        List<Map<String, String>> payFlowDTOList = payCATService.queryPayFlowByCartId(req);
        List<MainOrder> mainOrderList = new ArrayList<>();
        // 先做状态检查
        for (Map<String, String> payFlowDTOMap : payFlowDTOList) {
            payFlowDTOMap.put("outFlowNo",req.getOutFlowNo());
            String primaryOrderId = payFlowDTOMap.get("primaryOrderId");
            MainOrder mainOrder = orderQueryAbility.getMainOrder(Long.parseLong(primaryOrderId));
            if (mainOrder == null) {
                throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
            }
            if (req.getCustId() != null && !req.getCustId().equals(mainOrder.getCustomer().getCustId())) {
                throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
            }
            mainOrderList.add(mainOrder);
        }
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
        boolean b = orderPayRepository.confirmPayCartId(payFlowDTOList, req.getCartId());
        if (b) {
            OrderPay orderPayTrade = payQueryService.queryByOrder(mainOrder);
            // 更新订单状态
            orderStatusService.onPaySuccess(mainOrder, orderPayTrade);
            MainOrder newMainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
            orderTaskAbility.orderTask(newMainOrder, newMainOrder.getPrimaryOrderStatus());
        }
        return result;
    }

}
