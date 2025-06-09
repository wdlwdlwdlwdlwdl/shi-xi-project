package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.dto.ClientInfo;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "可加购商品校验入参")
public class CheckAddCartRpcReq extends AddCartRpcReq {

    /**
     * 加购检查非 Command
     */
    @Override
    public boolean isWrite() {
        return false;
    }

    /**
     * 加购检查时 itemQty 非必填
     */
    @Override
    public Integer getItemQty() {
        Integer qty = super.getItemQty();
        return qty == null ? 0 : qty;
    }

    /**
     * 加购检查时 clientInfo 非必填
     */
    @Override
    public ClientInfo getClientInfo() {
        ClientInfo cli = super.getClientInfo();
        return cli == null ? ClientInfo.builder().userId("0").build() : cli;
    }

    @Override
    public void checkInput() {
        ParamUtil.nonNull(getCustId(), I18NMessageUtils.getMessage("login.member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.nonNull(getItemId(), I18NMessageUtils.getMessage("product")+" [Id] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品Id不能为空"
        ParamUtil.nonNull(getSkuId(), I18NMessageUtils.getMessage("product")+" [SKU-ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品SKU-ID不能为空"
    }
}
