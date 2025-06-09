package com.aliyun.gts.gmall.manager.front.item.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Sku模块请求
 *
 * @author tiansong
 */
@ApiModel(description = "Sku模块请求")
@Data
public class ItemSkuRestQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;
    @ApiModelProperty("已选择SKU ID")
    private Long skuId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
        ParamUtil.expectTrue(itemId > 0L, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确"
    }
}
