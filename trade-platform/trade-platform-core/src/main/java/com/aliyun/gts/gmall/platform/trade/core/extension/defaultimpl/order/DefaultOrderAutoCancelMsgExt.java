package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PushTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.TimeOutTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderAutoCancelMsgExt;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class DefaultOrderAutoCancelMsgExt implements OrderAutoCancelMsgExt {

    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    private ImSendManager imSendManager;

    @Autowired
    private OrderPushAbility orderPushAbility;
    @Value("${order.email.shortUrl:}")
    private String shortUrl;
    @Value("${order.push.detailUrl:}")
    private String detailUrl;
    @Value("${order.push.evaluateUrl:}")
    private String evaluateUrl;
    @Value("${order.push.cartUrl:}")
    private String cartUrl;

    @Override
    public void autoCancelSend(OrderChangeNotify change) {
        // 使用 orderList
        if (CollectionUtils.isNotEmpty(change.getOrderList())) {
            orderPushAbility.sendOrderCancel(change.getOrderList(),change.getCancelFromStatus());
        } else if (change.getMainOrder() != null) {
            sendPush(change.getMainOrder());
        }
    }

    protected void sendPush(MainOrder mainOrder){
        TimeOutTemplateEnum pushTemplateEnum = TimeOutTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus());
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
}
