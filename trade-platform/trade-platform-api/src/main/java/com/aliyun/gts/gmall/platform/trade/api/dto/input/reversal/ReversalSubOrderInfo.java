package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReversalSubOrderInfo extends AbstractInputParam {

    @ApiModelProperty(value = "子订单ID", required = true)
    private Long orderId;

    @ApiModelProperty(value = "申请售后的商品数量")
    private Integer cancelQty;

    @ApiModelProperty(value = "申请退款金额, 不指定默认分摊")
    private Long cancelAmt;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(orderId, I18NMessageUtils.getMessage("sub.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "子订单ID不能为空"
//        if (cancelQty != null) {
//            ParamUtil.expectInRange(cancelQty, 1, Integer.MAX_VALUE, "商品数量不能小于1");
//        }
        if (cancelAmt != null) {
            ParamUtil.expectInRange(cancelAmt, 0L, Long.MAX_VALUE, I18NMessageUtils.getMessage("amount.not.less")+" 0");  //# "金额不能小于
        }
    }
}
