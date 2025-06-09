package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.RefusedTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderRefundAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderRollCouponService;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.DeliveryStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderOperateFlowQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DefaultDeliveryStatusChangeExt implements DeliveryStatusChangeExt {

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Autowired
    private OrderRollCouponService orderRollCouponService;

    @Autowired
    private OrderRefundAbility orderRefundAbility;
    @Autowired
    private ImSendManager imSendManager;
    @Value("${order.push.detailUrl:}")
    private String detailUrl;

    @Override
    public TradeBizResult<List<TcOrderDO>> orderStatusChange(OrderStatus orderStatus) {

        tcOrderRepository.updateStatusAndStageByPrimaryId(
                orderStatus.getPrimaryOrderId(),
                orderStatus.getStatus().getCode(),
                orderStatus.getOrderStage(),
                orderStatus.getCheckStatus().getCode()
        );
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if(orderStatus.getRefund()){
            // 写流水
            addFlow(mainOrder, orderStatus.getStatus().getCode(), orderStatus.getCheckStatus().getCode(),false);
            //客户拒绝消息发送 switch_order_cancel_customer_refused_cust
            sendPush(mainOrder);
            orderRefundAbility.doRefund(orderStatus,mainOrder);
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                    orderStatus.getPrimaryOrderId(),
                    orderStatus.getStatus().getCode(),
                    orderStatus.getOrderStage(),
                    orderStatus.getCheckStatus().getCode()
            );
            // 退券
            orderRollCouponService.orderRollCoupon(mainOrder);
        }
        // 创建任务
        orderTaskAbility.orderTask(mainOrder,orderStatus.getStatus().getCode());
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(orderStatus.getPrimaryOrderId());
        return TradeBizResult.ok(list);
    }

    /**
     * 写流水
     * @param mainOrder
     * @param status
     * @param checkStatus
     * @param isCustomer
     */
    protected void addFlow(MainOrder mainOrder, Integer status, Integer checkStatus,  boolean isCustomer) {
        OrderOperateFlowQuery query = new OrderOperateFlowQuery();
        query.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        query.setFromOrderStatus(checkStatus);
        query.setToOrderStatus(status);
        if(!tcOrderOperateFlowRepository.exist(query)){
            TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
            flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            flow.setOrderId(mainOrder.getPrimaryOrderId());
            flow.setFromOrderStatus(checkStatus);
            flow.setToOrderStatus(status);
            flow.setOperatorType(isCustomer ? 1 : 0);
            flow.setGmtCreate(new Date());
            flow.setOpName(OrderStatusEnum.codeOf(status).getInner());
            flow.setOperator(String.valueOf(mainOrder.getCustomer().getCustId()));
            flow.setGmtModified(new Date());
            tcOrderOperateFlowRepository.create(flow);
        }
    }

    private void sendPush(MainOrder mainOrder){
        if(Objects.nonNull(RefusedTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(RefusedTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()).getName());
            imSendManager.initParam(mainOrder.getCustomer().getCustId(), msg,true);
            Map<String, String> replacements = new HashMap<>();
            MainOrder order = orderQueryAbility.getMainOrder(mainOrder.getPrimaryOrderId());
            if (CollectionUtils.isNotEmpty(order.getSubOrders())) {
                replacements.put("imageHeight", "0");//模板默认值
                replacements.put("imageUrl", order.getSubOrders().get(0).getItemSku().getItemPic());
            }

            replacements.put("advertiseLink",detailUrl+String.valueOf(mainOrder.getPrimaryOrderId()));
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setSellerId(order.getSeller().getSellerId());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

}
