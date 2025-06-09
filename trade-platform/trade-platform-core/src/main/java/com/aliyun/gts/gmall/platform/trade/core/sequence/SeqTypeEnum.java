package com.aliyun.gts.gmall.platform.trade.core.sequence;

import com.aliyun.gts.gmall.platform.trade.core.functions.GenerateSeqFunction;

public enum SeqTypeEnum {

    PAY_FLOW,
    ORDER,
    ORDER_PAY,
    PAY_SPLIT_FLOW;

    public String generateSeq(String custId, GenerateSeqFunction<String, String> generateSeqFunction) {
        return generateSeqFunction.convert(custId);
    }

}
