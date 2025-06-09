package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "删除购物车入参")
public class DeleteCartRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "需要删除的商品列表，至少包含一个商品", required = true)
    private List<ItemSkuId> itemSkuIds;

    @ApiModelProperty(value = "购物车业务类型", required = false)
    private Integer cartType;

//    @ApiModelProperty("购物车卡片ID集合")
//    private List<Long> cartIds;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member") + " [ID] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.notEmpty(itemSkuIds, I18NMessageUtils.getMessage("product.list.empty"));  //# "商品列表不能为空"
        for (ItemSkuId sku : itemSkuIds) {
            sku.checkInput();
        }
    }
}
