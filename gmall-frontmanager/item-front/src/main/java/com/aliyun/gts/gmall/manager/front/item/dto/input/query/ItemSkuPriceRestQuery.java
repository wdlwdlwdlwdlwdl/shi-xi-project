package com.aliyun.gts.gmall.manager.front.item.dto.input.query;


import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ItemSkuPriceRestQuery extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;
    @ApiModelProperty(value = "skuId", required = true)
    private Long skuId;
    @ApiModelProperty(value = "quoteId", required = true)
    private String quoteId;
    @ApiModelProperty(value = "城市code", required = true)
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("sku")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "SkuID不能为空"
        ParamUtil.nonNull(cityCode, I18NMessageUtils.getMessage("sku")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "城市code不能为空"
        ParamUtil.expectTrue(skuId > 0L, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确"

    }
}
