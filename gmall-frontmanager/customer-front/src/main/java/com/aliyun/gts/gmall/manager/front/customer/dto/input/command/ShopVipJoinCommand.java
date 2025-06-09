package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;

@Data
public class ShopVipJoinCommand extends LoginRestCommand {

    private Long sellerId;
    private Boolean joinCVip;
    private Boolean joinBVip;

    public boolean isJoinCVip() {
        return Boolean.TRUE.equals(joinCVip);
    }

    public boolean isJoinBVip() {
        return Boolean.TRUE.equals(joinBVip);
    }

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(isJoinCVip() || isJoinBVip(), "membership.b.c.required");
        ParamUtil.expectFalse(isJoinCVip() && isJoinBVip(), "membership.b.c.mutual.exclusive");
    }
}
