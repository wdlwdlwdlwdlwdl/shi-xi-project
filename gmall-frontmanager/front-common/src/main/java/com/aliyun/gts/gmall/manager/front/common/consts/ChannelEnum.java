package com.aliyun.gts.gmall.manager.front.common.consts;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

/**
 * 用户渠道信息
 *
 * @author tiansong
 */
public enum ChannelEnum  implements I18NEnum {

    H5(1, "h5",  "H5", ""),
    WX_MINI(2, "wx_mini",  "|wechat.mini.program|", "WECHAT"),   //# "微信小程序"
    PC(3, "pc",  "PC", ""),
    ALI_MINI(4, "ali_mini",  "|alipay.mini.program|", "ALIPAY");  //# 支付宝小程序

    /**
     * 获取渠道信息，默认H5
     *
     * @param code
     * @return
     */
    public static ChannelEnum get(String code) {
        if ("miniapp".equals(code)) {
            return WX_MINI; // 兼容一下 前端code
        }
        if("alimini".equals(code)){
            return ALI_MINI; // 兼容一下 前端code: 前端code完全写死整个链路都要依赖，页面参数中是不存在下划线的
        }
        for (ChannelEnum channelEnum : values()) {
            if (channelEnum.code.equals(code)) {
                return channelEnum;
            }
        }
        return H5;
    }

    /**
     * 根据账号获取
     * @param accountType
     * @return
     */
    public static ChannelEnum getByAccountType(String accountType) {
        for (ChannelEnum channelEnum : values()) {
            if (channelEnum.accountType.equals(accountType)) {
                return channelEnum;
            }
        }
        return H5;
    }

    private Integer id;
    private String code;
    
    private String script;

    private String accountType;

    ChannelEnum(Integer id,  String code, String name, String accountType) {
        this.id = id;
        this.code = code;
        this.script = name;
        this.accountType = accountType;
    }

    public Integer getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }
    public String getAccountType() {
        return accountType;
    }
    public String getScript() {
        return script;
    }
    public String getDesc() {
        return getMessage();
    }
}
