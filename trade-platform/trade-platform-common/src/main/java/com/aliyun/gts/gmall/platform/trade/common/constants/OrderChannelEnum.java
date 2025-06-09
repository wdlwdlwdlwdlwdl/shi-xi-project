package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;

import java.util.Arrays;

public enum OrderChannelEnum  implements I18NEnum {

    WX_MINIAPP(ChannelEnum.WX_MINI.getCode(), "|wechat|"),   //# "微信小程序"
    H5(ChannelEnum.H5.getCode(), "h5|page|"),   //# 页面"
    SELLER("seller", "|backend|");  //# 卖家后台

    // ========================

    private final String code;
    
    private final String script;


    OrderChannelEnum(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static OrderChannelEnum codeOf(String code) {
        return Arrays.stream(OrderChannelEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }

    public String getName(){
        return getMessage();
    }
    public String getScript() {
        return script;
    }

    // 获取解析
    public String getDesc() { return getMessage(); }

}
