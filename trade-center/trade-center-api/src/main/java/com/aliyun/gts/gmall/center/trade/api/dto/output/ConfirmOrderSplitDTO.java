package com.aliyun.gts.gmall.center.trade.api.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import lombok.Data;

import java.util.List;

@Data
public class ConfirmOrderSplitDTO extends AbstractOutputInfo {

    private List<SplitPartDTO> splitParts;

    @Data
    public static class SplitPartDTO extends AbstractOutputInfo {
        private String bizCode;

        private boolean canOrder = true;    // 是否可下单
        private String canNotOrderCode;     // 不可下单原因code
        private String canNotOrderMessage;  // 不可下单原因message

        private List<ConfirmItemInfo> items;
    }
}
