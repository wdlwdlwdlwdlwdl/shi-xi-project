package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.OrderItemVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 订单商品分组
 *
 * @author tiansong
 */
@Data
@ApiModel("订单商品分组")
public class OrderGroupVO {
    @ApiModelProperty("主订单ID")
    private Long primaryOrderId;
    @ApiModelProperty("订单扩展信息 包括收货人信息  电子凭证信息等")
    private OrderExtendVO extend;
    boolean success = true;
    @ApiModelProperty("订单确认错误信息（收货地址不支持）")
    private String errorMsg;
    @ApiModelProperty("卖家ID")
    private Long sellerId;
    @ApiModelProperty("商品信息")
    private List<OrderItemVO>  orderItems;
    @ApiModelProperty("优惠券列表")
    private List<PromotionOptionVO> couponList;
    @ApiModelProperty("活动列表")
    private List<PromotionOptionVO> campaignList;
    @ApiModelProperty("运费")
    private Long freight;
    @ApiModelProperty("金本位分摊金额 ( realAmt + 积分抵扣金额 )")
    private Long totalAmt;
    @ApiModelProperty("实付分摊金额, 含运费, 扣除优惠 , 扣除积分抵扣")
    private Long realAmt;
    @ApiModelProperty("商品一口价总金额")
    private Long saleAmt;
    @ApiModelProperty("多阶段订单")
    private List<StepOrderVO> stepOrders;
    @ApiModelProperty("收货信息")
    private ReceiverDTO receiver;
    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;
    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;

    public String getSellerName() {
        return CollectionUtils.isEmpty(this.getOrderItems()) ? null : this.getOrderItems().get(0).getSellerName();
    }

    public String getFreightYuan() {
        return String.valueOf(this.getFreight());
    }

    public String getTotalAmtYuan() {
        return String.valueOf(this.getTotalAmt());
    }

    public String getRealAmtYuan() {
        return String.valueOf(this.getRealAmt());
    }

    public String getSaleAmtYuan() {return String.valueOf(this.getSaleAmt());}
}
