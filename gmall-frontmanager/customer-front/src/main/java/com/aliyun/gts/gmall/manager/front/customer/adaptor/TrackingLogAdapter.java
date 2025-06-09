package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.report.api.dto.input.FrontManagerTrackingLogMessage;
import com.aliyun.gts.gmall.framework.api.util.IpUtil;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.converter.TrackingLogConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.TrackingLogCommand;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description
 * @Author FaberWong
 * @Date 2024/8/29 13:59
 */
@Slf4j
@ApiOperation(value = "跟踪日志接口")
@Component
public class TrackingLogAdapter {


    @NacosValue(value = "${front-manager.message.track-log.topic}", autoRefreshed = true)
    @Value("${front-manager.message.track-log.topic:}")
    private String trackingLogTopic;


    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    private TrackingLogConverter converter;

    public Boolean addLog(TrackingLogCommand cmd) {
        FrontManagerTrackingLogMessage frontManagerTrackingLogMessage = converter.command2Message(cmd);
        //当前登录客户
        CustDTO user = UserHolder.getUser();
        if (user != null) {
            frontManagerTrackingLogMessage.setUserId(user.getCustId().toString());
        }
        frontManagerTrackingLogMessage.setGmtCreate(new Date());
        return messageSendManager.sendMessage(frontManagerTrackingLogMessage, trackingLogTopic, "TRACKING");
    }
}
