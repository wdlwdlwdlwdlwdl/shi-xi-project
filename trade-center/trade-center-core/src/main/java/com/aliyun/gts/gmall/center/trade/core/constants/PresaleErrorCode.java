package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PresaleErrorCode implements ResponseCode {

    TAIL_TIME_NOT_UP("20911001", "|final.payment.time.not.yet|"),   //# "尾款支付时间未到"
    TAIL_TIME_OUT("20911002", "|final.payment.time.ended|"),   //# "尾款支付时间已结束"
    PROMOTION_DATA_ERROR("20911003", "|presale.setting.error|"),   //# "预售活动设置错误"
    ;

    String code;

    String script;

    PresaleErrorCode(String code, String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public int getArgs() {
        return 0;
    }

    public static PresaleErrorCode codeOf(String code) {
        return Arrays.stream(PresaleErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
