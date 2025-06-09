package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后申请请求
 *
 * @author tiansong
 */
@Data
@ApiModel("售后申请请求")
public class ReversalCheckRestQuery extends LoginRestQuery {
    @ApiModelProperty(value = "主订单编号", required = true)
    private Long    primaryOrderId;
    @ApiModelProperty("子订单编号")
    private Long    subOrderId;
    @ApiModelProperty(value = "售后类型：ReversalTypeEnum", required = true)
    private Integer reversalType;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
        ParamUtil.nonNull(subOrderId, I18NMessageUtils.getMessage("sub.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "子订单ID不能为空"
        ParamUtil.nonNull(reversalType, I18NMessageUtils.getMessage("after.sales.type.required"));  //# "售后类型不能为空"
        ParamUtil.nonNull(ReversalTypeEnum.codeOf(reversalType), I18NMessageUtils.getMessage("after.sales.type.unsupported")+"，"+I18NMessageUtils.getMessage("please.confirm"));  //# "售后类型不支持，请确认"
    }
}
