package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.item.dto.utils.DetailShowType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 商品详情按钮控制
 * @author tiansong
 */
@Data
@Builder
@ApiModel("商品详情按钮控制")
public class ItemDetailButtonVO extends AbstractOutputInfo {
    @ApiModelProperty("加入购物车")
    private Boolean addCart;
    @ApiModelProperty("立即下单")
    private Boolean toBuy;


    /**
     * 构建
     * @param showType
     * @return
     */
    public void setButtonValue(DetailShowType showType) {
        if(showType == null){
            return;
        }
        this.setAddCart(showType.getAddCart());
        this.setToBuy(showType.getToBuy());
    }

}
