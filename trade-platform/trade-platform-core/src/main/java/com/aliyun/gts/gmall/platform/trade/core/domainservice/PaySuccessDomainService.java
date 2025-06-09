package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;

public interface PaySuccessDomainService {

    TradeBizResult paySuccessExecute(PaySuccessMessage message);

}
