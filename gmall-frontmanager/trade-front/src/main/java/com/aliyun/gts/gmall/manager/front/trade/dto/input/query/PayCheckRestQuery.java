package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * 支付成功检查
 *
 * @author tiansong
 */
@ApiModel("支付成功检查")
@Data
public class PayCheckRestQuery extends LoginRestQuery {
    @ApiModelProperty(value = "主订单号", required = true)
    private Long primaryOrderId;
    @ApiModelProperty(value = "支付流水号", required = true)
    private String payFlowId;
    @ApiModelProperty(value = "统一购物车标识", required = true)
    private String cartId;
    @ApiModelProperty(value = "主订单列表（合并支付）, 透传 OrderCreateResultVO.primaryOrderList")
    private List<String> primaryOrderList;
    private String outFlowNo;

    @Override
    public void checkInput() {
        super.checkInput();
        //if (CollectionUtils.isEmpty(this.getPrimaryOrderList())) {
        //    ParamUtil.nonNull(this.primaryOrderList, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
        //}
        ParamUtil.nonNull(cartId, I18NMessageUtils.getMessage("payment.serial.number.required"));  //# "统一购物车标识不能为空"
    }
}
