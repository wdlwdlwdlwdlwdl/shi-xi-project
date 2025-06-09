package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.CombineItemVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 子订单信息
 *
 * @author tiansong
 */
@Data
@ApiModel("子订单信息")
public class OrderSubVO extends OrderBaseVO {

    @ApiModelProperty("主订单id")
    Long primaryOrderId;

    /**
     * 子订单价格信息
     * orderSaleAmt = itemPrice * itemQuantity
     */
    @ApiModelProperty("商品原价 分")
    Long itemOriginPrice;

    @ApiModelProperty("商品单价 分")
    Long itemPrice;

    public String getItemOriginPrice() {return String.valueOf(this.itemOriginPrice);}

    public String getItemPriceYuan() {
        return String.valueOf(this.itemPrice);
    }

    @ApiModelProperty("商品数量")
    Integer itemQuantity;

    @ApiModelProperty("商品名称")
    String  itemTitle;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("商品主图")
    String  itemPic;

    @ApiModelProperty("商品id")
    Long    itemId;

    @ApiModelProperty("付款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date    payTime;

    @ApiModelProperty("发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date    sendTime;

    @ApiModelProperty("确认收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date    receivedTime;

    @ApiModelProperty("评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date    evaluatedTime;

    @ApiModelProperty("付款方式")
    Integer payType;

    @ApiModelProperty("主售后单ID（售后进行中状态时填充）")
    Long    primaryReversalId;

    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long weight;

    public String getWeightUnit() {return ItemUtils.showWeight(this.weight, true);}

    @ApiModelProperty("sku描述")
    String  skuDesc;

    @ApiModelProperty("商品扩展")
    String  itemFeature;

    @ApiModelProperty("skuid")
    Long    skuId;

    @ApiModelProperty("是否展示申请售后按钮")
    Boolean showReversal;

    @ApiModelProperty("是否展示售后退款")
    Boolean showReversalRefund;

    @ApiModelProperty("是否展示售后退货")
    Boolean showReversalReturnItem;

    @ApiModelProperty("加购按钮")
    Boolean showAddCart;

    @ApiModelProperty("申请取消按钮")
    Boolean showApplyCancel;

    @ApiModelProperty("是否可以退")
    Integer canRefunds;

    @ApiModelProperty("页面展示的标签")
    List<String> showTags;

    @ApiModelProperty("组合商品")
    private List<CombineItemVO> combineItems;
    /**
     * 商品优惠分摊明细
     */
    private List<PromotionOptionVO> itemDividePromotions;

    private EvaluationDTO evaluationDTO;
}