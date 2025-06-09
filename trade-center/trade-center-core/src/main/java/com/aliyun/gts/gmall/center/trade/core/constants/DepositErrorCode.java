package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DepositErrorCode implements ResponseCode {

    ITEM_INFO_ERROR("20910001", "|deposit.product.setting.error|"),   //# "定金商品设置不正确"
    LAST_SEND_TIME_EARLIER("20910002", "|delivery.date.too.early|");  //# 交期设置的日期过早

    String code;

    String script;

    DepositErrorCode(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public int getArgs() {
        return 0;
    }

    public static DepositErrorCode codeOf(String code) {
        return Arrays.stream(DepositErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
