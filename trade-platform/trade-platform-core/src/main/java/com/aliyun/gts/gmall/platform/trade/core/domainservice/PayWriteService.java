package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

public interface PayWriteService {

    /**
     * 校验支付状态
     * @param mainOrder
     */
    void checkToPay(MainOrder mainOrder);

    /**
     * 校验保存支付渠道
     * @param mainOrder
     */
    void checkSavePayChannel(MainOrder mainOrder, String payChannel);

    /**
     *构建一次性token
     * @param custId
     * @param cartId
     * @return
     */
    String generatePayToken(Long custId, String cartId);


    /**
     * 验证一次性token
     * @param token
     * @return
     */
    String getPayToken(String token);

}
