package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

@Data
public class ShopVipVO extends AbstractOutputInfo {

    private Boolean cVip;
    private Boolean bVip;
    private Boolean canJoinCVip;
    private Boolean canJoinBVip;

    private String shopLogo;    // 店铺logo
    private String shopName;    // 店铺名称
    private String shopDesc;    // 店铺描述
    private String totalScore;   // 综合评分
    private String descScore;   // 描述评分
    private String serviceScore;  // 服务评分
    private String logistScore;   // 物流评分

    private String custName;    // 用户名
    private String custNick;    // 用户昵称
    private String custPhone;   // 用户手机号
    private String custCompanyName; // 用户企业名


    public boolean isCVip() {
        return Boolean.TRUE.equals(cVip);
    }

    public boolean isBVip() {
        return Boolean.TRUE.equals(bVip);
    }

    public boolean isCanJoinCVip() {
        return Boolean.TRUE.equals(canJoinCVip);
    }

    public boolean isCanJoinBVip() {
        return Boolean.TRUE.equals(canJoinBVip);
    }
}
