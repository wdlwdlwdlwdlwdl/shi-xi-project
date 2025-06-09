package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayChannelVO;

/**
 * 在线支付的渠道信息
 *
 * @author tiansong
 */
public enum B2BPayChannelEnum implements PayChannelInterface {
    CPT("105", "B2B Offline Payment(|public.payment|)", BizConst.CPT_LOGO),  //# 对公支付
    ACCOUNT_PERIOD("106", "B2B Deferred Payment(|account.period.payment|)", BizConst.ACCOUNT_PERIOD_LOGO);  //# 账期支付

    public static B2BPayChannelEnum getByCode(String code) {
        for (B2BPayChannelEnum payChannel : B2BPayChannelEnum.values()) {
            if (payChannel.code.equals(code)) {
                return payChannel;
            }
        }
        return null;
    }

    public static PayChannelVO getVO(B2BPayChannelEnum onlinePayChannelEnum) {
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
        for (B2BPayChannelEnum onlinePayChannelEnum : values()) {
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

    B2BPayChannelEnum(String code, String name,  String logo) {
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