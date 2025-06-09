package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Data;

@Data
public class ItemDetailLinkRestQuery  extends AbstractPageQueryRestRequest {

    // skuId
    private Long skuId;

    // 跳转链接
    private String linkUrl;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("sku") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "SkuID不能为空"
    }
}
