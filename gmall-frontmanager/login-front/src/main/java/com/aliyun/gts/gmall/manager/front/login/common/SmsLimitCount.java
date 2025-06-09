package com.aliyun.gts.gmall.manager.front.login.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SmsLimitCount implements Serializable {

    private Date lastSendTime;

    private int totalCount;
}
