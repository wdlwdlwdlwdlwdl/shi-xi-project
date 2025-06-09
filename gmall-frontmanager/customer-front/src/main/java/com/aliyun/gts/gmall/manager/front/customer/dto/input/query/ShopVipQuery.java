package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import lombok.Data;

@Data
public class ShopVipQuery extends AbstractQueryRestRequest {

    private Long sellerId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("seller")+"id"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "卖家id不能为空"
    }
}
