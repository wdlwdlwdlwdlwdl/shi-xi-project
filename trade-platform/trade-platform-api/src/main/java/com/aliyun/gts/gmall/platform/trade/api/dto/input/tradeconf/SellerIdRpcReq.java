package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SellerIdRpcReq extends GlobalConfigRpcReq {

    @ApiModelProperty(value = "卖家ID", required = true)
    private Long sellerId;

    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("cannot.empty")+"ID不能为空");  //# "卖家
        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("seller") + " [ID] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "卖家
//        ParamUtil.expectTrue(sellerId.longValue() > 0, I18NMessageUtils.getMessage("cannot.be")+"ID不能为0");  //# "卖家
        ParamUtil.expectTrue(sellerId.longValue() > 0, I18NMessageUtils.getMessage("seller") + " [ID] " + I18NMessageUtils.getMessage("cannot.be")+" 0");  //# "卖家
    }
}
