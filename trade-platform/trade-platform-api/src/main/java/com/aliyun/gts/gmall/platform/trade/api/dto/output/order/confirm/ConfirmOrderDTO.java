package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ItemSkuId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "订单确认返回对象")
public class ConfirmOrderDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "会员ID")
    private Long custId;

    @ApiModelProperty(value = "收件人信息")
    private ReceiverDTO receiver;

    @ApiModelProperty(value = "商品分组信息, 按卖家拆分")
    private List<OrderGroupDTO> orderGroups;

    @ApiModelProperty(value = "支付方式")
    private List<PayTypeDTO> payTypes;

    @ApiModelProperty(value = "订单确认信息, 用于下单接口回传")
    private String confirmOrderToken;

    @ApiModelProperty("优惠选项")
    private List<PromotionOptionDTO> promotionOptions;

    @ApiModelProperty("优惠扩展信息")
    private PromotionExtendDTO promotionExtend;

    @ApiModelProperty("非阻断式的错误信息, 如收货地址不在配送范围")
    private List<NonblockFailDTO> nonblockFails;

    @ApiModelProperty("多阶段扩展信息")
    private ConfirmStepExtendDTO stepExtend;

    // ===== 费用字段 =====

    @ApiModelProperty(value = "下单实付金额, 含运费, 扣除了积分金额")
    private Long realAmt;

    @ApiModelProperty("下单商品实付金额, 不含运费")
    private Long realItemAmt;

    @ApiModelProperty(value = "积分抵扣金额")
    private Long pointAmt;

    @ApiModelProperty(value = "等于 realAmt+积分抵扣金额, 也等于 最终营销价+运费")
    private Long totalAmt;

    @ApiModelProperty(value = "等于 商品金额总和")
    private Long itemAmt;

    @ApiModelProperty(value = "运费")
    private Long freight;

    @ApiModelProperty(value = "最大可用积分数量(原子积分)")
    private Long maxAvailablePoint;

    @ApiModelProperty(value = "使用积分抵扣的数量(原子积分)")
    private Long usePointCount;

    @ApiModelProperty(value = "IC原价的总金额")
    private Long itemOriginAmt;

    @ApiModelProperty("可用分期列表")
    private List<Integer> installment;

    @ApiModelProperty("可用借贷列表")
    private List<Integer> loan;

    @ApiModelProperty("购物车id")
    private Long cartId;

    @ApiModelProperty("总订单合并分期信息")
    private List<LoanPeriodDTO> sumPriceList;

    @ApiModelProperty("总订单合并贷款信息")
    private List<LoanPeriodDTO> sumLoanPriceList;

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

    @ApiModelProperty("运费金额佣金")
    private Long deliveryMerchantFee;

    @ApiModelProperty("PVZ物流方式引导")
    private Boolean pvzPick;

    @ApiModelProperty("postamat物流方式引导")
    private Boolean postamatPick;

    @ApiModelProperty("超出价格类型0，未超出，1分期超出 2 贷款超出")
    private Integer overPriceLimit = 0;

    @ApiModelProperty("错误提示语")
    private String errMsg;

}
