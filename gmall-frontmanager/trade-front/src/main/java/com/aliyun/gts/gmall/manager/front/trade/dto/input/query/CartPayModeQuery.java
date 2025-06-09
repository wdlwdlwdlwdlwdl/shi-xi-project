package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CartPayModeQuery extends LoginRestQuery {

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("城市code")
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(cityCode, I18NMessageUtils.getMessage("city.code.required"));  //# "城市校验"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product") + " [payMode] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品Id不能为空"
    }
}
