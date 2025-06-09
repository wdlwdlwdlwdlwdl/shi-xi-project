package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderEvaluateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.*;
import com.aliyun.gts.gmall.platform.trade.domain.typehandler.MybatisArrayListTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName(value = "tc_order")
public class TcOrderDO implements Serializable {

    @ApiModelProperty("自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("主订单ID")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long primaryOrderId;

    @ApiModelProperty("对外展示订单id")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String displayOrderId;

    @ApiModelProperty("订单ID、订单ID可能是子订单也可能是主订单ID")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long orderId;

    @ApiModelProperty("是否主订单, PrimaryOrderFlagEnum ( 1主订单, 0子订单 ) ")
    private Integer primaryOrderFlag;

    @ApiModelProperty("业务身份，多值逗号分割")
    @TableField(typeHandler = MybatisArrayListTypeHandler.class)
    private List<String> bizCode;

    @ApiModelProperty("状态")
    private Integer orderStatus;

    @ApiModelProperty("主订单状态")
    private Integer primaryOrderStatus;

    @ApiModelProperty("订单快照，oss地址")
    private String snapshotPath;

    @ApiModelProperty("下单渠道")
    private String orderChannel;

    @ApiModelProperty("是否评价, 0未评价, 1已评价, 2已追评")
    private Integer evaluate = OrderEvaluateEnum.NOT_EVALUATE.getCode();

    @ApiModelProperty("售后类型")
    private Integer reversalType;

    @ApiModelProperty("买家id")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long custId;

    @ApiModelProperty("买家name")
    private String custName;

    @ApiModelProperty("用户下单备注")
    private String custMemo;

    @ApiModelProperty("商品id")
    private Long itemId;

    @ApiModelProperty("skuid")
    private Long skuId;

    @ApiModelProperty("sku描述")
    private String skuDesc;

    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty("商品数量")
    private Integer itemQuantity;

    @ApiModelProperty("商品主图oss地址")
    private String itemPic;

    @ApiModelProperty("IC原价的汇总金额")
    private Long orderPrice;

    @ApiModelProperty("IC的商品单价")
    private Long salePrice;

    @ApiModelProperty("订单实付现金金额（含优惠、含运费、扣除积分抵扣）")
    private Long realPrice;

    @ApiModelProperty("")
    private Date gmtCreate;

    @ApiModelProperty("")
    private Date gmtModified;

    @ApiModelProperty("订单扩展")
    private OrderAttrDO orderAttr;

    @ApiModelProperty("商品扩展字段")
    private OrderItemFeatureDO itemFeature;

    @ApiModelProperty("费用扩展字段")
    private OrderFeeAttrDO orderFeeAttr;

    @ApiModelProperty("营销扩展字段")
    private PromotionAttrDO promotionAttr;

    @ApiModelProperty("卖家id")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long sellerId;

    @ApiModelProperty("卖家名称")
    private String sellerName;

    @ApiModelProperty("收货信息")
    private ReceiverInfoDO receiveInfo;

    @ApiModelProperty("发货信息")
    private SalesInfoDO salesInfo;

    @ApiModelProperty("买家删除")
    private Integer custDeleted;

    @ApiModelProperty("版本号")
    @TableField(update = "%s+1", updateStrategy = FieldStrategy.IGNORED)
    private Long version;

    @ApiModelProperty("BIN或者IN")
    private String binOrIin;

    @ApiModelProperty("名")
    private String firstName;

    @ApiModelProperty("姓")
    private String lastName;

    @ApiModelProperty("贷款周期")
    private Integer loanCycle;

    @ApiModelProperty("取消原因")
    private String cancelCode;

    @ApiModelProperty("支付凭证cartId")
    private String payCartId;

    @ApiModelProperty(value = "佣金费率")
    private Integer categoryCommissionRate;

    @ApiModelProperty("开始支付时间")
    private Date toPayDate;

    @ApiModelProperty(value = "支付状态")
    private Integer payStatus;

    @ApiModelProperty("支付时间")
    private Date paymentDate;

    @ApiModelProperty("支付货币单位")
    private String currency;

    @ApiModelProperty(value = "是否交货")
    private Integer orderDelivered;

    @ApiModelProperty("订单实际完成时间")
    private Date orderDeliveryDate;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", primaryOrderId=" + primaryOrderId +
                ", orderId=" + orderId +
                ", primaryOrderFlag=" + primaryOrderFlag +
                ", bizCode=" + bizCode +
                ", orderStatus=" + orderStatus +
                ", primaryOrderStatus=" + primaryOrderStatus +
                ", snapshotPath=" + snapshotPath +
                ", orderChannel=" + orderChannel +
                ", evaluate=" + evaluate +
                ", reversalType=" + reversalType +
                ", custId=" + custId +
                ", custName=" + custName +
                ", custMemo=" + custMemo +
                ", itemId=" + itemId +
                ", skuId=" + skuId +
                ", skuDesc=" + skuDesc +
                ", itemTitle=" + itemTitle +
                ", itemQuantity=" + itemQuantity +
                ", itemPic=" + itemPic +
                ", orderPrice=" + orderPrice +
                ", salePrice=" + salePrice +
                ", realPrice=" + realPrice +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", orderAttr=" + orderAttr +
                ", itemFeature=" + itemFeature +
                ", orderFeeAttr=" + orderFeeAttr +
                ", promotionAttr=" + promotionAttr +
                ", sellerId=" + sellerId +
                ", sellerName=" + sellerName +
                ", version=" + version +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", loanCycle=" + loanCycle +
                ", displayOrderId=" + displayOrderId +
                "]";
        return sb;
    }
}