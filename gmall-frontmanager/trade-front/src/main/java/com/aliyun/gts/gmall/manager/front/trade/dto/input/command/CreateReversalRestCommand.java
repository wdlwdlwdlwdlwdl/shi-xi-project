package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.ReversalSubOrder;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 创建售后申请单
 *
 * @author tiansong
 */
@Data
@ApiModel("创建售后申请单")
public class CreateReversalRestCommand extends LoginRestCommand {
    @ApiModelProperty(value = "售后服务类型（参见ReversalTypeEnum）", required = true)
    private Integer                reversalType;
    @ApiModelProperty(value = "主订单编号", required = true)
    private Long                   primaryOrderId;
    @ApiModelProperty(value = "子订单信息", required = true)
    private List<ReversalSubOrder> subOrders;
    @ApiModelProperty(value = "退款总金额(元)", required = true)
    private String                 cancelAmtYuan;
    @ApiModelProperty(value = "退款总金额(分)")
    private Long                   cancelAmt;
    @ApiModelProperty(value = "退款原因code", required = true)
    private Integer                reversalReasonCode;
    @ApiModelProperty(value = "是否收到货物")
    private Boolean                itemReceived;
    @ApiModelProperty("申请售后描述")
    private String                 custMemo;
    @ApiModelProperty("申请售后举证图片、视频等")
    private List<String>           custMedias;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(this.reversalType, I18NMessageUtils.getMessage("after.sales.service.type.required"));  //# "售后服务类型不能为空"
        ParamUtil.nonNull(ReversalTypeEnum.codeOf(this.reversalType), I18NMessageUtils.getMessage("after.sales.service.type.unsupported"));  //# "售后服务类型不支持"
        ParamUtil.nonNull(this.primaryOrderId, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
        ParamUtil.notEmpty(this.subOrders, I18NMessageUtils.getMessage("sub.order.info.required"));  //# "子订单信息不能为空"
        //ParamUtil.expectTrue(this.subOrders.size() == 1, I18NMessageUtils.getMessage("single.sub.order.after.sales"));  //# "必须单个子订单申请售后"
        subOrders.forEach(subOrder -> {
            subOrder.checkInput();
            if (ReversalTypeEnum.REFUND_ITEM.getCode().equals(reversalType)) {
                // 退货退款，强制校验退货数量
                subOrder.checkCancelQty();
            }
        });
        ParamUtil.nonNull(this.cancelAmtYuan, I18NMessageUtils.getMessage("refund.total.amount.required"));  //# "退款总金额不能为空"
        //this.cancelAmt = ItemUtils.yuan2Fen(cancelAmtYuan);
        this.cancelAmt = Long.parseLong(cancelAmtYuan);
        ParamUtil.expectInRange(this.cancelAmt, 1L, Long.MAX_VALUE, I18NMessageUtils.getMessage("refund.total.amount.not.less")+"1分");  //# "退款总金额不能小于
        ParamUtil.nonNull(this.reversalReasonCode, I18NMessageUtils.getMessage("refund.reason")+"code"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "退款原因code不能为空"
        if (this.itemReceived == null) {
            // 按照退款类型设置
            this.setItemReceived(ReversalTypeEnum.REFUND_ITEM.getCode().equals(this.reversalType));
        }
        if (CollectionUtils.isNotEmpty(custMedias)) {
            // 图片上传过程中提交，会出现特殊URL
            custMedias.forEach(v -> ParamUtil.expectFalse(StringUtils.isEmpty(v) || v.contains("null.null"), I18NMessageUtils.getMessage("proof.image.format.invalid")));  //# "举证图片地址格式不正确"
        }
    }
}