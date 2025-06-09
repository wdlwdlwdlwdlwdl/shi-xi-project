package com.aliyun.gts.gmall.manager.front.trade.dto.output.cart;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 购物车分页请求结果
 *
 * @author tiansong
 */
@ApiModel(description = "购物车分页请求结果")
@Data
public class CartVO extends AbstractOutputInfo {
    @ApiModelProperty(value = "购物车商品分组", required = true)
    private List<CartGroupVO> groups;

    @ApiModelProperty(value = "购物车商品总量", required = true)
    private Integer totalItemCount;

    @ApiModelProperty("是否还有下一页,默认有下一页")
    private Boolean hasNext;
}
