package com.aliyun.gts.gmall.platform.trade.core.ability.impl;

import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.EmailTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PushTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.ReversalPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
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
public class ReversalPushAbilityImpl implements ReversalPushAbility {

    @Autowired
    private ImSendManager imSendManager;

    @Value("${order.email.reversalShortUrl:}")
    private String shortUrl;

    @Value("${order.push.detailUrl:}")
    private String detailUrl;

    @Value("${order.push.evaluateUrl:}")
    private String evaluateUrl;

    @Value("${order.push.cartUrl:}")
    private String cartUrl;

    @Override
    public void send(MainReversal reversal, Integer type) {
        switch (type) {
            //SMS
            case 1:
                break;
            //email
            case 2:
                sendEmail(reversal);
                break;
            //push
            case 3:
                sendPush(reversal);
                break;
            //站内信
            default:
        }
    }


    public void sendPush(MainReversal reversal){
        if(Objects.nonNull(PushTemplateEnum.codeOf(reversal.getReversalStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(PushTemplateEnum.codeOf(reversal.getReversalStatus()).getName());
            imSendManager.initParam(reversal.getMainOrder().getCustomer().getCustId(), msg,true);
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(reversal.getSubReversals())) {
                replacements.put("imageHeight", "0");
                replacements.put("imageUrl", reversal.getSubReversals().get(0).getSubOrder().getItemSku().getItemPic());
            }
            if(OrderStatusEnum.COMPLETED.getCode().equals(reversal.getMainOrder().getPrimaryOrderStatus())){
                replacements.put("advertiseLink", evaluateUrl + String.valueOf(reversal.getMainOrder().getPrimaryOrderId()));
            }else {
                replacements.put("advertiseLink", detailUrl + String.valueOf(reversal.getMainOrder().getPrimaryOrderId()));
            }
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setSellerId(reversal.getSellerId());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    public void sendEmail(MainReversal reversal){
        if(Objects.nonNull(EmailTemplateEnum.codeOf(reversal.getReversalStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(EmailTemplateEnum.codeOf(reversal.getReversalStatus()).getName());
            imSendManager.initParam(reversal.getSellerId(),msg,false);
            Map<String, String> replacements = new HashMap<>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (CollectionUtils.isNotEmpty(reversal.getSubReversals())) {
                replacements.put("imageHeight", "0");
                replacements.put("imageUrl", reversal.getSubReversals().get(0).getSubOrder().getItemSku().getItemPic());
            }
            replacements.put("orderNumber", String.valueOf(reversal.getMainOrder().getPrimaryOrderId()));
            replacements.put("orderCreateTime", formatter.format(reversal.getMainOrder().getGmtCreate()));
            replacements.put("orderDeeplink", shortUrl+String.valueOf(reversal.getMainOrder().getPrimaryOrderId()));
            msg.setTemplateType(ImTemplateTypeEnum.EMAIL.getCode());
            msg.setSellerId(reversal.getSellerId());
            msg.setHasSyncNotice(true);
            msg.setReplacements(replacements);
            log.info("imSendManager.sendMessage:"+msg.toString());
            imSendManager.sendMessage(msg);
        }
    }
}
