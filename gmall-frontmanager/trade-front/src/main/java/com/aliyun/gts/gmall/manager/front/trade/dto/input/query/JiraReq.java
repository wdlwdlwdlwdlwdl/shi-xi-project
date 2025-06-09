package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import lombok.Data;

@Data
public class JiraReq {
    private String project;
    private String type;
    private String title;
    private String content;
}
