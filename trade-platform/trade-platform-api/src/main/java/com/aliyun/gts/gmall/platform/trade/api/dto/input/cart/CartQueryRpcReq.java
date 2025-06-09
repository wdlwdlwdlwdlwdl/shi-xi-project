package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "购物车查看入参")
public class CartQueryRpcReq extends AbstractPageQueryRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String channel;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("支付方式")
    private String payMode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
    }
}
