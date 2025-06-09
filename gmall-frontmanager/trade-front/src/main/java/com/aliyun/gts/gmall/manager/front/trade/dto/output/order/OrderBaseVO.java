package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单基础信息
 *
 * @author tiansong
 */
@Data
@ApiModel("订单基础信息")
public class OrderBaseVO<T extends OrderExtendVO> {

    /**
     * 订单基础字段
     */
    @ApiModelProperty("订单id")
    Long orderId;

    @ApiModelProperty("对外展示display_order_id")
    String displayOrderId;

    @ApiModelProperty("订单状态")
    Integer orderStatus;

    @ApiModelProperty("订单创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date  createTime;

    @ApiModelProperty("售后状态")
    Integer reversalStatus;

    //新增的字段
    @ApiModelProperty("商品分类id")
    String categoryId;

    @ApiModelProperty("订单状态名称")
    String orderStatusName;

    @ApiModelProperty("订单扩展信息")
    T extend;

    @ApiModelProperty("订单阶段(售前/售中/售后) OrderStageENum")
    Integer orderStage;

    @ApiModelProperty("订单的版本号")
    Long version;

    @ApiModelProperty("自定义扩展内容 (orderAttr.extra)")
    Map<String, String> extras;

    @ApiModelProperty("订单自定义标, 进搜索 (orderAttr.tags)")
    List<String> tags;

    public String getReversalStatusName() {
        ReversalStatusEnum reversalStatusEnum = ReversalStatusEnum.codeOf(this.reversalStatus);
        return reversalStatusEnum == null ? null : reversalStatusEnum.getName();
    }

    /**
     * 订单价格信息
     * 1。orderTotalAmt = orderRealAmt + pointAmt
     * 2。orderTotalAmt = promotionAmt + freightAmt
     * 3。商品优惠 = orderSaleAmt - promotionAmt
     */
    @NotNull
    @Min(value = 1L)
    @ApiModelProperty("金本位总费用 的订单分摊金额, orderRealAmt + pointAmt")
    Long totalAmt;

    @ApiModelProperty("实付现金 的订单分摊金额, 含运费, 扣除积分抵扣 分")
    Long realAmt;

    @ApiModelProperty("积分抵扣金额 点")
    Long pointAmt;

    @ApiModelProperty("积分(原子个数)")
    Long pointCount;

    @ApiModelProperty("运费 分")
    Long freightAmt;

    @ApiModelProperty("营销返回的最终价格")
    Long promotionAmt;

    @ApiModelProperty("订单商品总价 分")
    Long saleAmt;

    // ============= 改价 =============

    @ApiModelProperty(value = "orderPromotionAmt 金额改动, 正改大、负改小")
    Long adjustPromotionAmt;

    @ApiModelProperty(value = "freightAmt 金额改动, 正改大、负改小")
    Long adjustFreightAmt;

    @ApiModelProperty(value = "pointAmt 金额改动, 正改大、负改小")
    Long adjustPointAmt;

    @ApiModelProperty(value = "orderRealAmt 金额改动, 正改大、负改小")
    Long adjustRealAmt;

    @ApiModelProperty(value = "pointCount 数量改动, 正改大、负改小")
    Long adjustPointCount;

    public String getOrderRealAmt() {
        return String.valueOf(this.realAmt);
    }

    public String getOrderSaleAmt() {
        return String.valueOf(this.saleAmt);
    }

    public String getOrderTotalAmt() {
        return String.valueOf(this.totalAmt);
    }

    public String getFreightAmt() {
        return String.valueOf(this.freightAmt);
    }

    public String getOrderPromotionAmt() {
        return String.valueOf(this.promotionAmt);
    }

    public String getPointAmt() {
        return String.valueOf(this.pointAmt);
    }

    public String getPointCount() {
        return ItemUtils.pointDisplay(this.pointCount);
    }

    public String getOrderReduceAmt() {
        if (this.saleAmt == null || this.promotionAmt == null) {
            return null;
        }
        return String.valueOf(this.saleAmt - this.promotionAmt);
    }
}
