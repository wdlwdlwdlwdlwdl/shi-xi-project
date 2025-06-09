package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.SeparateRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

@Data
public class OrderAttrDO extends ExtendComponent {

    @ApiModelProperty("减库存方式, 枚举:InventoryReduceType")
    private Integer inventoryReduceType;
    @ApiModelProperty("确认收货时间")
    private Date confirmReceiveTime;
    @ApiModelProperty("发货时间")
    private Date sendTime;
    @ApiModelProperty("评价时间")
    private Date evaluateTime;
    @ApiModelProperty("售后开始时间")
    private Date reversalStartTime;
    @ApiModelProperty("售后结束时间")
    private Date reversalEndTime;
    @ApiModelProperty("自提超时时间")
    private Date pickUpDeadTime;
    @ApiModelProperty("物流方式, LogisticsTypeEnum")
    private Integer logisticsType;
    @ApiModelProperty("卖家备注")
    private String sellerMemo;
    @ApiModelProperty("订单类型, OrderTypeEnum")
    private Integer orderType;
    @ApiModelProperty("订单售前/售中/售后阶段, OrderStageEnum")
    private Integer orderStage;
    @ApiModelProperty("发货物流单号")
    private List<String> logisticsNos;
    @ApiModelProperty("支付渠道, PayChannelEnum(支付宝、微信)")
    private String payChannel;
    @ApiModelProperty("是否为超卖")
    private Boolean overSale;
    @ApiModelProperty("售后状态的订单, 记录发起售后前的订单状态")
    private Integer reversalOrderStatus;
    @ApiModelProperty("积分抵扣汇率")
    private PointExchange pointExchange;
    @ApiModelProperty("分账规则")
    private SeparateRule separateRule;
    @ApiModelProperty("合并下单的主订单ID列表, 非合并下单该字段为null")
    private List<Long> mergeOrderIds;
    @ApiModelProperty("订单自定义标, 进搜索")
    private List<String> tags;
    @ApiModelProperty("多阶段模版")
    private String stepTemplateName;
    @ApiModelProperty("多阶段参数")
    private Map<String, String> stepContextProps;
    @ApiModelProperty("当前阶段号")
    private Integer currentStepNo;
    @ApiModelProperty("当前阶段状态")
    private Integer currentStepStatus;
    @ApiModelProperty("账期支付备注")
    private String accountPeriodMemo;
    @ApiModelProperty("卖家确认接单原因 SellerConfirmReason")
    private String scr;
    private String reasonCode;
    @ApiModelProperty("运费服务单的金额, 主订单上")
    private OrderPrice freightPrice = new OrderPrice();
    private String remark;//虚拟订单卡密相关
    @ApiModelProperty(value = "支付方式")
    private String payType;
    @ApiModelProperty(value = "支付模式")
    private String payMode;
    @ApiModelProperty(value = "是否支持退款")
    private Integer canRefunds;
    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long weight;
    @ApiModelProperty(value = "商品类型, IC字段")
    private Integer itemType;
    @ApiModelProperty("银行卡号")
    private String bankCardNbr;
    //添加这个字段
    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("物流费是否退")
    private Boolean freightRefunded = false;

    @ApiModelProperty("卖家是否ka")
    private boolean sellerKa;

    @ApiModelProperty(value = "Merchant SKU Code商家sku码")
    private String merchantSkuCode;

    @ApiModelProperty("手机号码")
    private String sellerPhone;

    public Map<String, String> stepContextProps() {
        if (stepContextProps == null) {
            stepContextProps = new HashMap<>();
        }
        return stepContextProps;
    }

    public List<String> tags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }
}
