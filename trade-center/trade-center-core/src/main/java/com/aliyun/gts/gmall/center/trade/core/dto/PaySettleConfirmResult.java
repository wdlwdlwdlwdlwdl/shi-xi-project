package com.aliyun.gts.gmall.center.trade.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaySettleConfirmResult {

    private String code;
    private String msg;
    private String subCode;
    private String subMsg;
    private String tradeNo;
    private String settleAmount;

    public boolean isSuccess() {
        return StringUtils.isEmpty(subCode);
    }

}
