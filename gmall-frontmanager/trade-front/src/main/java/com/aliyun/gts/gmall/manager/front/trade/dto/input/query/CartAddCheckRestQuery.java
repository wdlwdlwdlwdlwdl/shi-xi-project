package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 购物车按钮展示检查
 *
 * @author tiansong
 */
@ApiModel("购物车按钮展示检查")
@Data
public class CartAddCheckRestQuery extends LoginRestQuery {
    @ApiModelProperty("商品ID")
    private Long itemId;
    @ApiModelProperty("SKU ID")
    private Long skuId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
        ParamUtil.nonNull(skuId, "[SKU ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
    }
}
