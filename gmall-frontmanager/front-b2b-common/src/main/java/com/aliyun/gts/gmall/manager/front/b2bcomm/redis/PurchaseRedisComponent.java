package com.aliyun.gts.gmall.manager.front.b2bcomm.redis;

public interface PurchaseRedisComponent {

    void putApproveId(Long yourId , String flowId , String appCode);

    String getApproveId(Long yourId , String appCode);

}
