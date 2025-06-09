package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.domain.entity.notice.NoticeMessage;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.NoticeRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.gim.api.client.ImNoticeClient;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.NoticeDTO;
import com.aliyun.gts.gmall.platform.gim.common.type.ImNoticeStatus;
import com.aliyun.gts.gmall.platform.gim.common.type.ImNoticeType;
import com.aliyun.gts.gmall.platform.gim.common.type.ImUserOutType;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoticeRepositoryImpl extends ImNoticeClient implements NoticeRepository {

    @Override
    public void publish(NoticeMessage message) {
        NoticeDTO notice = new NoticeDTO();

        // 唯一幂等ID
        notice.setBizId(message.getBizId());

        // 接收人ID
        if (message.getSellerId() != null) {
            Long uid = ImUserOutType.seller.generateUid(message.getSellerId());
            notice.setReceiveId(uid);
        } else if (message.getCustId() != null) {
            Long uid = ImUserOutType.customer.generateUid(message.getCustId());
            notice.setReceiveId(uid);
        } else {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }

        // 消息类型
        //notice.setType(ImNoticeType.innerMail.getValue());

        // 状态;设置未读
        notice.setStatus(ImNoticeStatus.unRead.getValue());

        // 模板code
        notice.setTemplateCode(message.getTemplateCode());

        // 设置消息
        notice.setFeature(new JSONObject(message.getTemplateArgs()));

        RpcUtils.invokeRpc(() -> super.pushNotice(notice),
                "ImNoticeClient.pushNotice",
                "发送IM消息", notice);
    }
}
