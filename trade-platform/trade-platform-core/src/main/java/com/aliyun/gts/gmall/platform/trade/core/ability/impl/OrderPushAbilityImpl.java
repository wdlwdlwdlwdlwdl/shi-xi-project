package com.aliyun.gts.gmall.platform.trade.core.ability.impl;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderType;
import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j

@Component
public class OrderPushAbilityImpl implements OrderPushAbility {

    @Autowired
    private ImSendManager imSendManager;

    @Autowired
    private UserRepository userRepository;

    @Value("${order.email.shortUrl:}")
    private String shortUrl;
    @Value("${order.push.detailUrl:}")
    private String detailUrl;
    @Value("${order.push.evaluateUrl:}")
    private String evaluateUrl;
    @Value("${order.push.cartUrl:}")
    private String cartUrl;

    /**
     * 发消息
     * @param orderList
     * @param type
     */
    @Override
    public void send(List<TcOrderDO> orderList,Integer type) {
        switch (type) {
            //SMS
            case 1:
                sendSms(orderList);
                break;
            //email
            case 2:
                sendEmail(orderList);
                break;
            //push
            case 3:
                sendPush(orderList);
                break;
            //站内信
            default:
                sendPush(orderList);
        }
    }

    /**
     * 发消息
     * @param mainOrder
     * @param type
     */
    @Override
    public void send(MainOrder mainOrder,Integer type) {
        switch (type) {
            //SMS
            case 1:
                sendSms(mainOrder);
                break;
            //email
            case 2:
                sendEmail(mainOrder);
                break;
            //push
            case 3:
                sendPush(mainOrder);
                break;
            //站内信
            default:
                sendPush(mainOrder);
        }
    }

