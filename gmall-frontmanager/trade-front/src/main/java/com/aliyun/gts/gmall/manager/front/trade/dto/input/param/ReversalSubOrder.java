package com.aliyun.gts.gmall.manager.front.trade.dto.input.param;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.CombineItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 子订单售后
 *
 * @author tiansong
 */
@Data
@ApiModel("子订单售后")
public class ReversalSubOrder extends AbstractInputParam {
    @ApiModelProperty(value = "子订单ID", required = true)
    private Long    orderId;
    @ApiModelProperty(value = "申请售后的商品数量")
    private Integer cancelQty;
    @ApiModelProperty(value = "申请退款金额(分)")
    private Long    cancelAmt;
    @ApiModelProperty(value = "申请退款金额(元)")
    private String  cancelAmtYuan;
    @ApiModelProperty(value = "组合商品")
    private List<CombineItemVO> combineItems;
    @ApiModelProperty(value = "最大运费金额(分), 回传回来自动拆分金额")
    private Long    maxFreightAmt;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(orderId, I18NMessageUtils.getMessage("sub.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "子订单ID不能为空"
        ParamUtil.nonNull(cancelAmtYuan, I18NMessageUtils.getMessage("refund.amount.required"));  //# "申请退款金额不能为空"
        this.cancelAmt = cancelAmt;
        ParamUtil.expectInRange(this.cancelAmt, 1L, Long.MAX_VALUE, I18NMessageUtils.getMessage("refund.amount.min"));
    }

    /**
     * 退货退款类型，需要检验退货数量
     */
    public void checkCancelQty() {
        //组合商品
        if(CollectionUtils.isEmpty(combineItems)) {
            ParamUtil.nonNull(cancelQty, I18NMessageUtils.getMessage("return.qty.required"));  //# "退货数量必填"
            ParamUtil.expectTrue(cancelQty > 0, I18NMessageUtils.getMessage("return.min.qty"));  //# "退货数量至少一件"
        }
        //组合商品校验
        else{
            for(CombineItemVO vo : combineItems){
                ParamUtil.nonNull(vo.getCancelQty(), I18NMessageUtils.getMessage("return.qty.required"));  //# "退货数量必填"
                ParamUtil.expectTrue(vo.getCancelQty() > 0, I18NMessageUtils.getMessage("return.min.qty"));  //# "退货数量至少一件"
            }
        }
    }
}
