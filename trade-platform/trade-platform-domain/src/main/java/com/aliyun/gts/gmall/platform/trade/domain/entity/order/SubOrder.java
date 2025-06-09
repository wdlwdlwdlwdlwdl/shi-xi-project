package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.common.searchconvertor.JsonHandler;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderItemFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.SalesInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemDelivery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 子订单
 */
@Data
public class SubOrder extends AbstractBusinessEntity {

    @ApiModelProperty("主订单ID")
    @SearchMapping("primary_order_id")
    private Long primaryOrderId;

    @ApiModelProperty("子订单ID")
    @SearchMapping("order_id")
    private Long orderId;

    @ApiModelProperty("卖家id")
    @SearchMapping("seller_id")
    private Long sellerId;

    @ApiModelProperty("购物车ID, 支付标识符")
    private Long cartId;

    //新增的字段
    @ApiModelProperty("商品分类id")
    private String categoryId;

    @ApiModelProperty("子订单状态")
    @SearchMapping(value ="order_status")
    private Integer orderStatus;

    @ApiModelProperty("商品SKU信息")
    @SearchMapping(mapChild = true)
    private ItemSku itemSku;

    @ApiModelProperty("下单数量")
    @SearchMapping(value ="item_quantity")
    private Integer orderQty;

    @ApiModelProperty("创建时间")
    @SearchMapping("gmt_create")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("是否评价, OrderEvaluateEnum")
    @SearchMapping("evaluate")
    private Integer evaluate;

    @ApiModelProperty("子订单价格信息")
    @SearchMapping(value = "order_fee_attr",handler = JsonHandler.class)
    private OrderPrice orderPrice;

    @ApiModelProperty("商品优惠信息")
    private ItemPromotion promotions;

    @ApiModelProperty("最新的售后状态")
    private Integer reversalStatus;

    @ApiModelProperty("订单扩展")
    @SearchMapping(value="order_attr" , handler = JsonHandler.class)
    private OrderAttrDO orderAttr;

    @ApiModelProperty("订单附属扩展表信息")
    private List<TcOrderExtendDO> orderExtendList= Lists.newArrayList();

    @ApiModelProperty("版本号")
    private Long version;

    @ApiModelProperty("收货信息")
    @SearchMapping(value = "receive_info", handler = JsonHandler.class)
    private ReceiveAddr receiver;

    @ApiModelProperty("发货信息")
    @SearchMapping(value = "sales_info", handler = JsonHandler.class)
    private SalesAddr sales;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<ItemDelivery> itemDelivery;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("firstName")
    private String firstName;

    @ApiModelProperty("lastName")
    private String lastName;

    @ApiModelProperty("bin_or_iin")
    private String binOrIin;

    @ApiModelProperty("贷款周期")
    private Integer loanCycle;

    @ApiModelProperty("商品扩展字段")
    private OrderItemFeatureDO itemFeature;

    @ApiModelProperty("发货信息")
    private SalesInfoDO salesInfo;

    @ApiModelProperty("发货物流的仓库地址")
    private List<SkuQuoteWarehourseStockDTO> warehourseStockList;

    @ApiModelProperty("运费金额")
    private Long freightAmount;

    @ApiModelProperty("对外展示display_order_id")
    @SearchMapping("display_order_id")
    private String displayOrderId;

    @ApiModelProperty("支付凭证cartId")
    private String payCartId;

    //  以下字段废弃
    @ApiModelProperty("物流类型")
    private String logisticsType;

    @ApiModelProperty(value = "佣金费率")
    private Integer categoryCommissionRate;

    @ApiModelProperty("运费金额佣金")
    private Long deliveryMerchantFee = 0L;

    public OrderAttrDO orderAttr() {
        if (orderAttr == null) {
            orderAttr = new OrderAttrDO();
        }
        return orderAttr;
    }

    /**
     * 判断是否存在这个标
     * @param tag
     * @return
     */
    public boolean containsTag(String tag){
        int index = orderAttr().tags().indexOf(tag);
        return index >= 0;
    }
}
