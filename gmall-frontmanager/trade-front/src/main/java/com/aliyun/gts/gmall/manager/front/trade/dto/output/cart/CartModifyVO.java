package com.aliyun.gts.gmall.manager.front.trade.dto.output.cart;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("购物车商品修改结果")
@Data
public class CartModifyVO extends AbstractOutputInfo {

    @ApiModelProperty("修改后的商品")
    private CartItemVO cartItem;

    @ApiModelProperty("分组")
    private List<CartGroupVO> cartGroupVOS;

    @ApiModelProperty("是否有SKU合并")
    private boolean skuMerged;
}
