package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayChannelVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线支付的渠道信息
 *
 * @author tiansong
 */
public enum OnlinePayChannelEnum implements PayChannelInterface{
    ALIPAY("101", "Alipay(|alipay|)",  BizConst.ALIPAY_LOGO),  //# 支付宝
    WECHAT("102", "WeChat Pay(|wechat.payment|)",  BizConst.WECHAT_LOGO);  //# 微信支付

    public static OnlinePayChannelEnum getByCode(String code) {
        for (OnlinePayChannelEnum payChannel : OnlinePayChannelEnum.values()) {
            if (payChannel.code.equals(code)) {
                return payChannel;
            }
        }
        return null;
    }

    public static PayChannelVO getVO(OnlinePayChannelEnum onlinePayChannelEnum) {
        if (onlinePayChannelEnum == null) {
            return null;
        }
        PayChannelVO payChannelVO = new PayChannelVO();
        payChannelVO.setPayChannel(onlinePayChannelEnum.getCode());
        payChannelVO.setPayChannelName(onlinePayChannelEnum.getName());
        payChannelVO.setPayChannelLogo(onlinePayChannelEnum.getLogo());
        return payChannelVO;
    }

    public static List<PayChannelVO> getVOList() {
        List<PayChannelVO> payChannelVOList = new ArrayList<>(values().length);
        for (OnlinePayChannelEnum onlinePayChannelEnum : values()) {
            payChannelVOList.add(getVO(onlinePayChannelEnum));
        }
        return payChannelVOList;
    }

    /**
     * 支付渠道code
     */
    private String code;
    /**
     * 支付渠道名称
     */
    
    private String script;

    /**
     * 支付渠道LOGO
     */
    private String logo;

    OnlinePayChannelEnum(String code, String name,  String logo) {
        this.code = code;
        this.script = name;
        this.logo = logo;
    }

    public String getCode() {
        return this.code;
    }


    public String getLogo() {
        return this.logo;
    }
    public String getScript() {
        return script;
    }
}