package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayChannelVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderCheckPayModeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单确认页
 *
 * @author tiansong
 */
@Data
@ApiModel("订单确认页")
public class OrderConfirmVO {
    @ApiModelProperty("会员ID")
    private Long  custId;
    @ApiModelProperty("商品分组信息, 按卖家拆分")
    private List<OrderGroupVO>  orderGroups;
    @ApiModelProperty("默认支付渠道")
    private String payChannelSelected;
    @ApiModelProperty("支付渠道")
    private List<PayChannelVO> payChannelList;
    @ApiModelProperty("订单确认信息, 用于下单接口回传")
    private String confirmOrderToken;
    @ApiModelProperty("优惠券列表")
    private List<PromotionOptionVO> couponList;
    @ApiModelProperty("活动列表")
    private List<PromotionOptionVO> campaignList;
    @ApiModelProperty("下单实付金额, 含运费, 扣除了积分金额")
    private Long realAmt;
    @ApiModelProperty("下单商品实付金额, 不含运费")
    private Long realItemAmt;
    @ApiModelProperty("金本位金额 ( realAmt + 积分抵扣金额 )")
    private Long totalAmt;
    @ApiModelProperty("积分抵扣金额")
    private Long pointAmt;
    @ApiModelProperty("运费")
    private Long freight;
    @ApiModelProperty("最大可用积分")
    private Long maxAvailablePoint;
    @ApiModelProperty("使用积分抵扣的数量")
    private Long usePointCount;
    @ApiModelProperty("商品原价的总金额")
    private Long itemOriginAmt;
    @ApiModelProperty("商品一口价总金额")
    private Long saleAmt;
    @ApiModelProperty("总订单合并分期信息  install")
    private List<OrderCheckPayModeDTO> installPriceList;
    @ApiModelProperty("总订单合并分期信息  loan")
    private List<OrderCheckPayModeDTO> loanPriceList;
    @ApiModelProperty("是否可以下单")
    private Boolean confirmSuccess;
    @ApiModelProperty("订单类型 1 普通实物订单 2 多阶段订单 ")
    private Integer orderType;
    @ApiModelProperty(value = "折扣汇总金额")
    private Long itemDisCountAmt;

    @ApiModelProperty("没有城市价格的商品")
    private List<ItemSkuId> noCityPriceSkuIds;

    @ApiModelProperty("PVZ物流方式引导")
    private Boolean pvzPick;

    @ApiModelProperty("postamat物流方式引导")
    private Boolean postamatPick;

    @ApiModelProperty("超出价格类型0，未超出，1分期超出 2 贷款超出")
    private Integer overPriceLimit = 0;

    @ApiModelProperty("错误提示语")
    private String errMsg;


    public String getRealAmtYuan() {
        return String.valueOf(this.getRealAmt());
    }

    public String getTotalAmtYuan() {
        return String.valueOf(this.getTotalAmt());
    }

    public String getPointAmtYuan() { return String.valueOf(this.getPointAmt()); }

    public String getFreightYuan() {
        return ItemUtils.fen2Yuan(this.getFreight());
    }

    public String getSaleAmtYuan() {return ItemUtils.fen2Yuan(this.getSaleAmt());}

    public String getItemOriginAmtYuan() {return ItemUtils.fen2Yuan(this.getItemOriginAmt());}

    public String getReduceAmtYuan() {
        // 优惠金额 = 商品总价（saleAmt） + 运费 - 订单总金额（=商品优惠总价+运费）
        return ItemUtils.fen2Yuan(this.saleAmt + freight - totalAmt);
    }

    public Long getReduceAmt() {
        // 优惠金额 = 商品总价（saleAmt） + 运费 - 订单总金额（=商品优惠总价+运费）
        return (this.saleAmt + freight - totalAmt);
    }

    public String getUsePointCountUnit() {
        return ItemUtils.pointDisplay(usePointCount);
    }

    public String getMaxAvailablePointUnit() {
        return ItemUtils.pointDisplay(maxAvailablePoint);
    }
}
