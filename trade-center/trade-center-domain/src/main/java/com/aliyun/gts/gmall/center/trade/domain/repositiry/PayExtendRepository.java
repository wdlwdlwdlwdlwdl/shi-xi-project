package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.entity.pay.PayExtendModify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;

import java.util.List;
import java.util.Map;

public interface PayExtendRepository {

    void updatePayExtend(PayExtendModify modify);

    OrderPay queryByPayId(String payId);

    List<Map<String, String>> queryPayFlowByCartId(Long custId, Long cartId);
}
