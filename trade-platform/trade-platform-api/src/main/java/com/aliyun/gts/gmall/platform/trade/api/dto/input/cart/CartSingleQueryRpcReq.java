package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "购物车单商品查询入参")
public class CartSingleQueryRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String channel;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "SKU ID", required = true)
    private Long skuId;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("支付方式")
    private String payMode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品ID不能为空"
        ParamUtil.nonNull(skuId, "[SKU ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# 不能为空"

        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("[sellerId] ") + I18NMessageUtils.getMessage("cannot.empty"));  //# 商家ID 不能为空"
        ParamUtil.nonNull(payMode, "[payMode] "+I18NMessageUtils.getMessage("cannot.empty"));  //# 不能为空"
    }
}
