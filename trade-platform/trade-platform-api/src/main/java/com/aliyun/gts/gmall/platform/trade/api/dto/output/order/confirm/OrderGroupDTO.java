package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "商品分组信息")
public class OrderGroupDTO extends AbstractOutputInfo {

    @ApiModelProperty("主订单ID")
    private Long primaryOrderId;

    @ApiModelProperty(value = "卖家ID")
    private Long sellerId;

    @ApiModelProperty(value = "商品信息")
    private List<OrderItemDTO> orderItems;

    @ApiModelProperty("优惠选项")
    private List<PromotionOptionDTO> promotionOptions;

    @ApiModelProperty("优惠扩展信息")
    private PromotionExtendDTO promotionExtend;

    @ApiModelProperty("业务身份")
    private List<String> bizCodes;

    @ApiModelProperty("订单类型, OrderTypeEnum")
    private Integer orderType;

    @ApiModelProperty("多阶段订单")
    private List<ConfirmStepOrderDTO> stepOrders;

    @ApiModelProperty("阶段上下文")
    private Map<String, String> stepContextProps;

    @ApiModelProperty("收货信息")
    private ReceiverDTO receiver;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;

    // ===== 费用字段 =====

    @ApiModelProperty(value = "运费")
    private Long freight;

    @ApiModelProperty(value = "= realAmt + 积分抵扣金额")
    private Long totalAmt;

    @ApiModelProperty(value = "实付分摊金额, 含运费, 扣除优惠 , 扣除积分抵扣")
    private Long realAmt;

    @ApiModelProperty(value = "商品实付金额, 不含运费, 扣除优惠 , 扣除积分抵扣")
    private Long realItemAmt;

    @ApiModelProperty(value = "IC原价的总金额")
    private Long itemOriginAmt;

    @ApiModelProperty("运费金额佣金")
    private Long deliveryMerchantFee;


}
