package com.aliyun.gts.gmall.manager.front.common.dto;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 查询购物车商品数量
 * @author tiansong
 */
@ApiModel("查询购物车商品数量")
public class CartItemQtyRestQuery extends AbstractQueryRestRequest {
    @Override
    public void checkInput() {
        super.checkInput();
    }

    @JsonIgnore
    private Long custId;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? custId : user.getCustId();
    }

    public void setCustId(Long v) {
        custId = v;
    }

    public Integer getCartType() {
        return cartType;
    }

    public void setCartType(Integer cartType) {
        this.cartType = cartType;
    }
}
