package com.aliyun.gts.gmall.platform.trade.core.domainservice;

public interface OrderAutoCancelConfigService {

    /**
     * 获取取消原因
     * @param orderStatus
     * @return
     */
    public String getCancelCodeByOrderStatus(Integer orderStatus);

}
