package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 创建订单对象
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-13 17:59:20
 */
@Data
public class SplitOrderItemInfo {

    // 商品信息
    private ItemSku itemSku;

    /**
     * 来自订单确认和下单的属性信息
     *
     */
    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SkuId", required = true)
    private Long skuId;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer itemQty;

    @ApiModelProperty("确认订单时选择的商家")
    private Long sellerId;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty(value = "收件人信息")
    private ReceiverDTO receiver;

    @ApiModelProperty("贷款周期")
    private Integer loanCycle;

    @ApiModelProperty("购物车ID, 用来删除使用")
    private Long cartId;

    @ApiModelProperty("发货物流的仓库地址")
    private List<SkuQuoteWarehourseStockDTO> warehourseStockList;

}
