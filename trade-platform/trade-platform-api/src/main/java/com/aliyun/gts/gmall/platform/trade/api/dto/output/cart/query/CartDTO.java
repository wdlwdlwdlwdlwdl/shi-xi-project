package com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CartDTO extends AbstractOutputInfo {

    @ApiModelProperty("购物车商品分组")
    private List<CartGroupDTO> groups;

    @ApiModelProperty("购物车商品总量")
    private Integer totalItemCount;
}
