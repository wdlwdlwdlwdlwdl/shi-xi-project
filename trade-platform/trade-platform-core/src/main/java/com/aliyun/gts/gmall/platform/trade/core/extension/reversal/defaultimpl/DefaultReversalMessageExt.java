package com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl;

import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.MessageConstant;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ReversalMessageDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ToRefundMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.RefusedTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.TimeOutTemplateEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.ReversalPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalMessageExt;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class DefaultReversalMessageExt implements ReversalMessageExt {

    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    ReversalPushAbility reversalPushAbility;

    @Autowired
    private ImSendManager imSendManager;

    @Value("${trade.reversal.statuschange.topic:GMALL_TRADE_REVERSAL_STATUS_CHANGE}")
    private String statusChangeTopic;

    @Value("${trade.reversal.torefund.topic:GMALL_TRADE_REVERSAL_TO_REFUND}")
    private String toRefundTopic;

    @Value("${order.email.reversalShortUrl:}")
    private String shortUrl;
    @Autowired
    private ReversalQueryService reversalQueryService;

    @Value("${order.push.detailUrl:}")
    private String detailUrl;
    /**
     * 退单修改状态消息发送
     * @param reversal
     * @param targetStatus
     */
    @Override
    public void sendStatusChangedMessage(MainReversal reversal, ReversalStatusEnum targetStatus) {
        // 再查一次
        MainReversal newReversal = reversalQueryService.queryReversal(
                reversal.getPrimaryReversalId(),
                ReversalDetailOption.builder().includeOrderInfo(true).build()
        );
        // 同意退货
        if (targetStatus == ReversalStatusEnum.REFUND_APPROVED) {
            // 发送退款消息
            ToRefundMessageDTO refund = ToRefundMessageDTO
                .builder()
                .primaryReversalId(reversal.getPrimaryReversalId())
                .build();
            messageSendManager.sendMessage(refund, toRefundTopic, MessageConstant.TRADE_PAY_REFUND);
        }
        // 卖家拒绝退货
        else if(targetStatus == ReversalStatusEnum.REFUND_PART_SUCCESS || targetStatus==ReversalStatusEnum.COMPLETED){
            sendCancelPush(reversal);
            //售后商家拒绝返回之前状态
        }
        // 发送状态变更消息
        ReversalMessageDTO msg = ReversalMessageDTO
            .builder()
            .primaryReversalId(newReversal.getPrimaryReversalId())
            .reversalStatus(targetStatus.getCode())
            .fromReversalStatus(newReversal.getReversalStatus())
            .version(newReversal.getVersion())
            .build();
        messageSendManager.sendMessage(msg, statusChangeTopic, String.valueOf(targetStatus.getCode()));
        //发送推送会员PUSH
        reversalPushAbility.send(newReversal, ImTemplateTypeEnum.PUSH.getCode());
        sendTimeOutPush(reversal);
        // 只有用户申请的时候 给卖家发消息 后面的不用发了
        // 设计枚举配置哪些状态就发哪些
        if (targetStatus == ReversalStatusEnum.WAITING_FOR_ACCEPT ||
            targetStatus == ReversalStatusEnum.WAITING_FOR_RETURN) {
            // 给卖家发邮件
            reversalPushAbility.send(newReversal, ImTemplateTypeEnum.EMAIL.getCode());
        }
    }

    /**
     * 卖家拒绝退货
     *    发送取消退货
     * @param reversal
     */
    public void sendCancelPush(MainReversal reversal){
        ImCommonMessageRequest msg = new ImCommonMessageRequest();
        if (Objects.nonNull(RefusedTemplateEnum.codeOf(reversal.getReversalStatus()))) {
            msg.setCode(RefusedTemplateEnum.codeOf(reversal.getReversalStatus()).getName());
            imSendManager.initParam(reversal.getMainOrder().getCustomer().getCustId(), msg, true);
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(reversal.getSubReversals())) {
                replacements.put("imageHeight", "0");
                replacements.put("imageUrl", reversal.getSubReversals().get(0).getSubOrder().getItemSku().getItemPic());
            }
            replacements.put("advertiseLink", detailUrl + String.valueOf(reversal.getMainOrder().getPrimaryOrderId()));
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    public void sendTimeOutPush(MainReversal reversal){
        ImCommonMessageRequest msg = new ImCommonMessageRequest();
        if (Objects.nonNull(TimeOutTemplateEnum.codeOf(reversal.getReversalStatus()))) {
            msg.setCode(TimeOutTemplateEnum.codeOf(reversal.getReversalStatus()).getName());
            imSendManager.initParam(reversal.getMainOrder().getCustomer().getCustId(), msg, true);
            Map<String, String> replacements = new HashMap<>();
            if (CollectionUtils.isNotEmpty(reversal.getSubReversals())) {
                replacements.put("imageHeight", "0");
                replacements.put("imageUrl", reversal.getSubReversals().get(0).getSubOrder().getItemSku().getItemPic());
            }
            replacements.put("advertiseLink", detailUrl + String.valueOf(reversal.getMainOrder().getPrimaryOrderId()));
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    @Override
    public void autoRefundMessage(MainReversal reversal, ReversalStatusEnum targetStatus) {
        ToRefundMessageDTO refund = ToRefundMessageDTO.builder()
            .primaryReversalId(reversal.getPrimaryReversalId())
            .build();
        messageSendManager.sendMessage(refund, toRefundTopic, MessageConstant.TRADE_PAY_REFUND);
        // 发送状态变更消息
        ReversalMessageDTO msg = ReversalMessageDTO.builder()
            .primaryReversalId(reversal.getPrimaryReversalId())
            .reversalStatus(targetStatus.getCode())
            .fromReversalStatus(reversal.getReversalStatus())
            .version(reversal.getVersion())
            .build();
        messageSendManager.sendMessage(msg, statusChangeTopic, String.valueOf(targetStatus.getCode()));
    }
}
