package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "购物车费用计算入参")
public class CalCartPriceRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "需要计算的的商品列表，至少包含一个商品", required = true)
    private List<ItemSkuQty> itemSkuIds;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String channel;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.notEmpty(itemSkuIds, I18NMessageUtils.getMessage("product.list.empty"));  //# "商品列表不能为空"
        for (ItemSkuQty sku : itemSkuIds) {
            sku.checkInput();
        }
    }
}
