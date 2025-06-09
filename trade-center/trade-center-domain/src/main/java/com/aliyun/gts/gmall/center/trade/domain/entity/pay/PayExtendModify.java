package com.aliyun.gts.gmall.center.trade.domain.entity.pay;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PayExtendModify {

    private String payId;

    private List<String> addBizTags;
    private List<String> removeBizTags;
    private Map<String, String> putBizFeature;
}
