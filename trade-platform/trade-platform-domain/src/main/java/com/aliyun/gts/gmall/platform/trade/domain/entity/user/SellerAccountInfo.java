package com.aliyun.gts.gmall.platform.trade.domain.entity.user;

import lombok.Data;

import java.util.Map;

@Data
public class SellerAccountInfo {

    private String alipayAccountNo;

    private String wechatAccountNo;

    private Map<String, String> extendAccountInfos;

}
