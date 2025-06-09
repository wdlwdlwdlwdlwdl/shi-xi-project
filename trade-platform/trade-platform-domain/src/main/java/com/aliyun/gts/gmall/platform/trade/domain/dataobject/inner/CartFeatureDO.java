package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CartFeatureDO extends ExtendComponent {

    @ApiModelProperty("加购时的商品标题")
    private String itemTitle;

    @ApiModelProperty("加购时的商品图片")
    private String itemPic;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty("加购时的SKU描述")
    private String skuDesc;

    @ApiModelProperty("加入购物车选择的商家名称")
    private String sellerName;

    @ApiModelProperty("加购时商品原始价格")
    private Long addOriginPrice;

    @ApiModelProperty("加购installment 期限")
    private String addInstallment;

    @ApiModelProperty("加购loan 期限")
    private String addLoan;


}
