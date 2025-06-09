package com.aliyun.gts.gmall.manager.front.media.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

/**
 * @description 通用数据状态枚举
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/18 11:35
 **/
public enum LikesOperateTypeEnum  implements I18NEnum {
    
    ADD("无效", "10"),
    DELETE("|valid|", "20");  //# 有效
    
    
    private String script;

    private String code;

    LikesOperateTypeEnum(String name,  String code) {
        this.script = name;
        this.code = code;
    }

    public static String getName(String code) {
        for (LikesOperateTypeEnum c : LikesOperateTypeEnum.values()) {
            if (c.getcode().equals(code)) {
                return c.getName();
            }
        }
        return null;
    }

    public String getcode() {
        return code;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }
}
