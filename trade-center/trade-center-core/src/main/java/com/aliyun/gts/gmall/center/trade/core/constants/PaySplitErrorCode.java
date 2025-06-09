package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

public enum PaySplitErrorCode implements ResponseCode {
    PAY_SPLIT_INFO_MISSING("20811001", "|missing.split.billing.info|"),   //# "订单上分账信息缺失"
    PAY_SPLIT_INVOKE_GATE_WAY_ERROR("20811002", "|gateway.error.split.payment|"),   //# "分账支付调用网关出错"
    PAY_SPLIT_SAVE_FLOW_ERROR("20811003", "|error.saving.split.record|"),   //# "保存分账流水出错"
    PAY_SPLIT_RELATION_FLOW_ID_IS_EMPTY("20811003", "|split.billing.trans.id.empty|");  //# 分账关联的支付流水号为空


    String code;

    String script;

    PaySplitErrorCode(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getArgs() {
        return 0;
    }
    public String getScript() {
        return script;
    }
}
