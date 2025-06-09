package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.CartFeatureDO;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_cart")
public class TcCartDO implements Serializable {

    public static final int CART_TYPE_DEFAULT = 0;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("客户ID")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long custId;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("SKUID")
    private Long skuId;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("加购数量")
    private Integer quantity;

    @ApiModelProperty("加购时间")
    private Date addCartTime;

    @ApiModelProperty("加购价格")
    private Long addCartPrice;

    @ApiModelProperty("0: 默认购物车,  其他: 业务购物车")
    private Integer cartType;

    @ApiModelProperty("扩展KV")
    private CartFeatureDO features;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", custId=" + custId +
                ", itemId=" + itemId +
                ", skuId=" + skuId +
                ", sellerId=" + sellerId +
                ", quantity=" + quantity +
                ", addCartTime=" + addCartTime +
                ", addCartPrice=" + addCartPrice +
                ", cartType=" + cartType +
                ", features=" + features +
                "]";
        return sb;
    }
}