    /**
     * 发APP PUSH 消息
     * @param orderList
     */
    @Override
    public void sendOrderCancel(List<TcOrderDO> orderList, Integer primaryOrderStatus){
        for (TcOrderDO tcOrderDO : orderList) {
            if(tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())){
                if(Objects.nonNull(TimeOutTemplateEnum.codeOf(primaryOrderStatus))){
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(TimeOutTemplateEnum.codeOf(primaryOrderStatus).getName());
                    imSendManager.initParam(tcOrderDO.getCustId(), msg,true);
                    Map<String, String> replacements = new HashMap<>();
                    //模板没有默认值 暂时默认10
                    replacements.put("imageHeight","0");
                    replacements.put("imageUrl",tcOrderDO.getItemPic());
                    replacements.put("advertiseLink",detailUrl+String.valueOf(tcOrderDO.getPrimaryOrderId()));
                    msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
                    msg.setSellerId(tcOrderDO.getSellerId());
                    msg.setReplacements(replacements);
                    //如果是商家物流 虚拟订单则商家接收超时属于 未完成收货超时发消息
                    //HM物流 只要delivey pick_up状态存在完成收货超时
                    if(OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode().equals(primaryOrderStatus)){
                        //虚拟订单
                        if(ExtOrderType.EVOUCHER.getCode().equals(tcOrderDO.getOrderAttr().getOrderType())){
                            imSendManager.sendMessage(msg);
                        }
                        if((LogisticsTypeEnum.COURIER_LODOOR.getCode().equals(tcOrderDO.getOrderAttr().getLogisticsType())
                                || LogisticsTypeEnum.POINT_LODOOR.getCode().equals(tcOrderDO.getOrderAttr().getLogisticsType())
                                || LogisticsTypeEnum.SELLER_KA.getCode().equals(tcOrderDO.getOrderAttr().getLogisticsType())
                        )){
                            imSendManager.sendMessage(msg);
                        }
                    }else{
                        imSendManager.sendMessage(msg);
                    }
                    break;//取一条子单商品图片就行
                }
            }
        }
    }

    @Override
    public void sendOrderCancel(MainOrder mainOrder) {
        // 给卖家发一个邮件和站内信
        sendEmail(mainOrder);
        // 给会员发一个PUSh
        sendPush(mainOrder);
    }

    /**
     * 发短信
     * @param orderList
     */
    protected void sendSms(List<TcOrderDO> orderList) {
        Map<String, String> replacements = new HashMap<>();
        for (TcOrderDO tcOrderDO : orderList) {
            if (tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())) {
                if (Objects.nonNull(SmsTemplateEnum.codeOf(tcOrderDO.getPrimaryOrderStatus()))) {
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(SmsTemplateEnum.codeOf(tcOrderDO.getPrimaryOrderStatus()).getName());
                    imSendManager.initParam(tcOrderDO.getSellerId(), msg,false);
                    Seller seller = userRepository.getSeller(tcOrderDO.getSellerId());
                    msg.setReceiver(seller.getPhone());
                    replacements.put("imageHeight", "0");
                    replacements.put("shortUrl", shortUrl + String.valueOf(tcOrderDO.getPrimaryOrderId()));
                    msg.setTemplateType(ImTemplateTypeEnum.SMS.getCode());
                    msg.setSellerId(tcOrderDO.getSellerId());
                    msg.setReplacements(replacements);
                    imSendManager.sendMessage(msg);
                }
            }else{
                //预售尾款截止时间
                if(tcOrderDO.getOrderAttr().getStepContextProps()!=null){
                    replacements.put("presaleDeadlineDate",
                            tcOrderDO.getOrderAttr().getStepContextProps().get("tailEnd"));
                }

            }
        }
    }

    /**
     * 发APP PUSH 消息
     * @param orderList
     */
    protected void sendPush(List<TcOrderDO> orderList){
        Map<String, String> replacements = new HashMap<>();
        for (TcOrderDO tcOrderDO : orderList) {
            if(tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())){
                if(Objects.nonNull(PushTemplateEnum.codeOf(tcOrderDO.getPrimaryOrderStatus()))){
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(PushTemplateEnum.codeOf(tcOrderDO.getPrimaryOrderStatus()).getName());
                    imSendManager.initParam(tcOrderDO.getCustId(), msg,true);
                    replacements.put("imageHeight", "0");
                    replacements.put("imageUrl",tcOrderDO.getItemPic());
                    if(OrderStatusEnum.COMPLETED.getCode().equals(tcOrderDO.getPrimaryOrderStatus())){
                        replacements.put("advertiseLink",evaluateUrl+String.valueOf(tcOrderDO.getPrimaryOrderId()));
                    }else {
                        replacements.put("advertiseLink",detailUrl+String.valueOf(tcOrderDO.getPrimaryOrderId()));
                    }
                    msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
                    msg.setSellerId(tcOrderDO.getSellerId());
                    msg.setReplacements(replacements);
                    imSendManager.sendMessage(msg);
                    break;//取一条子单商品图片就行
                }
            }else{
                //预售尾款截止时间
                if(tcOrderDO.getOrderAttr().getStepContextProps()!=null){
                    replacements.put("presaleDeadlineDate",
                            tcOrderDO.getOrderAttr().getStepContextProps().get("tailEnd"));
                }
                if(OrderStatusEnum.READY_FOR_PICKUP.getCode().equals(tcOrderDO.getPrimaryOrderStatus())){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if(tcOrderDO.getOrderAttr().getPickUpDeadTime()!=null) {
                        replacements.put("dateOfExpiration", formatter.format(tcOrderDO.getOrderAttr().getPickUpDeadTime()));
                    }
                }
            }
        }
    }

    /**
     * 发邮件
     * @param orderList
     */
    protected void sendEmail(List<TcOrderDO> orderList){
        Map<String, String> replacements = new HashMap<>();
        for (TcOrderDO tcOrderDO : orderList) {
            if(tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())){
                if(Objects.nonNull(EmailTemplateEnum.codeOf(tcOrderDO.getPrimaryOrderStatus()))){
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(EmailTemplateEnum.codeOf(tcOrderDO.getPrimaryOrderStatus()).getName());
                    imSendManager.initParam(tcOrderDO.getSellerId(),msg,false);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    replacements.put("imageHeight", "0");
                    replacements.put("imageUrl",tcOrderDO.getItemPic());
                    replacements.put("orderNumber", String.valueOf(tcOrderDO.getPrimaryOrderId()));
                    replacements.put("orderCreateTime", formatter.format(tcOrderDO.getGmtCreate()));
                    replacements.put("orderDeeplink", shortUrl+String.valueOf(tcOrderDO.getPrimaryOrderId()));
                    msg.setTemplateType(ImTemplateTypeEnum.EMAIL.getCode());
                    //msg.setSellerId(tcOrderDO.getSellerId());
                    msg.setReplacements(replacements);
                    msg.setHasSyncNotice(true);
                    imSendManager.sendMessage(msg);
                    break;
                }
            }else{
                //预售尾款截止时间
                if(tcOrderDO.getOrderAttr().getStepContextProps()!=null){
                    replacements.put("presaleDeadlineDate",
                            tcOrderDO.getOrderAttr().getStepContextProps().get("tailEnd"));
                }
            }
        }
    }

    /**
     * 发短信
     * @param mainOrder
     */
    protected void sendSms(MainOrder mainOrder){
        if(Objects.nonNull(SmsTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(SmsTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()).getName());
            imSendManager.initParam(mainOrder.getSeller().getSellerId(), msg,false);
            Seller seller = userRepository.getSeller(mainOrder.getSeller().getSellerId());
            msg.setReceiver(seller.getPhone());
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(mainOrder.getSubOrders())) {
                replacements.put("imageHeight", "0");
                replacements.put("shortUrl", shortUrl+String.valueOf(mainOrder.getPrimaryOrderId()));
            }
            //预售尾款截止时间
            if(mainOrder.getOrderAttr().getStepContextProps()!=null){
                replacements.put("presaleDeadlineDate",
                        mainOrder.getOrderAttr().getStepContextProps().get("tailEnd"));
            }
            msg.setTemplateType(ImTemplateTypeEnum.SMS.getCode());
            msg.setSellerId(mainOrder.getSeller().getSellerId());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    /**
     * 发APP PUSH 消息
     * @param mainOrder
     */
    protected void sendPush(MainOrder mainOrder){
        PushTemplateEnum pushTemplateEnum = PushTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus());
        if(Objects.nonNull(pushTemplateEnum)) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            // 发送PUSH
            if (PushTemplateEnum.WAITING_FOR_PAYMENT.getCode().equals(pushTemplateEnum.getCode())
                    && StepOrderUtils.isMultiStep(mainOrder)) {
                msg.setCode(PushTemplateEnum.PARTIALLY_WAITING_FOR_PAYMENT.getName());
            } else {
                msg.setCode(pushTemplateEnum.getName());
            }
            // 发邮件初始化 ，并且初始化语言参数
            imSendManager.initParam(mainOrder.getCustomer().getCustId(), msg,true);
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(mainOrder.getSubOrders())) {
                replacements.put("imageHeight", "0");
                replacements.put("imageUrl", mainOrder.getSubOrders().get(0).getItemSku().getItemPic());
            }
            if(OrderStatusEnum.COMPLETED.getCode().equals(mainOrder.getPrimaryOrderStatus())){
                replacements.put("advertiseLink",evaluateUrl + String.valueOf(mainOrder.getPrimaryOrderId()));
            } else {
                replacements.put("advertiseLink", detailUrl + String.valueOf(mainOrder.getPrimaryOrderId()));
            }
            //预售尾款截止时间
            if(mainOrder.getOrderAttr().getStepContextProps()!=null){
                replacements.put("presaleDeadlineDate",
                        mainOrder.getOrderAttr().getStepContextProps().get("tailEnd"));
            }
            if(OrderStatusEnum.READY_FOR_PICKUP.getCode().equals(mainOrder.getPrimaryOrderStatus())){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(mainOrder.getOrderAttr().getPickUpDeadTime()!=null){
                    replacements.put("dateOfExpiration",formatter.format(mainOrder.getOrderAttr().getPickUpDeadTime()));
                }
            }
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setSellerId(mainOrder.getSeller().getSellerId());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    /**
     * 发邮件
     * @param mainOrder
     */
    protected void sendEmail(MainOrder mainOrder){
        if(Objects.nonNull(EmailTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            // 发邮件消息CODE
            msg.setCode(EmailTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()).getName());
            // 发邮件初始化 ，并且初始化语言参数
            imSendManager.initParam(mainOrder.getSeller().getSellerId(), msg,false);
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(mainOrder.getSubOrders())) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                replacements.put("imageHeight", "0");
                replacements.put("imageUrl", mainOrder.getSubOrders().get(0).getItemSku().getItemPic());
                replacements.put("orderNumber", String.valueOf(mainOrder.getPrimaryOrderId()));
                replacements.put("orderCreateTime", formatter.format(mainOrder.getGmtCreate()));
                replacements.put("orderDeeplink", shortUrl + String.valueOf(mainOrder.getPrimaryOrderId()));
            }
            //预售尾款截止时间
            if(mainOrder.getOrderAttr().getStepContextProps()!=null){
                replacements.put("presaleDeadlineDate",
                        mainOrder.getOrderAttr().getStepContextProps().get("tailEnd"));
            }
            msg.setTemplateType(ImTemplateTypeEnum.EMAIL.getCode());
            msg.setSellerId(mainOrder.getSeller().getSellerId());
            msg.setHasSyncNotice(true);
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }


}