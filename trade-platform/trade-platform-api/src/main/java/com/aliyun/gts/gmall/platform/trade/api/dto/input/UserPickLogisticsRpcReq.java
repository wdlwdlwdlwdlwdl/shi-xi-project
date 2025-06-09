package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Data;

@Data
public class UserPickLogisticsRpcReq extends AbstractCommandRpcRequest {

    /**
     * 购买用户的id，分库分表健
     */
    private Long custId;

    /**
     * 物流方式
     */
    private String deliveryType;

    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.nonNull(deliveryType, I18NMessageUtils.getMessage("invalid.param"));  //# "登录会员ID不能为空"
    }
}