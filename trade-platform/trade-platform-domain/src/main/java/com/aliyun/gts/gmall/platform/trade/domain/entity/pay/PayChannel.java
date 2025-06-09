package com.aliyun.gts.gmall.platform.trade.domain.entity.pay;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

import java.util.Date;

@Data
public class PayChannel extends AbstractBusinessEntity {
    private Long id;
    private String channelCode;
    private String channelName;
    private String methodCode;
    private String methodName;
    private Integer typeCode;
    private String typeName;
    private String orderChannelCode;
    private String refundChannelCode;
    private Integer paySequence;
    private String remark;
    private Date gmtCreate;
    private Date gmtModified;
    private Boolean deleted;
    private String marketId;
}
