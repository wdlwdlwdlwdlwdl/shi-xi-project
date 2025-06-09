package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.pay.api.dto.message.RefundSuccessMessage;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ToRefundMessageDTO;

public interface PayRefundDomainService {

    /**
     * 退款执行
     * @param message
     * @throws Exception
     */
    void toRefundExecute(ToRefundMessageDTO message) throws Exception;


    boolean payRefundExecute(RefundSuccessMessage message) throws Exception;

}
