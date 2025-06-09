package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderCancelMsgExt;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
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
public class DefaultOrderCancelMsgExt implements OrderCancelMsgExt {

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
    public void orderCancelSend(OrderChangeNotify change) {
        // 使用 orderList
        if (CollectionUtils.isNotEmpty(change.getOrderList())) {
            // 给客户发PUSH
            //orderPushAbility.send(change.getOrderList(), ImTemplateTypeEnum.PUSH.getCode());
            sendPush(change.getOrderList(),change.getCancelFromStatus());
            // 客户取消给卖家发邮件
            sendEmail(change.getOrderList(),change.getCancelFromStatus());
        } else if (change.getMainOrder() != null) {
            //给客户发PUSH
            orderPushAbility.send(change.getMainOrder(),ImTemplateTypeEnum.PUSH.getCode());
            // 给卖家发邮件
            sendEmail(change.getMainOrder());
        }
    }

    /**
     * 发邮件
     * @param orderList
     */
    protected void sendEmail(List<TcOrderDO> orderList,int cancelStatus){
        Map<String, String> replacements = new HashMap<>();
        for (TcOrderDO tcOrderDO : orderList) {
            if(tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())){
                if(Objects.nonNull(CancelEmailTemplateEnum.codeOf(cancelStatus))){
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(CancelEmailTemplateEnum.codeOf(cancelStatus).getName());
                    imSendManager.initParam(tcOrderDO.getSellerId(),msg,false);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    replacements.put("imageHeight","0");
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
     * 发邮件
     * @param mainOrder
     */
    protected void sendEmail(MainOrder mainOrder){
        if(Objects.nonNull(CancelEmailTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            // 发邮件消息CODE
            msg.setCode(CancelEmailTemplateEnum.codeOf(mainOrder.getPrimaryOrderStatus()).getName());
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

    /**
     * 发APP PUSH 消息
     * @param orderList
     */
    protected void sendPush(List<TcOrderDO> orderList,int cancelStatus){
        Map<String, String> replacements = new HashMap<>();
        for (TcOrderDO tcOrderDO : orderList) {
            if(tcOrderDO.getPrimaryOrderFlag().equals(PrimaryOrderFlagEnum.SUB_ORDER.getCode())){
                if(Objects.nonNull(CustCancelTemplateEnum.codeOf(cancelStatus))){
                    ImCommonMessageRequest msg = new ImCommonMessageRequest();
                    msg.setCode(CustCancelTemplateEnum.codeOf(cancelStatus).getName());
                    imSendManager.initParam(tcOrderDO.getCustId(), msg,true);
                    replacements.put("imageHeight","0");
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
}
