
package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.common.searchconvertor.JsonHandler;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 主订单
 */
@Data
public class MainOrder extends AbstractBusinessEntity {

    @ApiModelProperty("主订单ID")
    @SearchMapping("primary_order_id")
    private Long primaryOrderId;

    @ApiModelProperty("业务身份")
    private List<String> bizCodes;

    @ApiModelProperty("主订单状态")
    @SearchMapping(value = "primary_order_status")
    private Integer primaryOrderStatus;

    @ApiModelProperty("订单快照，oss地址")
    private String snapshotPath;

    @ApiModelProperty("下单渠道")
    @SearchMapping(value = "order_channel")
    private String orderChannel;

    @ApiModelProperty("是否评价, OrderEvaluateEnum")
    @SearchMapping("evaluate")
    private Integer evaluate;

    @ApiModelProperty("用户下单备注")
    private String custMemo;

    @ApiModelProperty("类目")
    private String categoryName;

    @ApiModelProperty("订单价格信息")
    @SearchMapping(value = "order_fee_attr", handler = JsonHandler.class)
    private OrderPrice orderPrice;

    @ApiModelProperty("创建时间")
    @SearchMapping("gmt_create")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("订单类型 1 普通实物订单 2 多阶段订单 ")
    private Integer orderType;

    @ApiModelProperty("收货信息")
    @SearchMapping(value = "receive_info", handler = JsonHandler.class)
    private ReceiveAddr receiver;

    @ApiModelProperty("发货信息")
    @SearchMapping(value = "sales_info", handler = JsonHandler.class)
    private SalesAddr sales;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;

    @ApiModelProperty("卖家信息")
    @SearchMapping(mapChild = true)
    private Seller seller;

    @ApiModelProperty("买家信息")
    @SearchMapping(mapChild = true)
    private Customer customer;

    @ApiModelProperty("订单优惠信息")
    private SellerPromotion promotions;

    @ApiModelProperty("子订单列表")
    private List<SubOrder> subOrders = new ArrayList<>();

    @ApiModelProperty("订单扩展")
    @SearchMapping(value = "order_attr", handler = JsonHandler.class)
    private OrderAttrDO orderAttr;

    @ApiModelProperty("订单附属扩展表信息")
    private List<TcOrderExtendDO> orderExtendList = Lists.newArrayList();

    @ApiModelProperty("版本号")
    private Long version;

    @ApiModelProperty("多阶段订单")
    private List<StepOrder> stepOrders;

    @ApiModelProperty("多阶段模版")
    private StepTemplate stepTemplate;

    @ApiModelProperty("贷款周期")
    private Integer loanCycle;

    @ApiModelProperty("贷款状态")
    private Integer loanStatus;

    @ApiModelProperty("买家名字")
    private String firstName;

    @ApiModelProperty("买家姓氏")
    private String lastName;

    @ApiModelProperty("运费金额")
    private Long freightAmount;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty("发货物流的仓库地址")
    private List<SkuQuoteWarehourseStockDTO> warehourseStockList;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("对外展示display_order_id")
    @SearchMapping("display_order_id")
    private String displayOrderId;

    // 以下字段不用
    @ApiModelProperty("物流类型")
    private String logisticsType;

    @ApiModelProperty("减库存方式, 枚举:InventoryReduceType")
    private Integer inventoryReduceType;

    @ApiModelProperty("购物车ID, 用来删除使用")
    private Long cartId;

    @ApiModelProperty("支付凭证cartId")
    private String payCartId;

    @ApiModelProperty("运费金额佣金")
    private Long deliveryMerchantFee = 0L;

    @Override
    public int hashCode() {
        return this.primaryOrderId.hashCode();
    }

    @Override
    public boolean equals(Object mainOrder) {
        if (mainOrder == null || !(mainOrder instanceof MainOrder)) {
            return false;
        }
        return this.primaryOrderId.equals(((MainOrder) mainOrder).primaryOrderId);
    }

    public OrderAttrDO orderAttr() {
        if (orderAttr == null) {
            orderAttr = new OrderAttrDO();
        }
        return orderAttr;
    }

    /**
     * 取当前支付信息
     */
    @JSONField(serialize = false)
    public OrderPayInfo getCurrentPayInfo() {
        return getPayInfo(orderAttr.getCurrentStepNo());
    }

    /**
     * 取支付信息
     */
    @JSONField(serialize = false)
    public OrderPayInfo getPayInfo(Integer stepNo) {
        if (StepOrderUtils.isMultiStep(this)) {
            StepOrder step = StepOrderUtils.getStepOrder(this, stepNo);
            return step == null ? null : OrderPayInfo.builder()
                    .payChannel(step.features().getPayChannel())
                    .payPrice(step.getPrice())
                    .build();
        } else {
            return OrderPayInfo.builder()
                    .payChannel(orderAttr().getPayChannel())
                    .payPrice(orderPrice)
                    .build();
        }
    }

    /**
     * 取当前阶段单, 普通订单返回null
     */
    @JSONField(serialize = false)
    public StepOrder getCurrentStepOrder() {
        return StepOrderUtils.getStepOrder(this, orderAttr().getCurrentStepNo());
    }

    /**
     * 子订单 及 运费服务单的金额, 汇总后等于 主单 orderPrice
     */
    @JSONField(serialize = false)
    public List<SubPrice> getAllSubPrice() {
        List<SubPrice> list = new ArrayList<>();
        for (SubOrder sub : subOrders) {
            list.add(SubPrice.subOrder(sub));
        }
        SubPrice freight = SubPrice.freight(this);
        list.add(freight);
        return list;
    }
}
