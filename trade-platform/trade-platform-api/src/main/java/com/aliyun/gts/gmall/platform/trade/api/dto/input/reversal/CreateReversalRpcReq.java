package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "创建售后单入参")
public class CreateReversalRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "客户编号", required = true)
    private Long custId;

    @ApiModelProperty(value = "售后申请渠道（参见OrderChannelEnum）", required = true)
    private String reversalChannel;

    @ApiModelProperty(value = "售后服务类型（参见ReversalTypeEnum）", required = true)
    private Integer reversalType;

    @ApiModelProperty(value = "主订单编号", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "子订单信息", required = true)
    private List<ReversalSubOrderInfo> subOrders;

    @ApiModelProperty(value = "退款总金额, 不传默认全退")
    private Long cancelAmt;

    @ApiModelProperty(value = "退运费金额, 不传默认退最大可退")
    private Long cancelFreightAmt;

    @ApiModelProperty(value = "退款原因code", required = true)
    private Integer reversalReasonCode;

    @ApiModelProperty(value = "是否收到货物", required = true)
    private Boolean itemReceived;

    @ApiModelProperty(value = "申请售后描述")
    private String custMemo;

    @ApiModelProperty(value = "申请售后举证图片、视频等")
    private List<String> custMedias;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("customer.id.empty"));  //# "客户编号不能为空"
        ParamUtil.nonNull(reversalChannel, I18NMessageUtils.getMessage("aftersales.channel.empty"));  //# "售后申请渠道不能为空"
        ParamUtil.nonNull(reversalType, I18NMessageUtils.getMessage("aftersale.type.empty"));  //# "售后服务类型不能为空"
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order.num.empty"));  //# "主订单编号不能为空"
        ParamUtil.notEmpty(subOrders, I18NMessageUtils.getMessage("sub.order.empty"));  //# "子订单信息不能为空"
        for (ReversalSubOrderInfo sub : subOrders) {
            sub.checkInput();
        }
        ParamUtil.expectTrue(cancelAmt == null || cancelAmt.longValue() > 0, I18NMessageUtils.getMessage("refund.total.less")+" 1 "+I18NMessageUtils.getMessage("cents"));  //# "退款总金额不能小于1分钱"
        ParamUtil.nonNull(reversalReasonCode, I18NMessageUtils.getMessage("refund.reason")+" [code] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "退款原因code不能为空"
        ParamUtil.nonNull(itemReceived, I18NMessageUtils.getMessage("goods.received.empty"));  //# "是否收到货物不能为空"
    }
}
