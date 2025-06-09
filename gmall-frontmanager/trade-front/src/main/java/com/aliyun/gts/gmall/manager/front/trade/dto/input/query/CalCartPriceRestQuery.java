package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.ItemSkuIdWithQty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 购物车价格计算
 *
 * @author tiansong
 */
@Data
@ApiModel("购物车价格计算")
public class CalCartPriceRestQuery extends LoginRestQuery {
    @ApiModelProperty(value = "需要计算的的商品列表，至少包含一个商品", required = true)
    private List<ItemSkuIdWithQty> itemSkuIds;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("城市code")
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(itemSkuIds, I18NMessageUtils.getMessage("product.required"));  //# "商品不能为空"
        ParamUtil.nonNull(cityCode, I18NMessageUtils.getMessage("city.code.required"));  //# "城市校验"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product") + " [payMode] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品Id不能为空"
        itemSkuIds.forEach(itemSkuId -> itemSkuId.checkInput());
    }
}