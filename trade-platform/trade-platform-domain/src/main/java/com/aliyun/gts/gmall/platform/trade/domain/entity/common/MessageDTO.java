package com.aliyun.gts.gmall.platform.trade.domain.entity.common;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import lombok.Data;

import java.util.Map;

@Data
public class MessageDTO<T extends Transferable> {

    private T message;
    private String topic;
    private String tag;
    private Long delayTime;
    private String shardingKey;

    private Map<String, String> userProperties;
}
