package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.MessageConstant;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.CustCancelTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.SmsTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderMessageExt;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.SalesInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class DefaultOrderMessageExt implements OrderMessageExt {

    @Value("${trade.order.statuschange.topic}")
    private String statusChangeTopic;

    @Value("${trade.order.ordersuccess.topic}")
    private String orderSuccessTopic;

    @Value("${order.email.shortUrl:}")
    private String shortUrl;
    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    private ImSendManager imSendManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    OrderPushAbility orderPushAbility;

    @Override
    public void orderMessageSend(OrderChangeNotify change) {
        // 使用 orderList
        if (CollectionUtils.isNotEmpty(change.getOrderList())) {
            //取消订单状态
            if(OrderStatusEnum.CANCELLED.getCode().equals(change.getFromOrderStatus())){
                sendCancelMsg(change.getOrderList(), change.getFromOrderStatus());
            }
            //发订单状态MQ消息
            sendMessage(change.getOrderList(), change);
            // 发客户通知PUSH
            sendPush(change);
            // 发订单完成MQ 消息
            if (change.getOp().isOrderSuccess()) {
                sendSuccessMessage(change.getOrderList(), change);
            }
        } else if (change.getMainOrder() != null) {
            //取消订单状态
            if(OrderStatusEnum.CANCELLED.getCode().equals(change.getFromOrderStatus())){
                sendCancelMsg(change.getMainOrder(), change.getFromOrderStatus());
            }
            //发订单状态MQ消息
            sendMessage(change.getMainOrder(), change);
            // 发客户通知PUSH
            sendPush(change);
            // 发订单完成MQ 消息
            if (change.getOp().isOrderSuccess()) {
                sendSuccessMessage(change.getMainOrder(), change);
            }
        }
    }


    private void sendPush(OrderChangeNotify change) {
        if(CollectionUtils.isNotEmpty(change.getOrderList())){
            orderPushAbility.send(change.getOrderList(), ImTemplateTypeEnum.SMS.getCode());
            orderPushAbility.send(change.getOrderList(), ImTemplateTypeEnum.PUSH.getCode());
            orderPushAbility.send(change.getOrderList(), ImTemplateTypeEnum.EMAIL.getCode());
        }else {
            orderPushAbility.send(change.getMainOrder(), ImTemplateTypeEnum.SMS.getCode());
            orderPushAbility.send(change.getMainOrder(), ImTemplateTypeEnum.PUSH.getCode());
            orderPushAbility.send(change.getMainOrder(), ImTemplateTypeEnum.EMAIL.getCode());
        }
    }


    @Override
    public void orderMqMessageSend(OrderChangeNotify change) {
        // 使用 orderList
        if (CollectionUtils.isNotEmpty(change.getOrderList())) {
            sendMessage(change.getOrderList(), change);
            if (change.getOp().isOrderSuccess()) {
                sendSuccessMessage(change.getOrderList(), change);
            }
        } else if (change.getMainOrder() != null) {
            sendMessage(change.getMainOrder(), change);
            if (change.getOp().isOrderSuccess()) {
                sendSuccessMessage(change.getMainOrder(), change);
            }
        }
    }

    /**
     * 取消订单PUSH 发动
     *     封装一个就好了。。。
     * @param orderList
     * @param fromOrderStatus
     */
    protected void sendCancelMsg(List<TcOrderDO> orderList,Integer fromOrderStatus){
        for (TcOrderDO tcOrderDO : orderList) {
            if(tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())){
                if(Objects.nonNull(CustCancelTemplateEnum.codeOf(fromOrderStatus))){
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(CustCancelTemplateEnum.codeOf(fromOrderStatus).getName());
                    imSendManager.initParam(tcOrderDO.getCustId(), msg,true);
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("imageHeight","0");
                    replacements.put("imageUrl",tcOrderDO.getItemPic());
                    msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
                    msg.setReplacements(replacements);
                    imSendManager.sendMessage(msg);
                    break;//取一条子单商品图片就行
                }
            }
        }
    }

    /**
     * 取消订单PUSH 发动
     *     封装一个就好了。。。
     * @param mainOrder
     * @param fromOrderStatus
     */
    protected void sendCancelMsg(MainOrder mainOrder, Integer fromOrderStatus){
        if(Objects.nonNull(CustCancelTemplateEnum.codeOf(fromOrderStatus))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(CustCancelTemplateEnum.codeOf(fromOrderStatus).getName());
            imSendManager.initParam(mainOrder.getCustomer().getCustId(), msg,true);
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(mainOrder.getSubOrders())) {
                replacements.put("imageHeight", "0");//模板默认值
                replacements.put("imageUrl", mainOrder.getSubOrders().get(0).getItemSku().getItemPic());
            }
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    protected void sendMessage(List<TcOrderDO> orderList, OrderChangeNotify change) {
        for (TcOrderDO tcOrderDO : orderList) {
            OrderMessageDTO msg = toOrderMsg(tcOrderDO, change);
            messageSendManager.sendMessage(msg, statusChangeTopic, tcOrderDO.getOrderStatus() + "");
        }
    }

    /**
     * 订单状态消息发送
     * @param mainOrder
     * @param change
     */
    protected void sendMessage(MainOrder mainOrder, OrderChangeNotify change) {
        OrderMessageDTO mainMsg = toMainMsg(mainOrder, change);
        messageSendManager.sendMessage(mainMsg, statusChangeTopic, mainMsg.getOrderStatus() + "");
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            OrderMessageDTO subMsg = toSubMsg(subOrder, mainOrder, change);
            messageSendManager.sendMessage(subMsg, statusChangeTopic, subMsg.getOrderStatus() + "");
        }
    }

    /**
     * 订单完成消息
     * @param orderList
     * @param change
     */
    protected void sendSuccessMessage(List<TcOrderDO> orderList, OrderChangeNotify change) {
        TcOrderDO order = orderList.stream()
            .filter(o -> PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode().equals(o.getPrimaryOrderFlag()))
            .findFirst().orElse(null);
        if (order != null) {
            OrderMessageDTO msg = toOrderMsg(order, change);
            messageSendManager.sendMessage(msg, orderSuccessTopic, MessageConstant.SUCCESS);
        }
    }

    /**
     * 订单完成消息
     * @param mainOrder
     * @param change
     */
    protected void sendSuccessMessage(MainOrder mainOrder, OrderChangeNotify change) {
        OrderMessageDTO msg = toMainMsg(mainOrder, change);
        messageSendManager.sendMessage(msg, orderSuccessTopic, MessageConstant.SUCCESS);
    }

    protected OrderMessageDTO toOrderMsg(TcOrderDO order, OrderChangeNotify change) {
        OrderMessageDTO dto = new OrderMessageDTO();
        BeanUtils.copyProperties(order, dto);
        if (order.getOrderAttr() != null) {
            dto.setOrderTags(order.getOrderAttr().getTags());
            dto.setOrderFeatures(order.getOrderAttr().getExtras());
            dto.setStepNo(order.getOrderAttr().getCurrentStepNo());
            dto.setStepOrderStatus(order.getOrderAttr().getCurrentStepStatus());
        }
        if (change.getOp() == OrderChangeOperateEnum.SELLER_CHANGE_FEE) {
            dto.setChangePrice(true);
        }
        return dto;
    }

    protected OrderMessageDTO toMainMsg(MainOrder mainOrder, OrderChangeNotify change) {
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        dto.setOrderId(mainOrder.getPrimaryOrderId());
        dto.setOrderStatus(mainOrder.getPrimaryOrderStatus());
        dto.setPrimaryOrderStatus(mainOrder.getPrimaryOrderStatus());
        dto.setPrimaryOrderFlag(PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode());
        dto.setGmtCreate(mainOrder.getGmtCreate());
        dto.setGmtModified(mainOrder.getGmtModified());
        dto.setSellerId(mainOrder.getSeller().getSellerId());
        dto.setCustId(mainOrder.getCustomer().getCustId());
        dto.setOrderFeatures(mainOrder.orderAttr().getExtras());
        dto.setOrderTags(mainOrder.orderAttr().getTags());
        dto.setVersion(mainOrder.getVersion());
        dto.setStepNo(mainOrder.orderAttr().getCurrentStepNo());
        dto.setStepOrderStatus(mainOrder.orderAttr().getCurrentStepStatus());
        if (change.getOp() == OrderChangeOperateEnum.SELLER_CHANGE_FEE) {
            dto.setChangePrice(true);
        }
        return dto;
    }

    protected OrderMessageDTO toSubMsg(SubOrder subOrder, MainOrder mainOrder, OrderChangeNotify change) {
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setPrimaryOrderId(subOrder.getPrimaryOrderId());
        dto.setOrderId(subOrder.getOrderId());
        dto.setOrderStatus(subOrder.getOrderStatus());
        dto.setPrimaryOrderStatus(mainOrder.getPrimaryOrderStatus());
        dto.setPrimaryOrderFlag(PrimaryOrderFlagEnum.SUB_ORDER.getCode());
        dto.setGmtCreate(subOrder.getGmtCreate());
        dto.setGmtModified(subOrder.getGmtModified());
        dto.setSellerId(mainOrder.getSeller().getSellerId());
        dto.setCustId(mainOrder.getCustomer().getCustId());
        dto.setOrderFeatures(subOrder.orderAttr().getExtras());
        dto.setOrderTags(subOrder.orderAttr().getTags());
        dto.setVersion(subOrder.getVersion());
        if (change.getOp() == OrderChangeOperateEnum.SELLER_CHANGE_FEE) {
            dto.setChangePrice(true);
        }
        return dto;
    }

    public static void main(String[] args) {
        System.out.println(SmsTemplateEnum.codeOf(72).getName());
        System.out.println(SmsTemplateEnum.codeOf(64).getName());
    }
}
