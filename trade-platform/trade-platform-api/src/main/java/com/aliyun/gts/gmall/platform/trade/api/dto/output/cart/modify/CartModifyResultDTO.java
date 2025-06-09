package com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.modify;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CartModifyResultDTO extends AbstractOutputInfo {

    @ApiModelProperty("是否有SKU合并")
    private boolean skuMerged;

    @ApiModelProperty("修改后的加购数量, 含合并的情况")
    private Integer quantity;

    @ApiModelProperty("修改后的商品")
    private CartItemDTO cartItem;
}
